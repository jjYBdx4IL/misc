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

import org.junit.Test;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class UnicodeTest {

    @Test
    public void testRemoveNonprintableCharacters() throws UnsupportedEncodingException {
        // http://stackoverflow.com/questions/32512359/java-8-handling-of-invalid-utf-8-encodings-different-than-java-7
        //log.info(new String(new byte[]{-7, 'a'}, "UTF-8").length());

        assertEquals("\ufffd", r(new byte[]{0}));
        // jdk7:
        //assertEquals("�", r(new byte[]{-7, 'a'}));
        // jdk8: (???)
        assertEquals("\ufffda", r(new byte[]{-7, 'a'}));
    }

    private String r(byte[] bytes) throws UnsupportedEncodingException {
        return Unicode.removeNonprintableCharacters(new String(bytes, "UTF-8"));
    }

}
