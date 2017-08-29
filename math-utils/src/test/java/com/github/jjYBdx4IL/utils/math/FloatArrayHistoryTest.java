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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.github.jjYBdx4IL.utils.math.FloatArrayHistory;

import org.junit.Test;

/**
 *
 * @author jjYBdx4IL
 */
public class FloatArrayHistoryTest {

    @Test
    public void testCopy() {
        float[] f = new float[3];
        FloatArrayHistory h = new FloatArrayHistory(f.length);

        h.add(0);
        h.add(1);
        h.add(2);

        h.copy(0, f, 0, 3);
        assertEquals("[0.0, 1.0, 2.0]", Arrays.toString(f));
        h.copy(1, f, 0, 2);
        assertEquals("[1.0, 2.0, 2.0]", Arrays.toString(f));
        h.copy(2, f, 0, 1);
        assertEquals("[2.0, 2.0, 2.0]", Arrays.toString(f));

        h.add(3);

        h.copy(0, f, 0, 3);
        assertEquals("[1.0, 2.0, 3.0]", Arrays.toString(f));
        h.copy(1, f, 0, 2);
        assertEquals("[2.0, 3.0, 3.0]", Arrays.toString(f));
        h.copy(2, f, 0, 1);
        assertEquals("[3.0, 3.0, 3.0]", Arrays.toString(f));

        h.add(4);

        h.copy(0, f, 0, 3);
        assertEquals("[2.0, 3.0, 4.0]", Arrays.toString(f));
        h.copy(1, f, 0, 2);
        assertEquals("[3.0, 4.0, 4.0]", Arrays.toString(f));
        h.copy(2, f, 0, 1);
        assertEquals("[4.0, 4.0, 4.0]", Arrays.toString(f));

        try {
            h.copy(0, f, 0, 4);
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {}

        try {
            h.copy(0, f, 0, -1);
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {}

        try {
            h.copy(-1, f, 0, -1);
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {}

        try {
            h.copy(-1, f, 0, 0);
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {}
    }

    @Test
    public void testIsFullyPopulated() {
        FloatArrayHistory h = new FloatArrayHistory(3);

        h.add(0);
        assertFalse(h.isFullyPopulated());
        h.add(1);
        assertFalse(h.isFullyPopulated());
        h.add(2);
        assertTrue(h.isFullyPopulated());
    }


    @Test
    public void testGet() {
        FloatArrayHistory h = new FloatArrayHistory(3);

        h.add(0);
        h.add(1);
        h.add(2);

        assertEquals(0f, h.get(0), 1e-7);
        assertEquals(1f, h.get(1), 1e-7);
        assertEquals(2f, h.get(2), 1e-7);

        try {
            h.get(3);
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {}

        h.add(3);

        assertEquals(1f, h.get(0), 1e-7);
        assertEquals(2f, h.get(1), 1e-7);
        assertEquals(3f, h.get(2), 1e-7);

        try {
            h.get(3);
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {}
    }
}
