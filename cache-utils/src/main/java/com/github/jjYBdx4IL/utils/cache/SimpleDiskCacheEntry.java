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
package com.github.jjYBdx4IL.utils.cache;

//CHECKSTYLE:OFF
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class SimpleDiskCacheEntry {

    public static final String APP_NAME = "SimpleDiskCache";
    public static final String PROPNAME_CACHE_DIR = APP_NAME + ".dir";
    private static final Logger log = LoggerFactory.getLogger(SimpleDiskCacheEntry.class);
    public static final UpdateMode DEFAULT_UPDATE_MODE = UpdateMode.DAILY;
    private final File localCacheFile;
    private final URL url;
    private final String urlMD5;
    private final UpdateMode updateMode;

    public SimpleDiskCacheEntry(URL url, File localCacheFile, UpdateMode updateMode)
            throws MalformedURLException {
        this.url = new URL(url.toString());
        this.urlMD5 = md5();
        this.localCacheFile = localCacheFile != null ? new File(localCacheFile.getPath()) : computeDiskCacheFile(url);
        this.updateMode = updateMode;
    }

    public SimpleDiskCacheEntry(URL url, UpdateMode updateMode) throws MalformedURLException {
        this(url, null, updateMode);
    }

    public SimpleDiskCacheEntry(URL url) throws MalformedURLException {
        this(url, null, DEFAULT_UPDATE_MODE);
    }

    public SimpleDiskCacheEntry(String url, File localCacheFile, UpdateMode updateMode)
            throws MalformedURLException {
        this(new URL(url), localCacheFile, updateMode);
    }

    public SimpleDiskCacheEntry(String url, UpdateMode updateMode) throws MalformedURLException {
        this(new URL(url), null, updateMode);
    }

    public SimpleDiskCacheEntry(String url) throws MalformedURLException {
        this(new URL(url), null, DEFAULT_UPDATE_MODE);
    }

    private File getCacheDir() {
        String propCacheDir = System.getProperty(PROPNAME_CACHE_DIR);
        if (propCacheDir != null) {
            return new File(propCacheDir);
        }
        String userHome = System.getProperty("user.home");
        if (userHome == null || userHome.length() == 0) {
            throw new IllegalArgumentException("environment variable user home is not set or empty");
        }
        return new File(new File(userHome, ".cache"), APP_NAME);
    }

    private File computeDiskCacheFile(URL url) {
        return new File(getCacheDir(), urlMD5);
    }

    private String md5() {
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(url.toExternalForm().getBytes(Charset.forName("UTF8")));
            final byte[] resultByte = messageDigest.digest();
            return Hex.encodeHexString(resultByte);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }

    protected boolean isUpdateRequired() {
        if (!localCacheFile.exists() || updateMode.equals(UpdateMode.ALWAYS)) {
            log.debug("local copy not found");
            return true;
        }
        SimpleDiskCacheEntryHeader header;
        // hint: do NOT use try-with-resources directly on the ObjectInputStream
        // -- it won't close the underlying FileInputStream reliably....
        try (InputStream is = new FileInputStream(localCacheFile)) {
        	ObjectInputStream ois = new ObjectInputStream(is);
            header = (SimpleDiskCacheEntryHeader) ois.readObject();
            log.debug(header.toString());
        } catch (IOException | ClassNotFoundException | ClassCastException ex) {
            log.warn("cached file's header is corrupt, discarding cached file", ex);
            return true;
        }
        if (log.isDebugEnabled()) {
            log.debug("getUrl() = " + getUrl().toExternalForm());
        }
        if (!getUrl().toExternalForm().equals(header.getUrl())) {
            log.debug("url changed, discarding cached file");
            return true;
        }
        if (updateMode.equals(UpdateMode.NEVER)) {
            log.debug("update mode NEVER, using cached file");
            return false;
        }
        if (header.getAgeSeconds() > updateMode.getExpireSeconds()) {
            log.debug("local copy too old, discarding cached file");
            return true;
        }
        log.debug("using cached file");
        return false;
    }

    /**
     * @return the localCacheFile
     */
    protected File getLocalCacheFile() {
        return localCacheFile;
    }

    /**
     * @return the url
     */
    protected URL getUrl() {
        return url;
    }

    public InputStream getInputStream() throws IOException {
        return getInputStream(false);
    }

    public InputStream getInputStream(boolean allowFallback) throws IOException {
        log.debug("getInputStream " + getLocalCacheFile().getPath());
        if (isUpdateRequired()) {
            try {
                File localTempFile = new File(getLocalCacheFile().getPath() + ".tmp");
                if (localTempFile.exists() && !localTempFile.delete()) {
                    throw new RuntimeException("failed to delete " + localTempFile.getAbsolutePath());
                }
                localTempFile.deleteOnExit();

                File parentDir = localTempFile.getParentFile();
                if (!parentDir.exists()) {
                    parentDir.mkdirs();
                }
                if (!parentDir.exists()) {
                    throw new IOException("failed to create parent directory " + parentDir.getAbsolutePath());
                }

                log.debug("downloading " + getUrl().toString() + " to " + localTempFile.getPath());

                try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                    HttpGet httpGet = new HttpGet(getUrl().toExternalForm());
                    try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                        int sc = response.getStatusLine().getStatusCode();
                        if (sc != 200) {
                            throw new IOException(String.format("%s (%d)",
                                    response.getStatusLine().getReasonPhrase(), sc));
                        }
                        try (FileOutputStream fos = new FileOutputStream(localTempFile)) {
                            try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                                oos.writeObject(new SimpleDiskCacheEntryHeader(getUrl()));
                                IOUtils.copyLarge(response.getEntity().getContent(), fos);
                            }
                        }
                    }
                }
                if (getLocalCacheFile().exists() && !getLocalCacheFile().delete()) {
                    throw new RuntimeException("failed to delete " + getLocalCacheFile().getAbsolutePath());
                }
                FileUtils.moveFile(localTempFile, getLocalCacheFile());
            } catch (IOException ex) {
                if (!allowFallback || !getLocalCacheFile().exists()) {
                    throw new IOException(ex);
                }
                log.warn("download of " + url.toExternalForm() + " failed, returning expired cache entry instead");
            }
        }

        log.debug("returning file input stream for " + getLocalCacheFile().getPath());
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(getLocalCacheFile());
            ObjectInputStream ois = new ObjectInputStream(fis);
            SimpleDiskCacheEntryHeader header = (SimpleDiskCacheEntryHeader) ois.readObject();
            if (!getUrl().toExternalForm().equals(header.getUrl())) {
                throw new IOException("race detected while accessing cache file");
            }
            return fis;
        } catch (Exception ex) {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex2) {
                }
            }
            throw new IOException(ex);
        }
    }

    public enum UpdateMode {

        // CHECKSTYLE IGNORE MagicNumber FOR NEXT 1 LINE
        NEVER(-1), ALWAYS(0), HOURLY(3600), DAILY(3600 * 24), WEEKLY(3600 * 24 * 7);
        private final long expireMillis;

        UpdateMode(int expireSeconds) {
            // CHECKSTYLE IGNORE MagicNumber FOR NEXT 1 LINE
            this.expireMillis = expireSeconds * 1000L;
        }

        public long getExpireMillis() {
            return this.expireMillis;
        }

        public long getExpireSeconds() {
            return this.expireMillis / 1000L;
        }
    }
}
