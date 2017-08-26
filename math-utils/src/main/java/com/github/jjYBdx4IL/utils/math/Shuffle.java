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
import java.util.Random;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class Shuffle<T> {

    private final Random rnd;

    public Shuffle() {
        rnd = new Random();
    }

    /**
     * For testing.
     *
     * @param random initialize using existing random number generator
     */
    public Shuffle(Random random) {
        rnd = random;
    }

    /**
     * Fisher–Yates shuffle.
     * 
     * @param ar the input array
     */
    public void shuffle(T[] ar) {
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            T a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    /**
     * Possibly faster shuffle.
     *
     * @param ar minimum length is 2
     */
    public void shuffle2(T[] ar) {
        if (ar.length < 2) {
            throw new IllegalArgumentException("input array must have minimum length 2");
        }
        int nextPos = rnd.nextInt(ar.length);
        T tmp = ar[nextPos];
        int lastPos = nextPos;
        for (int i = 0; i < ar.length - 1; i++) {
            nextPos = rnd.nextInt(ar.length);
            ar[lastPos] = ar[nextPos];
            lastPos = nextPos;
        }
        ar[lastPos] = tmp;
    }
}
