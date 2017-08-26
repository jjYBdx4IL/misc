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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

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
public class LogDumpInputStreamTest extends Log4jCaptureTestBase {

    @Test
    public void test() throws Exception {
        Logger slf4jTestLogger = LoggerFactory.getLogger(testLogger.getName());

        byte[] input = new byte[]{
            0, 1, 2, (byte)128, (byte)255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0};
        byte[] output = null;
        try (InputStream bais = new ByteArrayInputStream(input)) {
            try (InputStream ldis = new LogDumpInputStream(slf4jTestLogger, Level.INFO, bais)) {
                output = IOUtils.toByteArray(ldis);
            }
        }
        assertThat(input, is(output));
        assertEquals(3, events.size());
        assertEquals("00 01 02 80 ff 00 00 00 00 00 00 00 00 00 00 00   |................|", events.get(0).getRenderedMessage());
        assertEquals("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00   |................|", events.get(1).getRenderedMessage());
        assertEquals("00 EO EO                                          |.| (close, 33 bytes)", events.get(2).getRenderedMessage());
    }

}
