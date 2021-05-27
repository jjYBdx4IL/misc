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
package com.github.jjYBdx4IL.utils.env;

//CHECKSTYLE:OFF
import java.io.File;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import org.junit.Assume;

/**
 *
 * @author jjYBdx4IL
 */
public class WindowsUtilsTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(WindowsUtilsTest.class);
    
    /**
     * Test of getCurrentUserDesktopPath method, of class WindowsUtils.
     */
    @Test
    public void testGetCurrentUserDesktopPath() {
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS && !CI.isCI());
        
        String result = WindowsUtils.getCurrentUserDesktopPath();
        assertNotNull(result);
    }
    
    @Test
    public void testGetCygwinInstallationPath() {
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS && !CI.isCI());
        
        String result = WindowsUtils.getCygwinInstallationPath();
        if (new File("C:\\cygwin64").exists()) {
            assertNotNull(result);
        }
        LOG.info(result);
    }
}
