/*
 * Copyright © 2017 jjYBdx4IL (https://github.com/jjYBdx4IL)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jjYBdx4IL.jmon;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.github.jjYBdx4IL.jmon.checks.ICheck;
import com.github.jjYBdx4IL.jmon.dto.HostDef;
import com.github.jjYBdx4IL.jmon.dto.HostDef.MergeException;
import com.github.jjYBdx4IL.jmon.dto.HostState;
import com.github.jjYBdx4IL.jmon.dto.ServiceDef;
import com.github.jjYBdx4IL.jmon.dto.ServiceState;
import com.github.jjYBdx4IL.jmon.dto.ServiceStateXfer;
import com.google.gson.JsonSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class Model implements IServiceStateReceiver, IShutdownHandler {

    private static final Logger LOG = LoggerFactory.getLogger(Model.class);

    private final Map<String, HostDef> hostDefs = new HashMap<>();
    private final Map<String, HostState> hostStates = new HashMap<>();

    // check name -> ctor
    private final Map<String, Constructor<?>> checkConstructors = new HashMap<>();

    private ReporterThread notificationThread = null;

    public long loadedServicesCounter = 0;

    public Model() {
    }

    public void setReporterThread(ReporterThread notificationThread) {
        this.notificationThread = notificationThread;
    }

    public void init() throws Exception {
        scan();
        load();
    }

    private void scan() throws ClassNotFoundException {
        Set<String> klazznames = Utils.scanChecks();

        for (String checkCn : klazznames) {
            Constructor<?> ctor = null;
            Class<?> klazz = Class.forName(checkCn);
            for (Constructor<?> ctr : klazz.getDeclaredConstructors()) {
                Type[] types = ctr.getGenericParameterTypes();
                if (types.length == 2 && types[0].equals(HostDef.class) && types[1].equals(ServiceDef.class)) {
                    ctor = ctr;
                    break;
                }
            }
            checkNotNull(ctor);

            String checkName = klazz.getSimpleName().toLowerCase(Locale.ROOT);
            if (checkConstructors.put(checkName, ctor) != null) {
                throw new RuntimeException("duplicate check name: " + checkName);
            }
        }
    }

    private void load() throws IOException {
        LOG.debug("loading host definitions ({})", Config.cfgDir);
        Files.list(Config.cfgDir).filter(p -> !Files.isDirectory(p)).filter(p -> p.toFile().getName().endsWith(".def"))
            .forEach(p -> load(p));
        LOG.debug("imported {} service definitions distributed over {} host definitions",
            loadedServicesCounter, hostDefs.size());
        checkArgument(!hostDefs.isEmpty(), "no host definitions found");
        checkArgument(loadedServicesCounter > 0, "no service definitions found");
        checkArgument(!hostStates.isEmpty());
    }

    private void load(Path hostdefLoc) {
        LOG.debug("loading host definition ({})", hostdefLoc);
        String hostname = hostdefLoc.toFile().getName();
        hostname = hostname.substring(0, hostname.length() - 4);

        String json = null;
        try {
            json = Files.readString(hostdefLoc);
            HostDef hostdef = Config.gson.fromJson(json, HostDef.class);

            // default hostname derived from filename:
            if (hostdef.hostname == null || hostdef.hostname.isBlank()) {
                hostdef.hostname = hostname;
            }

            processIncludes(hostdef, new LinkedHashSet<Path>(Arrays.asList(hostdefLoc)));

            hostdef.fillInServiceNames();
            hostdef.fillInServiceHostDefs(hostdef);
            hostdef.fillInDefaults();
            hostdef.validate();
            LOG.debug("imported hostdef: {}", hostdef);
            hostDefs.put(hostdef.hostname, hostdef);

            Path stateLoc = Config.cfgDir.resolve(hostname + ".state");
            HostState hs;
            if (Files.exists(stateLoc)) {
                hs = Config.gson.fromJson(Files.readString(stateLoc), HostState.class);
            } else {
                hs = new HostState(hostdef.hostname);
            }
            pruneAndAugment(hs, hostdef);
            hs.hostname = hostdef.hostname;
            hs.fillInServiceDefs(hostdef);
            hs.fillInHostState();
            hs.services.values().stream().filter(s -> s.status > 1)
                .forEach(s -> notificationThread.addReport(s));
            hostStates.put(hostdef.hostname, hs);

            loadedServicesCounter += hostdef.services.size();
        } catch (Exception e) {
            if (json != null && e instanceof JsonSyntaxException) {
                LOG.error("bad json syntax: {}", json);
            }
            throw new RuntimeException(e);
        }
    }

    // included files can be host
    private void processIncludes(HostDef hostdef, LinkedHashSet<Path> loopDetect)
        throws JsonSyntaxException, IOException {
        LOG.trace("processIncludes: {}", loopDetect);
        if (hostdef.includes == null) {
            return;
        }
        for (String includeLoc : hostdef.includes) {
            Path includePath = Config.cfgDir.resolve(includeLoc);
            if (loopDetect.contains(includePath)) {
                throw new RuntimeException("include loop detected: trying to include \"" + includePath
                    + "\", current include sequence is: " + loopDetect);
            }
            HostDef ihd = Config.gson.fromJson(Files.readString(includePath), HostDef.class);
            loopDetect.add(includePath);
            processIncludes(ihd, loopDetect);
            loopDetect.remove(includePath);

            // merge service defs
            try {
                hostdef.addServices(ihd);
            } catch (MergeException e) {
                throw new RuntimeException(loopDetect.toString(), e);
            }
        }
    }

    private void pruneAndAugment(HostState hostState, HostDef hostdef) {
        // remove stale host state entries
        hostState.services.keySet().retainAll(hostdef.services.keySet());
        // augment missing host state entries
        hostdef.services.keySet().forEach(s -> {
            if (!hostState.services.containsKey(s)) {
                hostState.services.put(s, new ServiceState(hostdef.services.get(s), hostState));
            }
        });
    }

    // passive check result submission handler (used by RequestHandler)
    @Override
    public boolean handle(String hostname, ServiceStateXfer ssx) {
        ssx.validate();

        HostDef hd = hostDefs.get(hostname);
        if (hd == null) {
            LOG.error("bad hostname submitted for passive check: {}", hostname);
            return false;
        }
        HostState hs = hostStates.get(hostname);
        checkNotNull(hs);
        ServiceDef sdef = hd.services.get(ssx.service);
        if (sdef == null) {
            LOG.error("bad service name submitted for passive check: {}", ssx.toString());
            return false;
        }

        ServiceState serviceState = hs.services.get(ssx.service);
        checkNotNull(serviceState);

        synchronized (serviceState) {
            synchronized (serviceState.hostState) {
                final boolean wasProblem = serviceState.status > 1;
                final boolean isProblem = ssx.state > 1;

                serviceState.millisSinceEpoch = System.currentTimeMillis();
                serviceState.status = ssx.state;
                serviceState.msg = ssx.msg != null ? ssx.msg : "";

                serviceState.hostState.save(true);
                
                if (isProblem && !wasProblem) {
                    notificationThread.addReport(serviceState);
                } else if (!isProblem && wasProblem) {
                    notificationThread.removeReport(serviceState);
                }
            }
        }

        return true;
    }

    public void populateExecutionQueue(TreeSet<TimedExecution> queue) {
        for (Entry<String, HostDef> he : hostDefs.entrySet()) {
            HostDef hostdef = he.getValue();
            HostState hostState = hostStates.get(he.getKey());
            checkNotNull(hostState);
            for (Entry<String, ServiceDef> se : hostdef.services.entrySet()) {
                ServiceDef sd = se.getValue();
                ServiceState serviceState = hostState.services.get(se.getKey());
                checkNotNull(serviceState);
                if (sd.passive) {
                    queue.add(new TimedExecution(null, serviceState));
                } else {
                    Constructor<?> ctor = checkConstructors.get(sd.check);
                    try {
                        ICheck check = (ICheck) ctor.newInstance(hostdef, sd);
                        queue.add(new TimedExecution(check, serviceState));
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e1) {
                        throw new RuntimeException(e1);
                    }
                }
            }
        }
        LOG.debug("{} service executions queued", queue.size());
        if (queue.isEmpty() && !Config.cmd.hasOption(Config.OPT_IGNORENOACTIVECHECKS)) {
            throw new RuntimeException("no active checks configured");
        }
    }

    @Override
    public void shutdown() {
        hostStates.forEach((k, hs) -> hs.shutdown());
    }
}
