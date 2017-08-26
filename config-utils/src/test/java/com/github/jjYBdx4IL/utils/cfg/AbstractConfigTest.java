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
package com.github.jjYBdx4IL.utils.cfg;

import static com.github.jjYBdx4IL.utils.cfg.AbstractConfig.APP_NAME_PATTERN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;

/**
 * Test for AbstractConfig.
 *
 * @author Github jjYBdx4IL Projects
 */
public class AbstractConfigTest {

    private void assertDelete(File file) {
        if (file.exists()) {
            assertTrue(file.delete());
            assertFalse(file.exists());
        }
    }

    @Test
    public void testReadNonExistingFile() throws Exception {
        ExampleConfig config = new ExampleConfig("GithubUtilsTest");
        File file = config.getConfigFile();

        assertDelete(file);
        assertFalse(config.read());
    }

    @Test
    public void testFullCycle() throws Exception {
        ExampleConfig config = new ExampleConfig("GithubUtilsTest");
        config.configOption1 = "config value 1";
        config.configOption2 = "config value 2";
        config.write();

        ExampleConfig config2 = new ExampleConfig("GithubUtilsTest");
        assertTrue(config2.read());
        assertEquals("config value 1", config2.configOption1);
        assertEquals("CONFIG VALUE 2", config2.configOption2);
    }

    @Test
    public void testAppNamePattern() {
        assertFalse(APP_NAME_PATTERN.matcher("").find());
        assertFalse(APP_NAME_PATTERN.matcher("a,").find());
        assertTrue(APP_NAME_PATTERN.matcher("a.0B").find());
    }
}
