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
import static org.junit.Assert.*;

import com.github.jjYBdx4IL.utils.math.MatrixFormatter;

import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class MatrixFormatterTest {

    @Test
    public void testFormat1() {
        final int[][] testArr = new int[][]{{1, 2}, {3, 4}};
        String result = MatrixFormatter.format(null, testArr, "\n");
        assertEquals("1 2\n3 4\n", result);
    }

    @Test
    public void testFormat2() {
        final int[][] testArr = new int[][]{{1, -2}, {3, 4}};
        String result = MatrixFormatter.format(null, testArr, "\n");
        assertEquals("1 -2\n3  4\n", result);
    }

    @Test
    public void testFormat3() {
        final int[][] testArr = new int[][]{{1, -2}, {3, 444}};
        String result = MatrixFormatter.format(null, testArr, "\n");
        assertEquals("1  -2\n3 444\n", result);
    }

    @Test
    public void testFormat4() {
        final int[][] testArr = new int[][]{{1, -2}, {333, 4}};
        String result = MatrixFormatter.format(null, testArr, "\n");
        assertEquals("  1 -2\n333  4\n", result);
    }

}
