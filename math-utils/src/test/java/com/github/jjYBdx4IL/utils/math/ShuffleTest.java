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
package com.github.jjYBdx4IL.utils.math;

//CHECKSTYLE:OFF
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ShuffleTest extends ShuffleTestBase {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ShuffleTest.class);

    @Test
    public void testShuffle2() {
        Integer[] input = new Integer[3];
        for (int i = 0; i < 1; i++) {
            init(input);
            s.shuffle2(input);
            checkArray(input);
        }
    }

    /**
     * Create a transition matrix and use it to count the position transitions executed by the shuffle
     * operation. Repeat shuffles until we get a transition distribution over the entire matrix that is
     * sufficiently uniform within the given precision.
     */
    @Test
    public void testShuffleQuality() {
        LOG.info("testShuffleQuality");
        // perfect quality (transition matrix shows even distribution)
        float delta = computeDelta(new ShuffleRunner<Integer>() {

            @Override
            public void shuffle(Integer[] t) {
                s.shuffle(t);
            }
        }, 3, .0001f);
        assertEquals(0.0f, delta, .01f);
    }

    @Test
    public void testShuffle2Quality() {
        LOG.info("testShuffle2Quality");
        // not so good quality (transition matrix shows a larger weight on diagonal elements, which means
        // that shuffle2 tends to leave array elements at their position.
        float delta = computeDelta(new ShuffleRunner<Integer>() {

            @Override
            public void shuffle(Integer[] t) {
                s.shuffle2(t);
            }
        }, 3, .0001f);
        assertEquals(0.44, delta, .01f);
    }

    @Test
    public void testArraysShuffleQuality() {
        LOG.info("testArraysShuffleQuality");
        // perfect quality (transition matrix shows even distribution)
        float delta = computeDelta(new ShuffleRunner<Integer>() {

            @Override
            public void shuffle(Integer[] t) {
                Collections.shuffle(Arrays.asList(t), r);
            }
        }, 3, .0001f);
        assertEquals(0.0f, delta, .01f);
    }

    @Test
    public void test3() {
        // every position may change
        testUntilTrue(new int[]{0, 0, 0});
        testUntilTrue(new int[]{1, 1, 1});

        // everything *MAY* change
        testUntilTrue(new int[]{1, -1, -1});
        testUntilTrue(new int[]{0, -1, -1});
        testUntilTrue(new int[]{-1, 1, -1});
        testUntilTrue(new int[]{-1, 0, -1});
        testUntilTrue(new int[]{-1, -1, 1});
        testUntilTrue(new int[]{-1, -1, 0});

        // change in only one position not possible
        testAlwaysFalse(new int[]{1, 0, 0});
        testAlwaysFalse(new int[]{0, 1, 0});
        testAlwaysFalse(new int[]{0, 0, 1});
    }
}
