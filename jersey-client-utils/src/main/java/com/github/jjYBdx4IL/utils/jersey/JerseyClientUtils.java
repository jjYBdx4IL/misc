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
package com.github.jjYBdx4IL.utils.jersey;

import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.logging.LoggingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.ws.rs.client.Client;

public class JerseyClientUtils {

    private static final Logger LOG = LoggerFactory.getLogger(JerseyClientUtils.class);

    /**
     * Creates a usable Jersey REST client.
     * 
     * <p>"Usable" means:
     * <ul>
     * <li>Intercepts logging and redirects it to the slf4j framework.
     * <li>Makes the client use the {@link ApacheConnectorProvider} which adds Cookie handling support.
     * </ul>
     * 
     * <p>Does not use the generic REST client factory and forces the client to be from the Jersey implementation.
     * 
     * @return the client
     */
    public static Client createClient() {
        java.util.logging.Logger logj = java.util.logging.Logger.getLogger(JerseyClientUtils.class.getName());
        // add cookie handling support:
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new ApacheConnectorProvider());
        logj.setLevel(java.util.logging.Level.FINEST);
        logj.addHandler(new Handler() {

            @Override
            public void publish(LogRecord record) {
                LOG.info(record.getSourceClassName() + System.lineSeparator() + record.getMessage());
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        });
        logj.log(java.util.logging.Level.FINEST, "test");
        Client client = JerseyClientBuilder.createClient(clientConfig);
        client.register(new LoggingFeature(logj, LoggingFeature.Verbosity.PAYLOAD_ANY));
        return client;
    }

}
