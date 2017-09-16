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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.awt.Dimension;
import java.awt.Point;

public class LineFeedPackingTest {

    @Test
    public void test() {
        LineFeedPacking lfp;

        lfp = new LineFeedPacking(
            new int[] { 1 },
            new int[] { 1 },
            1f);
        assertEquals(0, lfp.fit());
        assertEquals(new Dimension(1, 1), lfp.getLayoutTotalSize());
        assertArrayEquals(new Point[] { new Point(0, 0) }, lfp.getLayoutOffsets().toArray());

        lfp = new LineFeedPacking(
            new int[] { 3, 1 },
            new int[] { 1, 1 },
            1f);
        assertEquals(1, lfp.fit());
        assertEquals(new Dimension(3, 2), lfp.getLayoutTotalSize());
        assertArrayEquals(new Point[] { new Point(0, 0), new Point(0, 1) },
            lfp.getLayoutOffsets().toArray());

        lfp = new LineFeedPacking(
            new int[] { 3, 1 },
            new int[] { 1, 1 },
            2f);
        assertEquals(0, lfp.fit());
        assertEquals(new Dimension(4, 1), lfp.getLayoutTotalSize());
        assertArrayEquals(new Point[] { new Point(0, 0), new Point(3, 0) },
            lfp.getLayoutOffsets().toArray());

        lfp = new LineFeedPacking(
            new int[] { 1, 1, 1, 1 },
            new int[] { 1, 1, 1, 1 },
            1f);
        assertEquals(2, lfp.fit());
        assertEquals(new Dimension(2, 2), lfp.getLayoutTotalSize());
        assertArrayEquals(new Point[] { new Point(0, 0), new Point(1, 0), new Point(0, 1), new Point(1, 1) },
            lfp.getLayoutOffsets().toArray());

        lfp = new LineFeedPacking(
            new int[] { 1, 1, 1, 1 },
            new int[] { 1, 1, 1, 1 },
            2f);
        assertEquals(0, lfp.fit());
        assertEquals(new Dimension(4, 1), lfp.getLayoutTotalSize());
        assertArrayEquals(new Point[] { new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(3, 0) },
            lfp.getLayoutOffsets().toArray());

        lfp = new LineFeedPacking(
            new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            1f);
        assertEquals(4 + 32, lfp.fit());
        assertEquals(new Dimension(3, 3), lfp.getLayoutTotalSize());
        assertArrayEquals(
            new Point[] {
                new Point(0, 0), new Point(1, 0), new Point(2, 0),
                new Point(0, 1), new Point(1, 1), new Point(2, 1),
                new Point(0, 2), new Point(1, 2), new Point(2, 2), },
            lfp.getLayoutOffsets().toArray());
    }
}
