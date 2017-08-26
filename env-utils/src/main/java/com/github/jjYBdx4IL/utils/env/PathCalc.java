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
package com.github.jjYBdx4IL.utils.env;

//CHECKSTYLE:OFF
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class PathCalc {

    public static File getAppCacheDir(String appName) {
        return getAppCacheDir(appName, -1);
    }

    /**
     * 
     * @param appName your application's name or id
     * @param maxCacheDirAge
     *            defines after how many days the cache dir should be
     *            reinitialized, -1 to deactivate reinitialization
     * @return the cache dir for your application
     */
    public static File getAppCacheDir(String appName, int maxCacheDirAge) {
        if (appName.contains(File.separator) || appName.contains(File.pathSeparator) || appName.contains(";")
                || appName.contains(":") || appName.contains("/") || appName.contains("\\")) {
            throw new IllegalArgumentException("bad appName param: " + appName);
        }

        String userHome = System.getProperty("user.home");
        if (userHome == null || userHome.length() == 0) {
            throw new IllegalArgumentException("environment variable user home is not set or empty");
        }
        File homeDir = new File(userHome);
        if (!homeDir.exists() || !homeDir.isDirectory()) {
            throw new IllegalArgumentException("home dir does not exist or is not a directory");
        }
        File cacheDir = new File(homeDir, ".cache");
        File appCacheDir = new File(cacheDir, appName);

        long currentTimestamp = System.currentTimeMillis();

        if (!appCacheDir.exists()) {
            appCacheDir.mkdirs();
        }

        try {
            writeTimestampIfNotExists(appCacheDir, currentTimestamp);
            long appCacheDirTimestamp = readTimestamp(appCacheDir);
            long ageDays = (currentTimestamp - appCacheDirTimestamp) / 86400 / 1000;
            if (maxCacheDirAge >= 0) {
                if (ageDays < 0 || ageDays >= maxCacheDirAge) {
                    FileUtils.cleanDirectory(appCacheDir);
                    writeTimestampIfNotExists(appCacheDir, currentTimestamp);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        if (!appCacheDir.exists() || !appCacheDir.isDirectory()) {
            throw new IllegalArgumentException("app cache dir does exist, is not a directory "
                    + "or could not be created: " + appCacheDir.getAbsolutePath());
        }

        return appCacheDir;
    }

    private static void writeTimestampIfNotExists(File appCacheDir, long timestamp) throws IOException {
        File timestampFile = new File(appCacheDir, ".creationtime");
        try (OutputStream os = new FileOutputStream(timestampFile)) {
            IOUtils.write(Long.toString(timestamp), os, Charset.forName("UTF-8"));
        }
    }

    private static long readTimestamp(File appCacheDir) throws NumberFormatException, IOException {
        File timestampFile = new File(appCacheDir, ".creationtime");
        try (InputStream is = new FileInputStream(timestampFile)) {
            return Long.valueOf(IOUtils.toString(is, Charset.forName("UTF-8")));
        }
    }
}
