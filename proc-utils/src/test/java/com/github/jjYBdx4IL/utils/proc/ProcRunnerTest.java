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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//CHECKSTYLE:OFF
import java.io.File;
import java.util.Random;

/**
 *
 * @author jjYBdx4IL
 */
public class ProcRunnerTest {

    private static final Logger LOG = LoggerFactory.getLogger(ProcRunnerTest.class);
    
    Random r = new Random(0);
    String filename = "akhkha";
    File notExistingFile = new File(filename);

    @Before
    public void before() {
        int i = 0;
        while (notExistingFile.exists()) {
            notExistingFile = new File(filename + i);
            i++;
        }
    }
    
    @Test
    public void testErrorRedirect() throws Exception {
        LOG.trace(notExistingFile.getAbsolutePath());
        ProcRunner pr = new ProcRunner("ls", notExistingFile.getAbsolutePath());

        if (SystemUtils.IS_OS_WINDOWS) {
            pr = new ProcRunner("xcopy.exe", notExistingFile.getAbsolutePath());
        }
        
        assertNotEquals(0, pr.run());
        LOG.debug(pr.getOutputBlob());
        assertTrue(pr.getOutputBlob(), pr.getOutputBlob().contains(filename));
    }

    @Test
    public void testStdout() throws Exception {
        assumeFalse(SystemUtils.IS_OS_WINDOWS);
        
        String testString = "akhkhasad88d";

        ProcRunner pr = new ProcRunner("echo", testString);
        assertEquals(0, pr.run());
        LOG.debug(pr.getOutputBlob());
        assertTrue(pr.getOutputBlob().contains(testString));
    }

    @Test
    public void testStdoutWindows() throws Exception {
        assumeTrue(SystemUtils.IS_OS_WINDOWS);
        
        ProcRunner pr = new ProcRunner("xcopy.exe", notExistingFile.getAbsolutePath());
        assertNotEquals(0, pr.run());
        LOG.debug(pr.getOutputBlob());
        assertTrue(pr.getOutputBlob().contains("0 File(s) copied"));
    }

}
