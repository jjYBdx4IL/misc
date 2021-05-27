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
package com.github.jjYBdx4IL.utils.io;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TplUtilsTest {

    @Test
    public void testProcessPlaceholders() {
        assertEquals("a0b}0dd",
                TplUtils.processPlaceholders("a${1}b}${123}dd", "\\$\\{(.+?)}", new TplUtils.PlaceholderProcessor() {
            
            @Override
            public String handlePlaceholder(String arg) {
                return arg.startsWith("1") ? "0" : null;
            }
        }));
    }
}
