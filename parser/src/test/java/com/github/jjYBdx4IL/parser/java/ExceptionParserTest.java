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
package com.github.jjYBdx4IL.parser.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.github.jjYBdx4IL.parser.java.ExceptionParser.ParsedException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

//CHECKSTYLE:OFF
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ExceptionParserTest {

    @Test
    public void testParseFirst2() throws Exception {
        @SuppressWarnings("deprecation")
        String input = IOUtils.toString(getRes(1));
        int exceptionLastLineIndex = 112;
        int exceptionStartLineIndex = 89;
        ParsedException result = null;

        for (int i = 0; i <= exceptionStartLineIndex; i++) {
            String part = _extractLines(input, i, exceptionLastLineIndex);
            //log.info(part);
            result = ExceptionParser.parseFirst(new ByteArrayInputStream(part.getBytes()));
            assertNotNull(result);
            //log.info(String.format("%d-%d=%s", i, lastLineIndex, result));
        }
    }

    @Test
    public void testParse() throws Exception {
        List<ParsedException> result = ExceptionParser.parse(getRes(1));
        assertEquals(1, result.size());
    }

    @Test
    public void testParseFirst() throws Exception {
        @SuppressWarnings("deprecation")
        String input = _extractLines(IOUtils.toString(getRes(1)), 90, 112);

        ParsedException result = ExceptionParser.parseFirst(new ByteArrayInputStream(input.getBytes()));
        assertNotNull(result);
        assertEquals("java.net", result.getTypePackageName());
        assertEquals("ConnectException", result.getTypeSimpleName());
        assertEquals("Connection timed out", result.getMessage());
        assertEquals("java.net.ConnectException: Connection timed out\n"
                + "	at java.net.PlainSocketImpl.socketConnect(Native Method)\n"
                + "	at java.net.AbstractPlainSocketImpl.doConnect(AbstractPlainSocketImpl.java:345)\n"
                + "	at java.net.AbstractPlainSocketImpl.connectToAddress(AbstractPlainSocketImpl.java:206)\n"
                + "	at java.net.AbstractPlainSocketImpl.connect(AbstractPlainSocketImpl.java:188)\n"
                + "	at java.net.SocksSocketImpl.connect(SocksSocketImpl.java:392)\n"
                + "	at java.net.Socket.connect(Socket.java:589)\n"
                + "	at java.net.Socket.connect(Socket.java:538)\n"
                + "	at java.net.Socket.<init>(Socket.java:434)\n"
                + "	at java.net.Socket.<init>(Socket.java:244)\n"
                + "	at CheckJNI.main(CheckJNI.java:68)\n"
                + "	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n"
                + "	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n"
                + "	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n"
                + "	at java.lang.reflect.Method.invoke(Method.java:497)\n"
                + "	at com.sun.javatest.regtest.MainWrapper$MainThread.run(MainWrapper.java:94)\n"
                + "	at java.lang.Thread.run(Thread.java:745)\n", result.getStackTrace());

    }

    public static String _extractLines(String input, int firstLineIndex, int lastLineIndex) {
        String[] lines = input.split("\\r?\\n");
        StringBuilder sb = new StringBuilder();
        for (int i = firstLineIndex; i <= lastLineIndex; i++) {
            sb.append(lines[i]).append("\n");
        }
        return sb.toString();
    }

    protected InputStream getRes(int i) throws IOException {
        return ExceptionParserTest.class.getResourceAsStream(String.format("jtreg_output_%d.txt", i));
    }

}
