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
package com.github.jjYBdx4IL.jmon.dto;

import com.github.jjYBdx4IL.jmon.Config;
import com.github.jjYBdx4IL.jmon.IShutdownHandler;
import com.github.jjYBdx4IL.utils.io.IoUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class HostState implements IShutdownHandler {

    private static final Logger LOG = LoggerFactory.getLogger(HostState.class);
    public static final String fileExt = ".state";
    
    public Map<String, ServiceState> services = new HashMap<>();
    
    public transient String hostname;
    public transient long lastSaved = System.currentTimeMillis();
    private transient boolean dirty = false;
    
    public HostState(String name) {
        hostname = name;
    }

    public void fillInServiceDefs(HostDef hostdef) {
        services.forEach((k,v) -> {v.def = hostdef.services.get(k);});
    }

    public void fillInHostState() {
        services.forEach((k,v) -> {v.hostState = this;});
    }

    public synchronized void save(boolean force) {
        dirty = true;
        long now = System.currentTimeMillis();
        if (force || Config.hostSaveIvalMillis == 0 || lastSaved + Config.hostSaveIvalMillis <= now) {
            try {
                Path file = Config.cfgDir.resolve(hostname + fileExt);
                LOG.debug("saving {}", file);
                IoUtils.safeWriteTo(file, Config.gson.toJson(this));
                lastSaved = now;
                dirty = false;
            } catch (IOException e) {
                LOG.error("", e);
            }
        }
    }

    @Override
    public void shutdown() {
        if (dirty) {
            save(true);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("HostState [services=").append(services).append("]");
        return builder.toString();
    }
}
