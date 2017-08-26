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
import static org.junit.Assert.*;

import com.github.jjYBdx4IL.utils.junit4.RDRunner;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
@RunWith(RDRunner.class)
public class RDRunnerTest {

    private static final Logger LOG = LoggerFactory.getLogger(RDRunnerTest.class);

    private static int c = 0;

    @BeforeClass
    public static void beforeClass() {
        LOG.info("beforeClass()");
        c++;
    }

    @Before
    public void before() {
        LOG.info("before()");
        c++;
    }

    @After
    public void after() {
        LOG.info("after()");
        c--;
    }

    @Test
    public void test1() {
        LOG.info("test1");
        assertEquals(2, c);
    }
    
    @Test
    public void test2() {
        LOG.info("test2");
        assertEquals(2, c);
    }

}
