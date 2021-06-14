/*
 * Copyright © 2021 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.utils.cygwin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;

import com.github.jjYBdx4IL.utils.cygwin.CygwinUtils.RunResult;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Files;

public class CygwinUtilsTest {

    @Before
    public void before() {
        assumeNotNull(CygwinUtils.installRoot);
    }
    
    @Test
    public void testBashRun() throws Exception {
        try (RunResult rr = CygwinUtils.bashRun("echo ehlo", true)) {
            assertEquals(Files.readString(rr.stderr), 0, rr.rc);
            assertEquals(Files.readString(rr.stdout), "ehlo\n", Files.readString(rr.stdout));
        }
        
        try (RunResult rr = CygwinUtils.bashRun("uname", true)) {
            assertEquals(Files.readString(rr.stderr), 0, rr.rc);
            assertTrue(Files.readString(rr.stdout), Files.readString(rr.stdout).startsWith("CYGWIN"));
        }
    }
    
    // junit does not like inherited I/O
    @Ignore 
    @Test
    public void testBashRunInheritIo() throws Exception {
        try (RunResult rr = CygwinUtils.bashRun("echo ehlo", false)) {
            assertEquals(0, rr.rc);
            assertTrue(Files.notExists(rr.stdout));
            assertTrue(Files.notExists(rr.stderr));
        }
        
        try (RunResult rr = CygwinUtils.bashRun("uname", false)) {
            assertEquals(0, rr.rc);
            assertTrue(Files.notExists(rr.stdout));
            assertTrue(Files.notExists(rr.stderr));
        }
    }
}
