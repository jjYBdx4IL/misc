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

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class QuoteTokenizerTest {

    @Test
    public void testTokenize() {
        assertEquals("", tokenize(""));
        assertEquals("a", tokenize("a"));
        assertEquals("a,a", tokenize("a   a"));
        assertEquals("a, ,a", tokenize("a   ' '   a"));
        assertEquals("a,     ,a", tokenize("a '     ' a"));
        assertEquals("a,     ,a", tokenize("a \"     \" a"));
        assertEquals("a,     a", tokenize("a \"     \"a"));
        assertEquals("a,-     a", tokenize("a -\"     \"a"));
    }
    
    public static String tokenize(String input) {
        return StringUtils.join(new QuoteTokenizer().tokenize(input), ",");
    }
}
