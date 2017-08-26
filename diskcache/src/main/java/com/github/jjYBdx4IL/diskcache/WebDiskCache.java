/*
 * Copyright © 2016 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.diskcache;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

//CHECKSTYLE:OFF
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class WebDiskCache extends DiskCache {

    private static final Logger LOG = LoggerFactory.getLogger(WebDiskCache.class);

    private final HttpClient httpclient = HttpClients.createDefault();

    public WebDiskCache(File parentDir, String dbName) {
        super(parentDir, dbName);
    }

    public WebDiskCache(String dbName) {
        super(dbName);
    }

    public WebDiskCache(File parentDir, String dbName, boolean reinit) {
        super(parentDir, dbName, reinit);
    }

    public byte[] getCached(URL url) throws IOException {
        return get(url.toExternalForm());
    }

    public byte[] getCached(URL url, long _expiryMillis) throws IOException {
        return get(url.toExternalForm(), _expiryMillis);
    }

    public InputStream getCachedStream(URL url) throws IOException {
        return getStream(url.toExternalForm());
    }

    public InputStream getCachedStream(URL url, long _expiryMillis) throws IOException {
        return getStream(url.toExternalForm(), _expiryMillis);
    }

    public byte[] retrieve(URL url) throws IOException {
        return retrieve(url, this.expiryMillis);
    }

    public byte[] retrieve(URL url, long _expiryMillis) throws IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();
                InputStream is = retrieveStream(url, _expiryMillis)) {
            IOUtils.copyLarge(is, os);
            return os.toByteArray();
        }
    }

    public InputStream retrieveStream(URL url, long _expiryMillis) throws IOException {

        InputStream is = getStream(url.toExternalForm(), _expiryMillis);
        if (is != null) {
            LOG.debug("returning cached data for " + url.toExternalForm());
            return is;
        }

        LOG.debug("retrieving " + url.toExternalForm());

        HttpGet httpGet = new HttpGet(url.toExternalForm());
        HttpResponse response = httpclient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new IOException("url returned status code " + response.getStatusLine().getStatusCode() + ": " + url.toExternalForm());
        }
        try (InputStream is2 = response.getEntity().getContent()) {
            put(url.toExternalForm(), is2);
        }

        return getStream(url.toExternalForm(), -1L);
    }

    public byte[] retrieve(String url) throws IOException {
        return retrieve(new URL(url), this.expiryMillis);
    }

    public byte[] retrieve(String url, long _expiryMillis) throws IOException {
        return retrieve(new URL(url), _expiryMillis);
    }

}
