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

import com.github.jjYBdx4IL.utils.env.Maven;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class IoUtilsTest {
    
    private final static File TEMP_DIR = Maven.getTempTestDir(IoUtilsTest.class);
    private final static File TEST_FILE= new File(TEMP_DIR, "testfile.txt");

    @Test
    public void testSafeWriteTo() throws IOException {
        IoUtils.safeWriteTo(TEST_FILE, "abc");
        assertEquals("abc", FileUtils.readFileToString(TEST_FILE, "UTF-8"));
        
        IoUtils.safeWriteTo(TEST_FILE, new ByteArrayInputStream("123".getBytes("UTF-8")));
        assertEquals("123", FileUtils.readFileToString(TEST_FILE, "UTF-8"));
    }
}
