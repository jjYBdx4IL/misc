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

import static com.github.jjYBdx4IL.utils.check.CheckUtil.checkArgument;
import static com.github.jjYBdx4IL.utils.check.CheckUtil.checkNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class PropStore implements AutoCloseable {

    private final Path loc;
    private final Properties props;
    private boolean isDirty = false;
    private final boolean immediate; // instantly save upon any change?

    private PropStore(Path loc, boolean immediate) throws IOException {
        this.loc = loc;
        this.immediate = immediate;
        Properties p = null;
        try {
            p = FileUtil.loadProps(loc);
        } catch (FileNotFoundException e) {
            // ignored
        }
        if (p == null) {
            p = new Properties();
            FileUtil.saveProps(loc, p);
        }
        props = p;
    }

    /**
     * Loads the propstore at the specified location. Creates an empty one if it
     * does not exist. Does NOT immediately save changes to disk. Intended to be
     * used with the AutoCloseable interface.
     */
    public static PropStore get(Path loc) throws IOException {
        return new PropStore(loc, false);
    }
    
    public static PropStore get(File loc) throws IOException {
        return new PropStore(loc.toPath(), false);
    }
    
    public static PropStore get(String loc) throws IOException {
        return new PropStore(Paths.get(loc), false);
    }

    /**
     * Loads the propstore at the specified location. Creates an empty one if it
     * does not exist. Immediately saves any changes to disk.
     */
    public static PropStore geti(Path loc) throws IOException {
        return new PropStore(loc, true);
    }
    
    public static PropStore geti(File loc) throws IOException {
        return new PropStore(loc.toPath(), true);
    }
    
    public static PropStore geti(String loc) throws IOException {
        return new PropStore(Paths.get(loc), true);
    }
    
    /**
     * Return entry for the given key in order to access its value.
     */
    public Entry entry(String key) {
        checkNotNull(key);
        checkArgument(!key.isEmpty());
        return new Entry(this, key);
    }
    
    @Override
    public void close() throws IOException {
        save();
    }
    
    protected void save() throws IOException {
        if (isDirty) {
            FileUtil.saveProps(loc, props);
            isDirty = false;
        }
    }
    
    protected void updated() throws IOException {
        isDirty = true;
        if (immediate) {
            save();
        }
    }
    
    public static class Entry {
        
        final PropStore parent;
        final String key;
        
        Entry(PropStore parent, String key) {
            this.parent = parent;
            this.key = key;
        }
        
        /**
         * Set a new value. Use null to remove the key.
         */
        public void set(Long value) throws IOException {
            if (value == null) {
                parent.props.remove(key);
            } else {
                parent.props.setProperty(key, Long.toString(value));
            }
            parent.updated();
        }
            
        /**
         * Returns null by default and if the value cannot be parsed as long.
         */
        public Long asLong() {
            return asLong(null);
        }
        
        /**
         * Returns defaultValue by default and if the value cannot be parsed as long.
         */
        public Long asLong(Long defaultValue) {
            String value = parent.props.getProperty(key);
            try {
                return value == null ? defaultValue : Long.parseLong(value);
            } catch (NumberFormatException ex) {
                return defaultValue;
            }
        }
    }
}
