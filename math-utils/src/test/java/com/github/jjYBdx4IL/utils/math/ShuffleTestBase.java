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
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author jjYBdx4IL
 */
public class ShuffleTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(ShuffleTestBase.class);
    protected static final int ITERATIONS = 1000;

    protected Shuffle<Integer> s;
    protected Random r;

    @Before
    public void beforeTest() {
        r = new Random(0);
        s = new Shuffle<Integer>(r);
    }

    protected float computeDelta(ShuffleRunner<Integer> runner, int arraySize, float maxDeltaVariation) {
        float delta = 0f;
        Integer[] input = new Integer[arraySize];
        long[][] counts = new long[input.length][input.length];

        float deltaVariation;
        int loopCount = 0;
        final int iterationsPerLoop = 1000;
        long start = System.currentTimeMillis();
        do {
            float maxDelta = 0f;
            float minDelta = Float.MAX_VALUE;
            for (int n = 0; n < iterationsPerLoop; n++) {
                init(input);
                runner.shuffle(input);
                checkArray(input);
                for (int i = 0; i < input.length; i++) {
                    counts[i][input[i]]++;
                }
                delta = maxDelta(counts);
                if (delta < minDelta) {
                    minDelta = delta;
                }
                if (delta > maxDelta) {
                    maxDelta = delta;
                }
            }
            deltaVariation = maxDelta - minDelta;
            loopCount++;
        } while (loopCount < 2 || deltaVariation > maxDeltaVariation);
        long duration = System.currentTimeMillis() - start;
        LOG.info("=================");
        LOG.info(String.format("delta: %f, shuffle(T[]) calls per second: %d", delta,
                loopCount * iterationsPerLoop * 1000L / duration));
        for (long[] ia : counts) {
            LOG.info(Arrays.toString(ia));
        }

        return delta;
    }

    protected float maxDelta(long[][] iaa) {
        long maxDelta = 0;
        long totalCount = 0;
        for (long[] ia : iaa) {
            for (int j = 0; j < ia.length; j++) {
                totalCount += ia[j];
            }
        }
        long expectedCount = totalCount / iaa.length / iaa.length;
        for (long[] ia : iaa) {
            for (int j = 0; j < ia.length; j++) {
                long dd = Math.abs(ia[j] - expectedCount);
                if (dd > maxDelta) {
                    maxDelta = dd;
                }
            }
        }
        return (float) maxDelta / expectedCount;
    }

    public static void init(Integer[] ia) {
        for (int i = 0; i < ia.length; i++) {
            ia[i] = i;
        }
    }

    protected void testAlwaysTrue(int[] expectChange) {
        testAlways(true, expectChange);
    }

    protected void testAlwaysFalse(int[] expectChange) {
        testAlways(false, expectChange);
    }

    protected void testAlways(boolean result, int[] expectChange) {
        for (int i = 0; i < ITERATIONS; i++) {
            assertEquals(result, test(expectChange));
        }
    }

    private void assertEquals(boolean result, boolean test) {
        if (result != test) {
            throw new RuntimeException("assert failed");
        }
    }

    protected void testUntilTrue(int[] expectChange) {
        testUntil(true, expectChange);
    }

    protected void testUntilFalse(int[] expectChange) {
        testUntil(false, expectChange);
    }

    protected void testUntil(boolean result, int[] expectChange) {
        while (result != test(expectChange)) {
        }
    }

    protected boolean test(int[] expectChange) {
        Integer[] ba = new Integer[expectChange.length];
        init(ba);
        s.shuffle(ba);
        checkArray(ba);
        for (int i = 0; i < ba.length; i++) {
            if (expectChange[i] == 0 && ba[i] != i || expectChange[i] == 1 && ba[i] == i) {
                return false;
            }
        }
        return true;
    }

    public static void checkArray(Integer[] ba) {
        int[] flag = new int[ba.length];

        for (int i = 0; i < ba.length; i++) {
            flag[ba[i]]++;
        }
        for (int i = 0; i < flag.length; i++) {
            assertEquals(Arrays.toString(ba), 1, flag[i]);
        }
    }

    private static void assertEquals(String string, int i, int j) {
        if (i != j) {
            throw new RuntimeException(string);
        }
    }

    interface ShuffleRunner<T> {

        void shuffle(T[] t);
    }

}
