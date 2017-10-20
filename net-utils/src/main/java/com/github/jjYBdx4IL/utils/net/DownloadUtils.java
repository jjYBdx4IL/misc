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
package com.github.jjYBdx4IL.utils.net;

import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

//CHECKSTYLE:OFF
public class DownloadUtils {

    private DownloadUtils() {
    }

    public static byte[] get(String url, long maxSize) throws IOException {
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30000).setConnectionRequestTimeout(30000)
            .setSocketTimeout(30000).build();
        
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(requestConfig);
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                int status = response.getStatusLine().getStatusCode();
                if (status == HttpStatus.SC_NOT_FOUND) {
                    throw new FileNotFoundException(url);
                }
                if (status < 200 || status >= 300) {
                    throw new IOException(
                        "invalid status code " + status + " received for " + url);
                }
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    byte[] buf = new byte[4096];
                    try (InputStream is = response.getEntity().getContent()) {
                        int n;
                        do {
                            n = is.read(buf);
                            if (n > 0) {
                                baos.write(buf, 0, n);
                            }
                        } while (baos.size() < maxSize && is.available() > 0);
                        if (n > 0) {
                            return null;
                        }
                    }
                    return baos.toByteArray();
                }
            }
        }
    }
}
