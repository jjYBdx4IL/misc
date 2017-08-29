/*
 * Copyright © 2014 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.utils.osdapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public class DisplayLevelTest {

    @Test
    public void testLargerThan() {
        assertFalse(DisplayLevel.OK.largerThan(DisplayLevel.OK));
        assertFalse(DisplayLevel.OK.largerThan(DisplayLevel.WARNING));
        assertFalse(DisplayLevel.OK.largerThan(DisplayLevel.CRITICAL));
        assertTrue(DisplayLevel.WARNING.largerThan(DisplayLevel.OK));
        assertFalse(DisplayLevel.WARNING.largerThan(DisplayLevel.WARNING));
        assertFalse(DisplayLevel.WARNING.largerThan(DisplayLevel.CRITICAL));
        assertTrue(DisplayLevel.CRITICAL.largerThan(DisplayLevel.OK));
        assertTrue(DisplayLevel.CRITICAL.largerThan(DisplayLevel.WARNING));
        assertFalse(DisplayLevel.CRITICAL.largerThan(DisplayLevel.CRITICAL));
    }

    @Test
    public void testMax() {
        List<DisplayLevel> levels = new ArrayList<>();
        assertEquals(DisplayLevel.OK, DisplayLevel.max(levels));

        levels.add(DisplayLevel.OK);
        assertEquals(DisplayLevel.OK, DisplayLevel.max(levels));

        levels.add(DisplayLevel.WARNING);
        assertEquals(DisplayLevel.WARNING, DisplayLevel.max(levels));

        levels.add(DisplayLevel.CRITICAL);
        assertEquals(DisplayLevel.CRITICAL, DisplayLevel.max(levels));
        
        levels.add(DisplayLevel.WARNING);
        levels.add(DisplayLevel.OK);
        assertEquals(DisplayLevel.CRITICAL, DisplayLevel.max(levels));
    }

}
