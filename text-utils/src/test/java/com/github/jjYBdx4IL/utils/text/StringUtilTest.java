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
package com.github.jjYBdx4IL.utils.text;

import static com.github.jjYBdx4IL.utils.text.StringUtil.haveEqualSets;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;

//CHECKSTYLE:OFF
public class StringUtilTest {

    @Test
    public void testF() {
        assertEquals("0123 - abc - 1.02", StringUtil.f("%04d - %s - %.2f", 123, "abc", 1.023f));
    }
    
    @Test
    public void testHaveEqualSets() {
        assertTrue(haveEqualSets(Arrays.asList(), Arrays.asList()));
        assertTrue(haveEqualSets(Arrays.asList(), null));
        assertTrue(haveEqualSets(null, Arrays.asList()));
        assertTrue(haveEqualSets(null, null));
        assertTrue(haveEqualSets(Arrays.asList("a", "b"), Arrays.asList("b", "a")));
        assertTrue(haveEqualSets(Arrays.asList("a", "b"), Arrays.asList("b", "a", "a")));
        assertTrue(haveEqualSets(Arrays.asList("a"), Arrays.asList("a")));
    }

}
