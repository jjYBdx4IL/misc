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
package com.github.jjYBdx4IL.utils.math;

//CHECKSTYLE:OFF
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

import com.github.jjYBdx4IL.utils.math.DigestFormatter;

import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class DigestFormatterTest {

    public static final String TEST_INPUT = "abc";

    @Test
    public void testMd5() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        assertNotNull(md);
        byte[] digest = md.digest(TEST_INPUT.getBytes("ASCII"));

        assertEquals("900150983cd24fb0d6963f7d28e17f72", DigestFormatter.md5(digest));
    }

    @Test
    public void testSha1() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        assertNotNull(md);
        byte[] digest = md.digest(TEST_INPUT.getBytes("ASCII"));

        assertEquals("a9993e364706816aba3e25717850c26c9cd0d89d", DigestFormatter.sha1(digest));
    }

}
