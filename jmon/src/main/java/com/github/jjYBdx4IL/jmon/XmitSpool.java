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

import static java.nio.charset.StandardCharsets.UTF_8;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request.Content;
import org.eclipse.jetty.client.util.StringRequestContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class XmitSpool implements IExecModule {

    private static final Logger LOG = LoggerFactory.getLogger(XmitSpool.class);

    @Override
    public void exec() {
        try {
            HttpClient c = Utils.getClient("client");
            // exclude temporary files written by IoUtils::safeWriteTo
            List<Path> files = Files.list(Config.spoolDir).filter(a -> !a.toFile().getName().startsWith("."))
                .collect(Collectors.toList());
            for (Path file : files) {
                String json = Files.readString(file);
                Content content = new StringRequestContent(RequestHandler.JSON_TYPE, json, UTF_8);
                ContentResponse response = c.POST(Config.spoolSendToUrl.toExternalForm()).body(content).send();
                if (response.getStatus() == 200) {
                    LOG.info("transmitted: {}", file);
                    Files.delete(file);
                } else {
                    LOG.error("transmission failure: {} - response was: {} - {}", file, response.getStatus(),
                        response.getReason());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void shutdown() {
        // not used
    }
}
