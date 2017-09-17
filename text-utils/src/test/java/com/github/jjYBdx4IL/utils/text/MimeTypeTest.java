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
import static org.junit.Assert.fail;

import org.junit.Test;

public class MimeTypeTest {

    @Test
    public void test() {
        MimeType mimeType = new MimeType();
        try {
            mimeType.get(null, null);
            fail();
        } catch (NullPointerException ex) {
        }
        assertEquals("application/octet-stream", mimeType.get("", null));
        assertEquals("application/octet-stream", mimeType.get(".", null));
        assertEquals("application/octet-stream", mimeType.get(".....", null));
        assertEquals("application/octet-stream", mimeType.get(".", "UTF-8"));
        assertEquals("application/javascript", mimeType.get(".js", null));
        assertEquals("application/javascript", mimeType.get("a/b.js", null));
        assertEquals("application/javascript;charset=utf-8", mimeType.get("a/b.js", "UTF-8"));
        assertEquals("text/css;charset=utf-8", mimeType.get("a/b.css", "UTF-8"));
        assertEquals("application/pdf", mimeType.get(".pDF", null));
    }
}
