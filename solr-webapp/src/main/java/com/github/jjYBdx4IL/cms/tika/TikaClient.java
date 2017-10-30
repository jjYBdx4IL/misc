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
package com.github.jjYBdx4IL.cms.tika;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletResponse;

public class TikaClient {

    private static final Logger LOG = LoggerFactory.getLogger(TikaClient.class);

    public static final int TIKA_SERVER_PORT = 9998;
    public static final String TIKA_SERVER_URL = "http://localhost:" + TIKA_SERVER_PORT;

    private TikaClient() {
    }

    public static MetaReply parse(byte[] data) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            MetaReply reply = null;
            try (InputStream is = new ByteArrayInputStream(data)) {
                HttpPut httpPut = new HttpPut(TIKA_SERVER_URL + "/meta");
                httpPut.setEntity(new InputStreamEntity(is));
                httpPut.setHeader("Content-Type", "text/html");
                httpPut.setHeader("Accept", "application/json;charset=UTF-8");
                try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
                    int status = response.getStatusLine().getStatusCode();
                    if (422 == status) {
                        LOG.info(response.getStatusLine().getReasonPhrase());
                        return null;
                    }
                    if (HttpServletResponse.SC_OK != status) {
                        throw new IOException(
                            "invalid status code " + status + " received for " + httpPut.getURI().toString());
                    }
                    try (InputStream is2 = response.getEntity().getContent()) {
                        try (InputStreamReader reader = new InputStreamReader(is2)) {
                            JsonParser parser = new JsonParser();
                            JsonElement root = parser.parse(reader);
                            try {
                                reply = new GsonBuilder()
                                    .registerTypeAdapter(String.class, new StringArrayDeserializer()).create()
                                    .fromJson(root, MetaReply.class);
                            } catch (JsonSyntaxException ex) {
                                LOG.info("bad json: " + root.toString());
                                throw ex;
                            }
                            checkNotNull(reply);
                        }
                    }
                }
            }
            try (InputStream is = new ByteArrayInputStream(data)) {
                HttpPut httpPut = new HttpPut(TIKA_SERVER_URL + "/tika");
                httpPut.setEntity(new InputStreamEntity(is));
                httpPut.setHeader("Content-Type", "text/html");
                httpPut.setHeader("Accept", "text/plain; charset=UTF-8");
                try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
                    int status = response.getStatusLine().getStatusCode();
                    if (HttpServletResponse.SC_OK != status) {
                        throw new IOException(
                            "invalid status code " + status + " received for " + httpPut.getURI().toString());
                    }
                    reply.setParsedContent(IOUtils.toString(response.getEntity().getContent(), "UTF-8"));
                }
            }
            return reply;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
