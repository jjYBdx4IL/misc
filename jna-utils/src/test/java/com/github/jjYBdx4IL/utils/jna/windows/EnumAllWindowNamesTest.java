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
package com.github.jjYBdx4IL.utils.jna.windows;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.github.jjYBdx4IL.utils.env.CI;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public class EnumAllWindowNamesTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(EnumAllWindowNamesTest.class);
    
    /**
     * Test of getAllWindowNames method, of class EnumAllWindowNames.
     */
    @Test
    public void testGetAllWindowNames() {
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS && !CI.isCI());
        
        long started = System.currentTimeMillis();
        List<String> result = EnumAllWindowNames.getAllWindowNames();
        long duration = System.currentTimeMillis() - started;
        LOG.info("duration: " + duration + " ms");
        assertNotNull(result);
        assertTrue(result.size() > 0);
        for (String s : result) {
            LOG.info(s);
        }
    }
    
}
