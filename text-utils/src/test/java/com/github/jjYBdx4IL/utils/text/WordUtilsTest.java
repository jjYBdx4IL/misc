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

//CHECKSTYLE:OFF
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class WordUtilsTest {

    public WordUtilsTest() {
    }

    /**
     * Test of camelCase method, of class WordUtils.
     */
    @Test
    public void testCamelCase() {
        assertEquals("", WordUtils.camelCase(""));
        assertEquals("a", WordUtils.camelCase("a"));
        assertEquals("1", WordUtils.camelCase("1"));
        assertEquals("a", WordUtils.camelCase("_a"));
        assertEquals("aA", WordUtils.camelCase("a_a"));
        assertEquals("aA", WordUtils.camelCase("a a"));
        assertEquals("aABC", WordUtils.camelCase("a a b c"));
        assertEquals("josefinSansRegular", WordUtils.camelCase("JosefinSans-Regular"));
    }

}
