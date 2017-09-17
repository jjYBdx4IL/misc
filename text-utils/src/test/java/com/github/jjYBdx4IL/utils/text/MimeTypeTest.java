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

import javax.activation.MimetypesFileTypeMap;

public class MimeTypeTest {

    @Test
    public void test() {
        MimetypesFileTypeMap mimeType = MimeType.createMap();
        try {
            mimeType.getContentType((String)null);
            fail();
        } catch (NullPointerException ex) {
        }
        assertEquals("application/octet-stream", mimeType.getContentType(""));
        assertEquals("application/octet-stream", mimeType.getContentType("."));
        assertEquals("application/octet-stream", mimeType.getContentType("....."));
        assertEquals("application/octet-stream", mimeType.getContentType("."));
        assertEquals("application/javascript", mimeType.getContentType(".js"));
        assertEquals("application/javascript", mimeType.getContentType("a/b.js"));
        assertEquals("application/javascript", mimeType.getContentType("a/b.js"));
        assertEquals("text/css", mimeType.getContentType("a/b.css"));
        assertEquals("application/octet-stream", mimeType.getContentType(".pDF"));
        assertEquals("application/pdf", mimeType.getContentType(".pdf"));
    }
}
