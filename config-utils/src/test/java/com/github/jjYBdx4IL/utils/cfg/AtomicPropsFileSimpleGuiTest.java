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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.cfg.AtomicPropsFileSimpleGui;
import com.github.jjYBdx4IL.utils.env.Surefire;

import org.junit.Test;

import java.io.IOException;

public class AtomicPropsFileSimpleGuiTest {

    @Test
    public void test() throws InterruptedException, IOException {
        assumeTrue(Surefire.isSingleTestExecution());

        AtomicPropsFileSimpleGui gui = new AtomicPropsFileSimpleGui(AtomicPropsFileSimpleGuiTest.class, "config key 1",
                "config key 2");

        gui.getPropsFile().getCfgFile().delete();
        assertFalse(gui.getPropsFile().getCfgFile().exists());

        gui.loadOrShow(false);

        assertNotNull(gui.get("config key 1"));
        assertNotNull(gui.get("config key 2"));
        assertFalse(gui.get("config key 1").isEmpty());
        assertFalse(gui.get("config key 2").isEmpty());
    }

}
