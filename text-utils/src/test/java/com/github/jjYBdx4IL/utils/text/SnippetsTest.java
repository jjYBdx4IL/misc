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
import com.github.jjYBdx4IL.utils.text.Snippets;

import java.util.Map;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class SnippetsTest {

    @Test
    public void testExtract() {
        Map<String, String> result = Snippets.extract("\n"
                + "---START:ONE---\n"
                + "bla\n"
                + "---END:ONE---\n"
                + "\n");
        assertEquals(1, result.size());
        assertEquals("bla\n".replace("\n", System.lineSeparator()), result.get("ONE"));
    }

    @Test
    public void testExtractEOL() {
        Map<String, String> result = Snippets.extract("\n"
                + "---START:ONE---\n"
                + "1\n"
                + "2\r\n"
                + "3\n"
                + "---END:ONE---\n"
                + "\n");
        assertEquals(1, result.size());
        assertEquals("1\n2\n3\n".replace("\n", System.lineSeparator()), result.get("ONE"));
    }

    @Test
    public void testExtractMulti() {
        Map<String, String> result = Snippets.extract("\n"
                + "---START:ONE---\n"
                + "1\n"
                + "---END:ONE---\n"
                + "---START:TWO---\n"
                + "2\n"
                + "---END:TWO---\n"
                + "\n");
        assertEquals(2, result.size());
        assertEquals("1\n".replace("\n", System.lineSeparator()), result.get("ONE"));
        assertEquals("2\n".replace("\n", System.lineSeparator()), result.get("TWO"));
    }

    @Test
    public void testExtractNoName() {
        Map<String, String> result = Snippets.extract("\n"
                + "---START---\n"
                + "1\n"
                + "---END---\n"
                + "---START---\n"
                + "2\n"
                + "---END---\n"
                + "\n");
        assertEquals(1, result.size());
        assertEquals("2\n".replace("\n", System.lineSeparator()), result.get(Snippets.DEFAULT_SNIPPET_NAME));
    }

    @Test
    public void testExtractNested() {
        Map<String, String> result = Snippets.extract("\n"
                + "---START:ONE---\n"
                + "---START---\n"
                + "1\n"
                + "---END---\n"
                + "2\n"
                + "---END:ONE---\n"
                + "\n");
        assertEquals(1, result.size());
        assertEquals("---START---\n1\n".replace("\n", System.lineSeparator()), result.get("ONE"));
    }
}
