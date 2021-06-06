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

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class RunServer implements IExecModule {

    public final Server server = new Server();
    public final Model model;
    public final CheckThread checkThread;
    public final ReporterThread reporterThread;
    
    public RunServer() {
        model = new Model();
        checkThread = new CheckThread(model);
        reporterThread = new ReporterThread(model);
        model.setReporterThread(reporterThread);
        checkThread.setReporterThread(reporterThread);
    }

    protected void startServer() throws Exception {
        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());
        SslContextFactory.Server ssl = new SslContextFactory.Server();

        ssl.setCertAlias("server");
        ssl.setKeyStorePath(Config.ks.toString());
        ssl.setKeyStorePassword("password");

        ssl.setNeedClientAuth(true);
        ssl.setTrustStorePath(Config.ts.toString());
        ssl.setTrustStorePassword("password");

        ServerConnector sslConnector = new ServerConnector(server, new SslConnectionFactory(ssl, "http/1.1"),
            new HttpConnectionFactory(https));
        sslConnector.setPort(Config.port);

        server.setConnectors(new Connector[] { sslConnector });

        server.setHandler(new RequestHandler(model));

        server.start();
    }
    
    @Override
    public void exec() {
        try {
            model.init();
            checkThread.init();
            checkThread.start();
            reporterThread.start();
            startServer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void shutdown() {
        try {
            server.join();
            checkThread.shutdown();
            reporterThread.shutdown();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
