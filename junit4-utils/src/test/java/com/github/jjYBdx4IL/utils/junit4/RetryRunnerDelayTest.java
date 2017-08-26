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
package com.github.jjYBdx4IL.utils.junit4;

import static org.junit.Assert.*;

import com.github.jjYBdx4IL.utils.junit4.RetryRunner;
import com.github.jjYBdx4IL.utils.junit4.RetryRunnerConfig;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * One failure only.
 *
 * @author jjYBdx4IL
 */
@RunWith(RetryRunner.class)
@RetryRunnerConfig(delayMillis = 500L)
public class RetryRunnerDelayTest {

    private static long lastTime = -1l;

    @Test
    public void test() {
        if (lastTime == -1l) {
            lastTime = System.currentTimeMillis();
            fail();
        }
        assertTrue(System.currentTimeMillis() >= lastTime + 500L);
    }

}
