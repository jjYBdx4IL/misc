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
package com.github.jjYBdx4IL.utils.cfg;

import com.github.jjYBdx4IL.utils.env.Env;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Class storing properties in a file.
 * 
 * <p>
 * Used for storing application configuration parameters. We try to make config
 * file updates as atomic and reliable as possible, ie. <b>not</b> updating them
 * in-place, but using temp files and rollback on missing files if there is a
 * backup file.
 * </p>
 * 
 * @author jjYBdx4IL
 */
public class AtomicPropsFile {

    public static final String DEFAULT_FILENAME = "config.properties";
    private final File cfgFile;
    private final Properties props;
    private boolean modified = false;
    private final Class<?> klazz;
    private final String relativeFileName;

    public AtomicPropsFile(Class<?> klazz) {
        this(klazz, DEFAULT_FILENAME);
    }

    /**
     * Constructor.
     * 
     * @param klazz
     *            the class for which to construct a config file
     * @param relativeFileName
     *            the relative path of the file name
     */
    public AtomicPropsFile(Class<?> klazz, String relativeFileName) {
        if (klazz == null || relativeFileName == null) {
            throw new IllegalArgumentException();
        }
        this.klazz = klazz;
        this.relativeFileName = relativeFileName;
        cfgFile = new File(Env.getConfigDir(klazz), relativeFileName);
        props = new Properties();
    }

    /**
     * Load the configuration from disk.
     * 
     * @throws IOException
     *             if there was an error
     */
    public void load() throws IOException {
        try (InputStream is = new FileInputStream(cfgFile)) {
            props.load(is);
        }
    }

    /**
     * Save the configuration to disk.
     * 
     * <p>
     * Won't do anything if nothing has been changed.
     * </p>
     * 
     * <p>
     * TODO: flush/sync?
     * </p>
     * 
     * @throws IOException
     *             if there was an error saving the config file
     */
    public void save() throws IOException {
        if (!modified) {
            return;
        }

        File parentDir = cfgFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        if (!parentDir.exists()) {
            throw new IOException("failed to created directory: " + parentDir);
        }

        File tmpFile = new File(cfgFile.getAbsolutePath() + ".tmp");
        if (tmpFile.exists()) {
            tmpFile.delete();
        }
        if (tmpFile.exists()) {
            throw new IOException("failed to delete " + tmpFile);
        }
        try (OutputStream os = new FileOutputStream(tmpFile, false)) {
            props.store(os, "");
        }

        if (cfgFile.exists()) {
            File bakFile = new File(cfgFile.getAbsolutePath() + ".bak");
            if (bakFile.exists()) {
                bakFile.delete();
            }
            if (bakFile.exists()) {
                throw new IOException("failed to delete " + bakFile);
            }
            cfgFile.renameTo(bakFile);
            if (cfgFile.exists()) {
                throw new IOException("failed to rename " + cfgFile);
            }
        }

        tmpFile.renameTo(cfgFile);
        if (tmpFile.exists()) {
            throw new IOException("failed to rename " + tmpFile);
        }
        if (!cfgFile.exists()) {
            throw new IOException("failed to create " + cfgFile);
        }

        modified = false;
    }

    /**
     * Get config value by its key.
     * 
     * @param key
     *            The config key.
     * @return The config value.
     */
    public String get(String key) {
        return props.getProperty(key);
    }

    /**
     * Get config value by its key.
     * 
     * @param key
     *            The config key.
     * @param defaultValue
     *            The config value to return if the config parameter has not
     *            been set.
     * @return The config value.
     */
    public String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    /**
     * Set a config parameter.
     * 
     * @param key
     *            The config parameter's name/key.
     * @param value
     *            Its value.
     */
    public void put(String key, String value) {
        modified = true;
        props.put(key, value);
    }

    protected Class<?> getKlazz() {
        return klazz;
    }

    protected String getRelativeFileName() {
        return relativeFileName;
    }

    /**
     * For testing only.
     * 
     * @return the config file
     */
    protected File getCfgFile() {
        return cfgFile;
    }

}
