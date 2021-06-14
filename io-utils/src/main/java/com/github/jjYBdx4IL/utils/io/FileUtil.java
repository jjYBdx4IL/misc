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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class FileUtil {

    /**
     * Delete a file with retries. Retry delay is one second. Can be used to wait for other processes
     * to give up their locks on the file in question. Returns immediately if the file does not exist.
     * 
     * @param file the file to delete
     * @param timeoutSecs how long to wait
     * @throws InterruptedException if interrupted during wait
     * @throws IOException on timeout
     */
    public static void ensureDeleted(Path file, int timeoutSecs) throws InterruptedException, IOException {
        if (Files.notExists(file)) {
            return;
        }
        long timeout = System.currentTimeMillis() + timeoutSecs * 1000L;
        do {
            try {
                Files.delete(file);
                return;
            } catch (IOException e) {
                Thread.sleep(1000);
            }
        }
        while (Files.exists(file) && System.currentTimeMillis() < timeout);
        throw new IOException("timed out while trying to delete " + file);
    }
    
    /**
     * Clean a directoy with retries. Retry delay is one second. Can be used to wait for other processes
     * to give up their locks on the file in question. Returns immediately if the file does not exist.
     * The retry functionality is only enabled on Windows. Returns immediately if the directory does not
     * exist.
     */
    public static void cleanDir(Path dir, int timeoutSecs) throws IOException, InterruptedException {
        if (Files.notExists(dir)) {
            return;
        }
        if (!SystemUtils.IS_OS_WINDOWS) {
            FileUtils.cleanDirectory(dir.toFile());
            return;
        }
        long timeout = System.currentTimeMillis() + timeoutSecs * 1000L;
        do {
            try {
                FileUtils.cleanDirectory(dir.toFile());
                break;
            } catch (IOException ex) {
                Thread.sleep(1000);
            }
        }
        while (Files.exists(dir) && System.currentTimeMillis() < timeout);
        FileUtils.cleanDirectory(dir.toFile());
    }
    
    /**
     * Load properties from file.
     */
    public static Properties loadProps(Path file) throws FileNotFoundException, IOException {
        Properties p = new Properties();
        try (InputStream is = new FileInputStream(file.toFile())) {
            p.load(is);
        }
        return p;
    }
    
    /**
     * Load properties from XML file.
     */
    public static Properties loadPropsXml(Path file) throws FileNotFoundException, IOException {
        Properties p = new Properties();
        try (InputStream is = new FileInputStream(file.toFile())) {
            p.loadFromXML(is);
        }
        return p;
    }
    
    /**
     * Save properties to file.
     */
    public static void saveProps(Path file, Properties props) throws IOException {
        try (OutputStream os = SafeFileOutputStream.getDefSave(file.toFile())) {
            props.store(os, "");
        }
    }
    
    /**
     * Save properties to XML file (UTF-8 encoded).
     */
    public static void savePropsXml(Path file, Properties props) throws IOException {
        try (OutputStream os = SafeFileOutputStream.getDefSave(file.toFile())) {
            props.storeToXML(os, "", "UTF-8");
        }
    }
    
    /**
     * Update properties in file and return the updated properties after saving.
     */
    public static Properties updateProps(Path file, String key, String value) throws IOException {
        Properties p;
        if (file.toFile().exists()) {
            p = loadProps(file);
        } else {
            p = new Properties();
        }
        p.put(key,value);
        saveProps(file, p);
        return p;
    }
    
    /**
     * Update properties in XML file and return the updated properties after saving.
     */
    public static Properties updatePropsXml(Path file, String key, String value) throws IOException {
        Properties p;
        if (file.toFile().exists()) {
            p = loadPropsXml(file);
        } else {
            p = new Properties();
        }
        p.put(key,value);
        savePropsXml(file, p);
        return p;
    }
    
    private FileUtil() {
    }
    
}
