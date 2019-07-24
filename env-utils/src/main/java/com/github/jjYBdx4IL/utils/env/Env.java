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

import com.github.jjYBdx4IL.utils.io.IoUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Pattern;

// CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public class Env {

    private static final Logger LOG = LoggerFactory.getLogger(Env.class);
    public static final Pattern APP_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9.]+$");

    private static List<String> getEnvPropDump() {
        List<String> lines = new ArrayList<>();
        for (Entry<Object, Object> e : System.getProperties().entrySet()) {
            lines.add(String.format("SYSPROP: %s=%s", e.getKey(), e.getValue()));
        }
        for (Entry<String, String> e : System.getenv().entrySet()) {
            lines.add(String.format("ENV: %s=%s", e.getKey(), e.getValue()));
        }
        Collections.sort(lines);
        return lines;
    }

    public static void dumpEnvToStdErr() {
        dumpEnv(System.err);
    }

    public static void dumpEnvToStdOut() {
        dumpEnv(System.out);
    }

    public static void dumpEnv(PrintStream s) {
        for (String line : getEnvPropDump()) {
            s.println(line);
        }
    }

    public static void dumpEnvInfo() {
        if (!LOG.isInfoEnabled()) {
            return;
        }
        for (String line : getEnvPropDump()) {
            LOG.info(line);
        }
    }

    public static void dumpEnvDebug() {
        if (!LOG.isDebugEnabled()) {
            return;
        }
        for (String line : getEnvPropDump()) {
            LOG.debug(line);
        }
    }

    public static void dumpEnvTrace() {
        if (!LOG.isTraceEnabled()) {
            return;
        }
        for (String line : getEnvPropDump()) {
            LOG.trace(line);
        }
    }

    protected static String get(String envVar) {
        return get(envVar, null);
    }

    protected static String get(String envVar, String fallback) {
        String value = System.getenv(envVar);
        if (value == null) {
            value = fallback;
        }
        if (value == null) {
            throw new IllegalAccessError(
                    String.format("tried to access env var %s, but it is not set", envVar));
        }
        return value;
    }

    public static boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().startsWith("linux");
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().startsWith("windows");
    }

    public static void assertWindows() {
        if (!isWindows()) {
            throw new RuntimeException("function not implemented for this platform");
        }
    }

    public static File getDesktopDir() {
        assertWindows();

        return new File(WindowsUtils.getCurrentUserDesktopPath());
    }

    /**
     * Read application config properties from a standard location.
     * 
     * @param classRef indicating the app (name)
     * @return properties read from config file
     * @throws IOException if config file is not found or cannot be read
     */
    public static Properties readAppConfig(Class<?> classRef) throws IOException {
        return readAppConfig(classRef.getName());
    }
    
    public static Properties readAppConfig(String appName) throws IOException {
        File configDir = getConfigDir(appName);
        File configFile = new File(configDir, "config");
        Properties p = new Properties();
        try (InputStream is = new FileInputStream(configFile)) {
            p.load(is);
        }
        return p;
    }
    
    public static void writeAppConfig(Class<?> classRef, Properties props) throws IOException {
        writeAppConfig(classRef.getName(), props);
    }
    
    public static void writeAppConfig(String appName, Properties props) throws IOException {
        File configDir = getConfigDir(appName);
        File configFile = new File(configDir, "config");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        props.store(baos, "");
        IoUtils.safeWriteTo(configFile, baos.toString(StandardCharsets.ISO_8859_1.name()));
    }
    
    public static File getConfigDir(Class<?> classRef) {
        return getConfigDir(classRef.getName());
    }
    
    public static File getConfigDir(String appName) {
        if (!APP_NAME_PATTERN.matcher(appName).find()) {
            throw new IllegalArgumentException("invalid app name: " + appName);
        }
        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData != null) {
            return new File(new File(localAppData, appName), "config");
        }
        return new File(new File(System.getProperty("user.home"), ".config"), appName);
    }
    
    public static File getCacheDir(String appName) {
        if (!APP_NAME_PATTERN.matcher(appName).find()) {
            throw new IllegalArgumentException("invalid app name: " + appName);
        }
        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData != null) {
            return new File(new File(localAppData, appName), "cache");
        }
        return new File(new File(System.getProperty("user.home"), ".cache"), appName);
    }
    
    public static File getLogDir(String appName) {
        if (!APP_NAME_PATTERN.matcher(appName).find()) {
            throw new IllegalArgumentException("invalid app name: " + appName);
        }
        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData != null) {
            return new File(new File(localAppData, appName), "log");
        }
        return new File(new File(new File(System.getProperty("user.home"), ".cache"), appName), "log");
    }

    public static File provideLogDir(String appName) throws IOException {
        File logDir = getLogDir(appName);
        if (!logDir.exists()) {
            if (!logDir.mkdirs() || !logDir.exists() || !logDir.isDirectory()) {
                throw new IOException("failed to create directory "+ logDir.getAbsolutePath());
            }
        }
        return logDir;
    }

}
