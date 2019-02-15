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
import static org.junit.Assert.fail;

import com.github.jjYBdx4IL.utils.env.Maven;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class FindUtilsTest {

    public static final File TEMP_DIR = Maven.getTempTestDir(FindUtilsTest.class);

    @Before
    public void before() throws IOException {
        FileUtils.cleanDirectory(TEMP_DIR);
        new File(TEMP_DIR, "1/22/333").mkdirs();
        FileUtils.write(new File(TEMP_DIR, "1/22/333/4444"), "abc", "UTF-8");
    }

    @Test
    public void testFind() throws IOException {
        try {
            // start dir is a file
            FindUtils.find(new File(TEMP_DIR, "1/22/333/4444"), "");
            fail();
        } catch (IOException ex) {
        }
        try {
            FindUtils.find(new File(TEMP_DIR, "1/22/333/not-existing"), "");
            fail();
        } catch (IOException ex) {
        }
        assertEquals(0, FindUtils.find(TEMP_DIR, "asd").size());
        assertEquals(1, FindUtils.find(TEMP_DIR, "4").size());
        assertEquals("4444", FindUtils.find(TEMP_DIR, "4").get(0).getName());
        assertEquals(2, FindUtils.find(TEMP_DIR, "3").size());
        assertEquals(3, FindUtils.find(TEMP_DIR, "2").size());
        assertEquals(4, FindUtils.find(TEMP_DIR, "1").size());
        // directories have final slash, files don't
        assertEquals(0, FindUtils.find(TEMP_DIR, "^/1$").size());
        assertEquals(1, FindUtils.find(TEMP_DIR, "^/1/$").size());
        assertEquals(1, FindUtils.find(TEMP_DIR, "/333/$").size());
        assertEquals(1, FindUtils.find(TEMP_DIR, "^/1/22/333/$").size());
        assertEquals(1, FindUtils.find(TEMP_DIR, "/4444$").size());
    }

    @Test
    public void testFindOne() throws IOException {
        assertNotNull(FindUtils.findOne(TEMP_DIR, "3/$"));
        assertNull(FindUtils.findOne(TEMP_DIR, "3"));
        assertNotNull(FindUtils.findOne(TEMP_DIR, "4$"));
    }

    @Test
    public void testFindOneOrThrow() throws IOException {
        assertNotNull(FindUtils.findOneOrThrow(TEMP_DIR, "3/$"));
        try {
            FindUtils.findOneOrThrow(TEMP_DIR, "3");
            fail();
        } catch (IOException ex) {
        }
    }
    
    @Test
    public void testFindFirst() throws IOException {
        assertNotNull(FindUtils.findFirst(TEMP_DIR, "3/$"));
        assertNull(FindUtils.findFirst(TEMP_DIR, "5"));
    }

    @Test
    public void testFindFirstOrThrow() throws IOException {
        assertNotNull(FindUtils.findFirstOrThrow(TEMP_DIR, "3/$"));
        try {
            FindUtils.findFirstOrThrow(TEMP_DIR, "321");
            fail();
        } catch (IOException ex) {
        }
    }
    
    @Test
    public void testGlobOne() throws IOException {
        assertNotNull(FindUtils.globOne(TEMP_DIR, "**/333/"));
        assertNull(FindUtils.globOne(TEMP_DIR, "**/3"));
        assertNull(FindUtils.globOne(TEMP_DIR, "*/3"));
        assertNotNull(FindUtils.globOne(TEMP_DIR, "**/4444"));
        assertNotNull(FindUtils.globOne(TEMP_DIR, "/*/*/*/4444"));
        assertNotNull(FindUtils.globOne(TEMP_DIR, "/*/*/*/*4*"));
        assertNull(FindUtils.globOne(TEMP_DIR, "/*/*/*/*4*/"));
    }
    
    @Test
    public void testGlob() throws IOException {
        assertEquals(new File(TEMP_DIR, "1/22/333/"), FindUtils.globOne(TEMP_DIR, "**/333/"));
        assertEquals(new File(TEMP_DIR, "1/22/333/"), FindUtils.globOne(TEMP_DIR,"/1/22/3*3/"));
    }
    
    @Test
    public void testGlobToRegex() {
        assertEquals("^$", FindUtils.globToRegex(""));
        assertEquals("^/$", FindUtils.globToRegex("/"));
        assertEquals("^[^/]*$", FindUtils.globToRegex("*"));
        assertEquals("^.*$", FindUtils.globToRegex("**"));
        assertEquals("^1/22/3[^/]*3/$", FindUtils.globToRegex("1/22/3*3/"));
    }
}
