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
import static org.junit.Assert.fail;

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
    
    @Test
    public void testsq() {
        assertEquals("one=1, two=2, three='3', %q", StringUtil.sq("one=%d, two=%s, three=%q, %%q", 1, "2", "3"));
        
        assertEquals("%", StringUtil.sq("%%"));
        assertEquals("%", StringUtil.sq("%"));
        assertEquals("", StringUtil.sq(""));
        
        assertEquals("1", StringUtil.sq("%d", 1));
        assertEquals("-1", StringUtil.sq("%d", -1));
        assertEquals("-1", StringUtil.sq("%d", -1L));
        
        assertEquals("1", StringUtil.sq("%s", "1"));
        
        assertEquals("'1'", StringUtil.sq("%q", "1"));
        assertEquals("'1'\\''2'", StringUtil.sq("%q", "1'2"));
        
        try {
            StringUtil.sq("%d", 1.f);
            fail();
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().contains("#0 has wrong type"));
        }
        
        try {
            StringUtil.sq("%d %s", 1, 2);
            fail();
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().contains("#1 has wrong type"));
        }
        
    }

}
