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
package com.github.jjYBdx4IL.cms;

import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.component.LifeCycle.Listener;
import org.h2.Driver;
import org.h2.engine.Constants;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Properties;

/**
 * After you set the "jdbc/url" jndi resource (type String, something like "jdbc:h2:/path/to/db"), add this class to the
 * Jetty server via {@link org.eclipse.jetty.server.Server#addManaged(org.eclipse.jetty.util.component.LifeCycle)}.
 *
 * @author jjYBdx4IL
 */
public class H2FrontendRunner implements Listener {

    private static final Logger LOG = LoggerFactory.getLogger(H2FrontendRunner.class);

    private Server h2FrontendServer = null;
    public static final int H2_FRONTEND_PORT = 8083;
    private final String jdbcUrl;

    public H2FrontendRunner(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }
    
    protected void doStart() throws Exception {
        LOG.info("starting h2 managment frontend");

        // inject connection settings into frontend config
        Properties webServerProps = new Properties();
        webServerProps.put("0", String.format(Locale.ROOT, "Generic H2 (Embedded)|%s|%s",
                Driver.class.getName(),
                jdbcUrl.replace("\\", "\\\\").replace(":", "\\:")));

        String dbLoc = jdbcUrl.replaceFirst("^[^:]*:[^:]*:", "");
        if (dbLoc.contains(";")) {
            dbLoc = dbLoc.substring(0, dbLoc.indexOf(";"));
        }
        if (dbLoc.contains("?")) {
            dbLoc = dbLoc.substring(0, dbLoc.indexOf("?"));
        }
        File dbDir = new File(dbLoc).getParentFile();
        // put h2 frontend config in same directory as the database:
        File h2FrontendConfigFile = new File(dbDir, Constants.SERVER_PROPERTIES_NAME);

        try (OutputStream os = new FileOutputStream(h2FrontendConfigFile)) {
            webServerProps.store(os, "");
        }

        // use -Dh2.bindAddress=localhost to force binding to your localhost interface!
        h2FrontendServer = new Server();
        h2FrontendServer.runTool(
                "-web",
                "-webPort", Integer.toString(H2_FRONTEND_PORT),
                "-ifExists",
                "-baseDir", dbDir.getAbsolutePath(),
                "-properties", dbDir.getAbsolutePath());

        LOG.info("H2 frontend available on localhost:" + H2_FRONTEND_PORT);
    }

    protected void doStop() throws Exception {
        LOG.info("stopping h2 managment frontend");
        if (h2FrontendServer != null) {
            h2FrontendServer.shutdown();
            h2FrontendServer = null;
        }
    }

    @Override
    public void lifeCycleStarting(LifeCycle event) {
        try {
            doStart();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void lifeCycleStarted(LifeCycle event) {
    }

    @Override
    public void lifeCycleFailure(LifeCycle event, Throwable cause) {
    }

    @Override
    public void lifeCycleStopping(LifeCycle event) {
    }

    @Override
    public void lifeCycleStopped(LifeCycle event) {
        try {
            doStop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
