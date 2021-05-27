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
package com.github.jjYBdx4IL.utils.io;

import org.apache.commons.lang3.SystemUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class DlCache {

    private static final int BUF_LEN = 4096;

    private static Path getLoc(URL url) {
        String s = url.toExternalForm().replaceAll("[^A-Za-z0-9/.-]", "_");
        Path root;
        if (SystemUtils.IS_OS_WINDOWS) {
            root = Paths.get(System.getenv("LOCALAPPDATA"), "dlcache");
        } else {
            root = Paths.get(System.getenv("HOME"), ".cache", "dlcache");
        }
        if (root.toFile().exists() && !root.toFile().isDirectory()) {
            throw new RuntimeException(root.toString() + " is not a directory");
        }
        return root.resolve(s);
    }

    /**
     * Fetches a URL and caches it locally (Windows: %LOCALAPPDATA%\dlcache, rest:
     * $HOME/.cache/dlcache). If a checksum is given, the file will be verified once
     * after download and before moving it into its final cache location. See also:
     * https://github.com/jjYBdx4IL/dlcache
     * 
     * @param url    the url to download
     * @param sha256 SHA-256 digest
     * @param sha512 SHA-512 digest
     * @return the file input stream
     * @throws IOException any error or checksum mismatch
     */
    public static FileInputStream get(URL url, String sha256, String sha512) throws IOException {
        Path cacheFile = getLoc(url);
        Path cacheFileTmp = createRenameableTmpFn(cacheFile);
        if (cacheFile.toFile().exists()) {
            return new FileInputStream(cacheFile.toFile());
        }
        if (!cacheFile.getParent().toFile().exists()) {
            if (!cacheFile.getParent().toFile().mkdirs()) {
                throw new IOException("failed to create parent dir for: " + cacheFile);
            }
        }
        try {
            try (InputStream in = url.openStream()) {
                Files.copy(in, cacheFileTmp, StandardCopyOption.REPLACE_EXISTING);
            }

            if (sha256 != null) {
                String digest = getDigest(cacheFileTmp, "SHA-256");
                if (!sha256.toLowerCase(Locale.ROOT).equals(digest)) {
                    throw new IOException(
                            "SHA-256 for " + url.toExternalForm() + " is " + digest + ", expected: " + sha256);
                }
            }
            if (sha512 != null) {
                String digest = getDigest(cacheFileTmp, "SHA-512");
                if (!sha512.toLowerCase(Locale.ROOT).equals(digest)) {
                    throw new IOException(
                            "SHA-512 for " + url.toExternalForm() + " is " + digest + ", expected: " + sha512);
                }
            }

            Files.move(cacheFileTmp, cacheFile, StandardCopyOption.ATOMIC_MOVE);
            return new FileInputStream(cacheFile.toFile());
        } finally {
            if (cacheFileTmp.toFile().exists()) {
                cacheFileTmp.toFile().delete();
            }
        }
    }

    private static String getDigest(Path input, String type) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance(type);
            feed(digest, input);
            byte[] hash = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString().toLowerCase(Locale.ROOT);
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        }
    }

    private static void feed(MessageDigest digest, Path input) throws IOException {
        try (FileInputStream fis = new FileInputStream(input.toFile())) {
            byte[] buf = new byte[BUF_LEN];
            int read;
            while ((read = fis.read(buf)) > 0) {
                digest.update(buf, 0, read);
            }
        }
    }

    /**
     * Convenience wrapper around {@link #get(URL, String, String)}.
     */
    public static void copyIfNotExists(URL url, Path dest, String sha256, String sha512) throws IOException {
        Path tmp = createRenameableTmpFn(dest);
        try (InputStream is = get(url, sha256, sha512)) {
            Files.copy(is, tmp, StandardCopyOption.REPLACE_EXISTING);
            Files.move(tmp, dest, StandardCopyOption.ATOMIC_MOVE);
        } finally {
            if (tmp.toFile().exists()) {
                tmp.toFile().delete();
            }
        }
    }
    
    public static void copyIfNotExists(String url, Path dest, String sha256, String sha512) throws IOException {
        copyIfNotExists(new URL(url), dest, sha256, sha512);
    }

    /**
     * Returns a temporary file name in same directory as file, ie. below its parent.
     * The temp file name is constructed as {@code .<file>.tmp<current_thread_id>}. 
     */
    public static Path createRenameableTmpFn(Path file) {
        return Paths.get(file.getParent().toString(),
                "." + file.getFileName() + ".tmp" + Thread.currentThread().getId());
    }

    private DlCache() {
    }
}
