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

import com.github.jjYBdx4IL.utils.text.UnobtrusiveFrontmatterUpdater.FormatException;
import org.junit.Test;

public class UnobtrusiveFrontmatterUpdaterTest {

    UnobtrusiveFrontmatterUpdater ufu = new UnobtrusiveFrontmatterUpdater();
    
    @Test(expected = FormatException.class)
    public void testUpdateEmpty() throws FormatException {
        ufu.update("");
    }

    @Test
    public void testUpdateEmptyFm() throws FormatException {
        String input = "---\n"
            + "---\n";
        assertEquals(input, ufu.update(input));
        assertEquals("---\na: b\n---\n", ufu.update(input, "a", "b"));
        assertEquals("---\na: b\nc: d\n---\n", ufu.update(input, "a", "b", "c", "d"));
    }
    
    @Test
    public void testUpdate() throws FormatException {
        String input = "---\n"
            + "a: B\n"
            + " Z: z\n"
            + "---\n"
            + "l1\n"
            + "l2\n";
        assertEquals("---\nc: d\na: b\n Z: z\n---\nl1\nl2\n", ufu.update(input, "a", "b", "c", "d"));
    }
    
    @Test
    public void testUpdate2() throws FormatException {
        String input = "---\n"
            + "title: test\n"
            + "---\n"
            + "content\n";
        assertEquals("---\nid: 123\ntitle: test\n---\ncontent\n", ufu.update(input, "id", "123"));
    }
}
