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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;

public class SafeFileOutputStreamTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testGetDefSave() throws Exception {
        File finalFile = new File(folder.getRoot(), "testfile");
        try (FileOutputStream os = SafeFileOutputStream.getDefSave(finalFile)) {
            os.write(32);
            os.flush();
            assertFalse(finalFile.exists());
        }
        assertTrue(finalFile.exists());
        assertEquals(" ", Files.readString(finalFile.toPath()));
    }

    @Test
    public void testGetDefSaveWithAbort() throws Exception {
        File finalFile = new File(folder.getRoot(), "testfile");
        try (SafeFileOutputStream os = SafeFileOutputStream.getDefSave(finalFile)) {
            os.abort();
            os.write(32);
            os.flush();
            assertFalse(finalFile.exists());
        }
        assertFalse(finalFile.exists());
    }

    @Test
    public void testGet() throws Exception {
        File finalFile = new File(folder.getRoot(), "testfile");
        try (SafeFileOutputStream os = SafeFileOutputStream.get(finalFile)) {
            os.write(32);
            os.flush();
        }
        assertFalse(finalFile.exists());
    }
    
    @Test
    public void testGetWithSave() throws Exception {
        File finalFile = new File(folder.getRoot(), "testfile");
        try (SafeFileOutputStream os = SafeFileOutputStream.get(finalFile)) {
            os.save();
            os.write(32);
            os.flush();
        }
        assertTrue(finalFile.exists());
    }
}
