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

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;

import com.github.jjYBdx4IL.utils.junit4.RetryRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * One failure only.
 *
 * @author jjYBdx4IL
 */
@RunWith(RetryRunner.class)
public class RetryRunnerOneFailureTest {

    private static int c = 0;
    private static int cBefore = 0;
    private static int cAfter = 0;

    @AfterClass
    public static void afterClass() {
        assertEquals(2, c);
        assertEquals(2, cBefore);
        assertEquals(2, cAfter);
    }

    @Before
    public void before() {
        cBefore++;
    }

    @After
    public void after() {
        cAfter++;
    }

    @Test
    public void test() {
        c++;
        if(c==2) {
            return;
        }
        fail();
    }

}
