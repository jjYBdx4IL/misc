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
package com.github.jjYBdx4IL.test;

import static com.github.jjYBdx4IL.test.Log4jCaptureTestBase.testLogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 *
 * @author jjYBdx4IL
 */
public class LogDumpOutputStreamTest extends Log4jCaptureTestBase {

    @Test
    public void test() throws Exception {
        Logger slf4jTestLogger = LoggerFactory.getLogger(testLogger.getName());

        byte[] input = new byte[]{
            0, 1, 2, (byte)128, (byte)255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0};
        byte[] output = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (OutputStream ldos = new LogDumpOutputStream(slf4jTestLogger, Level.INFO, baos)) {
                IOUtils.write(input, ldos);
            }
            baos.close();
            output = baos.toByteArray();
        }
        assertThat(input, is(output));
        assertEquals(3, events.size());
        assertEquals("00 01 02 80 ff 00 00 00 00 00 00 00 00 00 00 00   |................|", events.get(0).getRenderedMessage());
        assertEquals("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00   |................|", events.get(1).getRenderedMessage());
        assertEquals("00                                                |.| (close, 33 bytes)", events.get(2).getRenderedMessage());
    }

    @Test
    public void testFlush() throws IOException {
        Logger slf4jTestLogger = LoggerFactory.getLogger(testLogger.getName());

        byte[] input = new byte[]{0, 1, 2};
        byte[] output = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (OutputStream ldos = new LogDumpOutputStream(slf4jTestLogger, Level.INFO, baos, "out: ")) {
                IOUtils.write(input, ldos);
                ldos.flush();
            }
            baos.close();
            output = baos.toByteArray();
        }
        assertThat(input, is(output));
        assertEquals(2, events.size());
        assertEquals("out: 00 01 02                                          |...| (flush)", events.get(0).getRenderedMessage());
        assertEquals("out: (close, 3 bytes)", events.get(1).getRenderedMessage());
    }

}
