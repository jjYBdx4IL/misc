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

import static com.github.jjYBdx4IL.utils.text.StringUtil.f;
import static com.google.common.base.Preconditions.checkArgument;

import com.google.gson.Gson;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import javax.net.ssl.SSLSocketFactory;

public class Config {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(Config.class);

    public static final long GENERIC_MAX_RETRIEVAL_SIZE = 1024 * 1024L;

    public static int port = 443;
    public static IProblemReporter reporter = new MailProblemReporter();
    
    public static final Gson gson = Utils.createGson();
    public static SSLSocketFactory sslSocketFactory = null;

    // 0 to save on every update
    public static long hostSaveIvalMillis = 60 * 1000L;

    public static Path cfgDir = Paths.get(System.getProperty("user.home"));
    public static Path ks;
    public static Path ts;

    // NOT used in server mode:
    public static final Path spoolDir = Paths.get("/var/spool/nagiosresdump");
    public static URL spoolSendToUrl = null;

    public static CommandLine cmd;

    public static final String OPT_HELP = "help";
    public static final String OPT_HELP_CONFIG = "helpConfig";
    public static final String OPT_CFGDIR = "cfgdir";
    public static final String OPT_IGNORENOACTIVECHECKS = "ignoreNoActiveChecks";
    public static final String OPT_ACTION_RUN_SERVER = "runServer";
    public static final String OPT_ACTION_STORE_IN_SPOOL = "storeInSpool";
    public static final String OPT_ACTION_XMIT_SPOOL = "xmitSpool";
    public static final String OPT_SPOOL_SEND_TO = "spoolSendTo";

    public static enum ACTION {
        RUN_SERVER,
        STORE_IN_SPOOL,
        XMIT_SPOOL
    }

    public static ACTION selectedAction = null;
    
    public static InstanceLock instanceLock = null;

    public static boolean parseCmdLine(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("h", OPT_HELP, false, "this help page");
        options.addOption(null, OPT_HELP_CONFIG, false, "config help page");
        options.addOption("c", OPT_CFGDIR, true, "configuration directory");
        options.addOption(null, OPT_IGNORENOACTIVECHECKS, false, "ignore if no active checks are configured");
        options.addOption(null, OPT_ACTION_RUN_SERVER, false, "server mode");
        options.addOption(null, OPT_ACTION_STORE_IN_SPOOL, true,
            "store mode - writes status update to spool directory (fmt: \"[012]:serviceName:message\")");
        options.addOption(null, OPT_ACTION_XMIT_SPOOL, false,
            "transmit spool mode - sends spooled status updates to server");
        options.addOption(null, OPT_SPOOL_SEND_TO, true,
            "name of jmon server where the spooled status updates will be sent");
        CommandLineParser parser = new DefaultParser();
        cmd = parser.parse(options, args);
        
        if (cmd.hasOption(OPT_HELP)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("jmon", options);
            return false;
        }
        if (cmd.hasOption(OPT_HELP_CONFIG)) {
            Set<String> klazznames = Utils.scanChecks();
            System.out.print("""
                == Host and services config ==

                Put host definitions into the directory specified by the --cfgdir option.
                For convenience you may name them "fqdn.def" where fqdn is the fully qualified
                hostname, ie. "www.google.de", or an IP address. The suffix ".def" is important.
                Alternatively, you may also use the optional hostname parameter that is part if the host
                definition itself (syntax is JSON (Gson without html escaping)):

                example.com.def:

                    {
                        "hostname": "example.com", // optional, will use filename minus .def by default
                        // merge services defined in another file (excl. defaults)
                        "includes": ["arbitrary other file (incl host .defs) given by relative path", ...],
                        "services": {
                            "arbitrary service name": {
                                "passive": true, // default: false
                                // for passive checks:
                                "timeout": "1w2d3h4m5s", // default 1h (1 hour)
                                // for active checks:
                                "check": "name of the service check, see below",
                                "conf": "...", // check-specific configuration, see below
                                "checkIval": "15m", // default 5m (5 minutes)
                                "retryIval": "1m", // default 1m
                                "tries": 4 // default 3
                            }
                        },
                        "defaults": { // section and entries are optional
                            // change defaults for this host:
                            "timeout": "6h",
                            "checkIval": "30m",
                            "retryIval": "2m",
                            "tries" : 5
                        }
                    }

                The "includes" parameter will include the services definition from other files. That
                way you can define basic service checks for generic host types.

                == Available Checks (<CheckName> : <conf>) ==

                """);
            for (String cn : klazznames) {
                try {
                    Class<?> klazz = Class.forName(cn);
                    System.out.print(klazz.getSimpleName());
                    System.out.print(" : ");
                    Method m = klazz.getMethod("help");
                    if (!m.getReturnType().equals(String.class)) {
                        throw new NoSuchMethodException("wrong return type");
                    }
                    String s = (String) m.invoke(null);
                    if (s == null) {
                        throw new NoSuchMethodException("null returned");
                    }
                    System.out.println(s);
                } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException e) {
                    System.out.println("no help text found!");
                }
            }
            return false;
        }
        
        if (cmd.hasOption(OPT_ACTION_RUN_SERVER)) {
            selectedAction = ACTION.RUN_SERVER;
        }
        if (cmd.hasOption(OPT_ACTION_STORE_IN_SPOOL)) {
            if (selectedAction != null) {
                System.out.println(f("multiple actions selected: %s", OPT_ACTION_STORE_IN_SPOOL));
                return false;
            }
            selectedAction = ACTION.STORE_IN_SPOOL;
        }
        if (cmd.hasOption(OPT_ACTION_XMIT_SPOOL)) {
            if (selectedAction != null) {
                System.out.println(f("multiple actions selected: %s", OPT_ACTION_XMIT_SPOOL));
                return false;
            }
            selectedAction = ACTION.XMIT_SPOOL;
        }
        
        if (selectedAction == null) {
            System.out.println("ERROR: no operation mode selected.");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("jmon", options);
            return false;
        }
        
        if (cmd.hasOption(OPT_CFGDIR)) {
            cfgDir = Paths.get(cmd.getOptionValue(OPT_CFGDIR));
        }
        ks = cfgDir.resolve("keystore");
        ts = cfgDir.resolve("truststore");

        sslSocketFactory = Utils.createSSLSocketFactory(ts);

        if (selectedAction.equals(ACTION.XMIT_SPOOL) || selectedAction.equals(ACTION.STORE_IN_SPOOL)) {
            checkArgument(Files.exists(spoolDir), "directory missing: %s", spoolDir);
        }
        
        if (cmd.hasOption(OPT_SPOOL_SEND_TO)) {
            spoolSendToUrl = new URL(cmd.getOptionValue(OPT_SPOOL_SEND_TO));
        }
        
        if (selectedAction.equals(ACTION.XMIT_SPOOL)) {
            if (spoolSendToUrl == null) {
                System.out.println("The selected action requires the " + OPT_SPOOL_SEND_TO + " argument.");
                return false;
            }
        }
        
        if (selectedAction.equals(ACTION.RUN_SERVER)) {
            if (!Files.exists(ks)) {
                System.out.println("no keystore found: " + ks.toString());
                return false;
            }
            if (!Files.exists(ts)) {
                System.out.println("no truststore found: " + ts.toString());
                return false;
            }
        }
        
        return true;
    }

}
