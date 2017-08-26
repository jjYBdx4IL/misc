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
package com.github.jjYBdx4IL.utils.proc;

//CHECKSTYLE:OFF
import java.io.File;
import java.util.Random;

import org.apache.commons.lang3.SystemUtils;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jjYBdx4IL.utils.proc.ProcRunner;

/**
 *
 * @author jjYBdx4IL
 */
public class ProcRunnerTest {

    private static final Logger log = LoggerFactory.getLogger(ProcRunnerTest.class);
    Random r = new Random(0);

    @Before
    public void before() {
    	Assume.assumeTrue(SystemUtils.IS_OS_LINUX);
    }
    
    @Test
    public void testErrorRedirect() throws Exception {
        String filename = "akhkha";
        int i = 0;
        File notExistingFile = new File(filename);
        while (notExistingFile.exists()) {
            notExistingFile = new File(filename + i);
            i++;
        }

        log.trace(notExistingFile.getAbsolutePath());
        ProcRunner pr = new ProcRunner("ls", notExistingFile.getAbsolutePath());
        assertNotEquals(0, pr.run());
        log.debug(pr.getOutputBlob());
        assertTrue(pr.getOutputBlob().contains(filename));
    }

    @Test
    public void testStdout() throws Exception {
        String testString = "akhkhasad88d";

        ProcRunner pr = new ProcRunner("echo", testString);
        assertEquals(0, pr.run());
        log.debug(pr.getOutputBlob());
        assertTrue(pr.getOutputBlob().contains(testString));
    }

}
