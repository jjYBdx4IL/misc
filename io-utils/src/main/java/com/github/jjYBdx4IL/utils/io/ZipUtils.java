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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

//CHECKSTYLE:OFF
public class ZipUtils {

    /**
     * Beware! This method is not very reliable (compared to "unzip -t" command line utility).
     * 
     * @param file
     * @return
     */
    public static boolean test(File file) {
        
        long count = 0;
        
        byte[] buf = new byte[4096];
        
        try (ZipFile zipFile = new ZipFile(file)) {
            for (ZipEntry zipEntry : Collections.list(zipFile.entries())) {
                try (InputStream is = zipFile.getInputStream(zipEntry)) {
                    while (is.read(buf) != -1) {}
                }
                zipEntry.getCrc();
                zipEntry.getCompressedSize();
                zipEntry.getName();
                count++;
            }
        } catch (IOException e) {
            return false;
        }
        
        return count > 0;
    }
}
