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
package com.github.jjYBdx4IL.test;

import static org.junit.Assert.assertTrue;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public class FileUtil {

    /**
     * Requires to be executed via maven.
     *
     * @return ${basedir}/target
     */
    public static File getMavenTargetDir() {
        String basedir = System.getProperty("basedir");
        // junit test running under eclipse? -> use user.dir property
        if (basedir == null && System.getProperty("sun.java.command")
                .startsWith("org.eclipse.jdt.internal.junit.runner.RemoteTestRunner ")) {
            basedir = System.getProperty("user.dir");
        }
        if (basedir == null) {
            throw new IllegalArgumentException("basedir sys prop not found, please use maven");
        }
        return new File(basedir, "target");
    }

    /**
     * Creates an empty directory at ${basedir}/target/{testClassName} and
     * cleans it if necessary. Requires to be executed via maven. If the
     * directory exists, it will be emptied.
     *
     * @param klazz the class
     * @return the directory
     */
    public static File createMavenTestDir(final Class<?> klazz) {
        String testClassName = klazz.getName();
        File rootDir = new File(getMavenTargetDir(), testClassName);
        try {
            if (rootDir.exists()) {
                FileUtils.cleanDirectory(rootDir);
            } else {
                assertTrue(rootDir.mkdirs());
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return rootDir;
    }

    public static void provideCleanDirectory(File dir) {
        if (!dir.exists()) {
            assertTrue(dir.mkdirs());
        } else {
            try {
                FileUtils.cleanDirectory(dir);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * Return properly escaped File.separator that can be plugged into a regular
     * expression.
     * 
     * @return the separator
     */
    public static String escapedSeparator() {
        return File.separator.replace("\\", "\\\\");
    }

    private FileUtil() {
    }

}
