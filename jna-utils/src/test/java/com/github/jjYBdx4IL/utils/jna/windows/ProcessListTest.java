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

//CHECKSTYLE:OFF
import com.github.jjYBdx4IL.utils.env.CI;
import com.github.jjYBdx4IL.utils.jna.windows.ProcessList;

import java.util.List;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class ProcessListTest {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessListTest.class);

    /**
     * Test of get method, of class ProcessList.
     */
    @Test
    public void testGet() {
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS && !CI.isCI());

        long started = System.currentTimeMillis();
        List<ProcessList.Process> result = ProcessList.get();
        long duration = System.currentTimeMillis() - started;
        LOG.info("duration: " + duration + " ms");
        assertNotNull(result);
        assertTrue(result.size() > 0);
        for (ProcessList.Process p : result) {
            LOG.info(p.toString());
        }
    }

}
