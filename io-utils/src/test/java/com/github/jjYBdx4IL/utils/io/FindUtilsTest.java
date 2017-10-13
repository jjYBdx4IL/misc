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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.github.jjYBdx4IL.utils.env.Maven;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class FindUtilsTest {

    public static final File tempDir = Maven.getTempTestDir(FindUtilsTest.class);

    @Before
    public void before() throws IOException {
        FileUtils.cleanDirectory(tempDir);
        new File(tempDir, "1/22/333").mkdirs();
        FileUtils.write(new File(tempDir, "1/22/333/4444"), "abc", "UTF-8");
    }

    @Test
    public void testFind() throws IOException {
        try {
            FindUtils.find(new File(tempDir, "1/22/333/4444"), ""); // s tart
                                                                    // dir is a
                                                                    // file
            fail();
        } catch (IOException ex) {
        }
        try {
            FindUtils.find(new File(tempDir, "1/22/333/not-existing"), "");
            fail();
        } catch (IOException ex) {
        }
        assertEquals(0, FindUtils.find(tempDir, "asd").size());
        assertEquals(1, FindUtils.find(tempDir, "4").size());
        assertEquals("4444", FindUtils.find(tempDir, "4").get(0).getName());
        assertEquals(2, FindUtils.find(tempDir, "3").size());
        assertEquals(3, FindUtils.find(tempDir, "2").size());
        assertEquals(4, FindUtils.find(tempDir, "1").size());
        // directories have final slash, files don't
        assertEquals(0, FindUtils.find(tempDir, "^/1$").size());
        assertEquals(1, FindUtils.find(tempDir, "^/1/$").size());
        assertEquals(1, FindUtils.find(tempDir, "/333/$").size());
        assertEquals(1, FindUtils.find(tempDir, "^/1/22/333/$").size());
        assertEquals(1, FindUtils.find(tempDir, "/4444$").size());
    }

    @Test
    public void testFindOne() throws IOException {
        assertNotNull(FindUtils.findOne(tempDir, "3/$"));
        assertNull(FindUtils.findOne(tempDir, "3"));
        assertNotNull(FindUtils.findOne(tempDir, "4$"));
    }

    @Test
    public void testFindOneOrThrow() throws IOException {
        assertNotNull(FindUtils.findOneOrThrow(tempDir, "3/$"));
        try {
            FindUtils.findOneOrThrow(tempDir, "3");
            fail();
        } catch (IOException ex) {
        }
    }
    
    @Test
    public void testFindFirst() throws IOException {
        assertNotNull(FindUtils.findFirst(tempDir, "3/$"));
        assertNull(FindUtils.findFirst(tempDir, "5"));
    }

    @Test
    public void testFindFirstOrThrow() throws IOException {
        assertNotNull(FindUtils.findFirstOrThrow(tempDir, "3/$"));
        try {
            FindUtils.findFirstOrThrow(tempDir, "321");
            fail();
        } catch (IOException ex) {
        }
    }
    
    @Test
    public void testGlobOne() throws IOException {
        assertNotNull(FindUtils.globOne(tempDir, "**/333/"));
        assertNull(FindUtils.globOne(tempDir, "**/3"));
        assertNull(FindUtils.globOne(tempDir, "*/3"));
        assertNotNull(FindUtils.globOne(tempDir, "**/4444"));
        assertNotNull(FindUtils.globOne(tempDir, "/*/*/*/4444"));
        assertNotNull(FindUtils.globOne(tempDir, "/*/*/*/*4*"));
        assertNull(FindUtils.globOne(tempDir, "/*/*/*/*4*/"));
    }
    
    @Test
    public void testGlobToRegex() {
        assertEquals("^$", FindUtils.globToRegex(""));
        assertEquals("^/$", FindUtils.globToRegex("/"));
        assertEquals("^[^/]*$", FindUtils.globToRegex("*"));
        assertEquals("^.*$", FindUtils.globToRegex("**"));
    }
}
