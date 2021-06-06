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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class SslTestServer {

    static final Logger LOG = LoggerFactory.getLogger(SslTestServer.class);

    public static final int port = 8441;
    
    public static Server server = new Server();

    public static void start(Path ks, Path ts) throws Exception {
        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());
        SslContextFactory.Server ssl = new SslContextFactory.Server();

        ssl.setCertAlias("testserver");
        ssl.setKeyStorePath(ks.toString());
        ssl.setKeyStorePassword("password");

        ssl.setNeedClientAuth(false); // assume checking publicly visible targets
        if (ts != null) {
            ssl.setTrustStorePath(ts.toString());
            ssl.setTrustStorePassword("password");
        }

        ServerConnector sslConnector = new ServerConnector(server, new SslConnectionFactory(ssl, "http/1.1"),
            new HttpConnectionFactory(https));
        sslConnector.setPort(port);

        server.setConnectors(new Connector[] { sslConnector });

        server.setHandler(new TestRequestHandler());

        server.start();
    }
}
