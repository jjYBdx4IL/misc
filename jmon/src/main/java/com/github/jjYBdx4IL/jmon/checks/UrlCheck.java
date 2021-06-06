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
package com.github.jjYBdx4IL.jmon.checks;

import static com.github.jjYBdx4IL.utils.text.StringUtil.f;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.github.jjYBdx4IL.jmon.Config;
import com.github.jjYBdx4IL.jmon.dto.HostDef;
import com.github.jjYBdx4IL.jmon.dto.ServiceDef;
import com.github.jjYBdx4IL.utils.io.IoUtils;
import com.github.jjYBdx4IL.utils.lang.ArrayUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class UrlCheck extends CheckBase implements ICheck {

    private static final Logger LOG = LoggerFactory.getLogger(UrlCheck.class);

    private final URL url;
    private final byte[] expectedContent;
    private final boolean truststore;

    public UrlCheck(HostDef hostDef, ServiceDef serviceDef) {
        super(hostDef, serviceDef);

        String path = "/";
        String expContent = null;
        Conf conf = null;
        if (serviceDef.conf != null) {
            conf = Config.gson.fromJson(serviceDef.conf, Conf.class);
        }
        if (conf != null) {
            if (conf.path != null) {
                path = conf.path;
            }
            if (conf.content != null && !conf.content.isEmpty()) {
                expContent = conf.content;
            }
            truststore = conf.truststore;
        } else {
            truststore = false;
        }
        
        if (!path.isEmpty() && path.startsWith("/")) {
            path = "/" + path;
        }

        try {
            if (conf != null && conf.port != 0) {
                url = new URL(f("https://%s:%d%s", serviceDef.hostDef.hostname, conf.port, path));
            } else {
                url = new URL(f("https://%s%s", serviceDef.hostDef.hostname, path));
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        expectedContent = expContent != null ? expContent.getBytes(UTF_8) : null;
    }

    @Override
    public CheckResult execute() throws Exception {
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        if (truststore) {
            conn.setSSLSocketFactory(Config.sslSocketFactory);
        }
        try (InputStream is = conn.getInputStream()) {
            byte[] content = IoUtils.toByteArray(is, Config.GENERIC_MAX_RETRIEVAL_SIZE);

            if (expectedContent != null && !ArrayUtils.contains(content, expectedContent)) {
                return new CheckResult(f("%s not found in page %s", expectedContent, url.toExternalForm()), 2);
            }

            return new CheckResult();
        } catch (Exception ex) {
            LOG.warn("", ex);
            throw ex;
        }
    }

    public static String help() {
        return "{\"path\":\"/\", \"content\":\"expected content or null\",\n"
            + "  \"port\":443, - use this remote port\n"
            + "  \"truststore\":false} - use server truststore?";
    }

    public static class Conf {
        public String path;
        public String content;
        public boolean truststore;
        public int port;
    }
}
