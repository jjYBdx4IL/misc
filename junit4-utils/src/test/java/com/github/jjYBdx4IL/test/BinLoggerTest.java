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

import java.util.Locale;

import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;

import com.github.jjYBdx4IL.utils.junit4.PropertyRestorer;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 *
 * @author jjYBdx4IL
 */
public class BinLoggerTest extends Log4jCaptureTestBase {

    @SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(BinLoggerTest.class);
    private static final PropertyRestorer propertyRestorer = PropertyRestorer.getInstance();
    private static Logger slf4jTestLogger;

    @BeforeClass
    public static void beforeClass() {
        propertyRestorer.setDefaultLocale(Locale.ROOT);
        slf4jTestLogger = LoggerFactory.getLogger(testLogger.getName());
    }

    @AfterClass
    public static void afterClass() {
        propertyRestorer.restoreProps();
    }

    @Test
    public void testAutoFlushLine() {
        BinLogger l = new BinLogger(slf4jTestLogger, Level.TRACE, 4, "prefix:");
        l.append(0x30);
        assertEquals(0, events.size());
        l.append(0x30);
        assertEquals(0, events.size());
        l.append(0x30);
        assertEquals(0, events.size());
        l.append(0xff);
        assertEquals(1, events.size());
        assertEquals("prefix:30 30 30 ff   |000.|", events.get(0).getRenderedMessage());
        l.close();
    }

    @Test
    public void testFlush() {
        BinLogger l = new BinLogger(slf4jTestLogger, Level.TRACE, 4, "prefix:");
        l.append(0x30);
        l.flush();
        assertEquals("prefix:30            |0| (flush)", events.get(0).getRenderedMessage());
        l.close();
    }

    @Test
    public void testFlushOnEmptyLine() {
        BinLogger l = new BinLogger(slf4jTestLogger, Level.TRACE, 4, "prefix:");
        l.flush();
        assertEquals("prefix:(flush)", events.get(0).getRenderedMessage());
        l.close();
    }

    @Test
    public void testNamedFlush() {
        BinLogger l = new BinLogger(slf4jTestLogger, Level.TRACE, 4, "prefix:");
        l.append(0x30);
        l.flush("name");
        assertEquals("prefix:30            |0| (name)", events.get(0).getRenderedMessage());
        l.close();
    }

    @Test
    public void testNonPrintable() {
        BinLogger l = new BinLogger(slf4jTestLogger, Level.TRACE, 4, "prefix:");
        l.append(0x10);
        l.flush();
        assertEquals("prefix:10            |.| (flush)", events.get(0).getRenderedMessage());
        l.close();
    }

    @Test
    public void testClose() {
        BinLogger l = new BinLogger(slf4jTestLogger, Level.TRACE, 4, "prefix:");
        l.append(0x30);
        l.close();
        assertEquals("prefix:30            |0| (close, 1 bytes)", events.get(0).getRenderedMessage());
    }

    @Test
    public void testEOFClose() {
        BinLogger l = new BinLogger(slf4jTestLogger, Level.TRACE, 4, "prefix:");
        l.append(0xae);
        l.append(-1);
        l.close();
        assertEquals("prefix:ae EO         |.| (close, 1 bytes)", events.get(0).getRenderedMessage());
    }

    @Test
    public void testCloseEmptyLine() {
        BinLogger l = new BinLogger(slf4jTestLogger, Level.TRACE, 4, "prefix:");
        l.close();
        assertEquals("prefix:(close, 0 bytes)", events.get(0).getRenderedMessage());
    }

    @Test
    public void testLarge() {
        BinLogger l = new BinLogger(slf4jTestLogger, Level.TRACE, 100, "prefix:");
        for (int i=0; i<1000; i++) {
            l.append(0x30);
        }
        l.close();
        assertEquals(11, events.size());
        assertEquals("prefix:(close, 1,000 bytes)", events.get(10).getRenderedMessage());
    }

    @Test
    public void testAverageUseCase() {
        BinLogger l = new BinLogger(slf4jTestLogger, Level.TRACE, 4, "prefix:");
        l.append(0x30);
        assertEquals(0, events.size());
        l.append(0x30);
        assertEquals(0, events.size());
        l.append(0x30);
        assertEquals(0, events.size());
        l.append(0x30);
        assertEquals(1, events.size());
        assertEquals("prefix:30 30 30 30   |0000|", events.get(0).getRenderedMessage());
        l.append(0x12);
        assertEquals(1, events.size());
        l.append(0x30);
        assertEquals(1, events.size());
        l.append(0x32);
        assertEquals(1, events.size());
        l.flush();
        assertEquals(2, events.size());
        assertEquals("prefix:12 30 32      |.02| (flush)", events.get(1).getRenderedMessage());
        l.append(0x12);
        assertEquals(2, events.size());
        l.append(0x14);
        assertEquals(2, events.size());
        l.close();
        assertEquals(3, events.size());
        assertEquals("prefix:12 14         |..| (close, 9 bytes)", events.get(2).getRenderedMessage());
    }

    @Test
    public void testGetTotalByteCount() {
        BinLogger l = new BinLogger(slf4jTestLogger, Level.TRACE, 5, "prefix:");
        int i;
        for (i=0; i<=33; i++) {
            assertEquals(i, l.getTotalByteCount());
            l.append(0x30);
        }
        assertEquals(i, l.getTotalByteCount());
        l.close();
        assertEquals(i, l.getTotalByteCount());
    }
}
