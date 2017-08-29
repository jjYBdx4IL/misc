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
import java.math.BigInteger;

import static org.junit.Assert.*;

import com.github.jjYBdx4IL.utils.text.FileSizeFormatter;

import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
// CHECKSTYLE IGNORE MagicNumber FOR NEXT 1000 LINES
public class FileSizeFormatterTest {

    /**
     * Test of byteCountToDisplaySize method, of class FileUtils.
     */
    @Test
    public void testByteCountToDisplaySize() {
        assertEquals("99 B", FileSizeFormatter.byteCountToDisplaySize(99L));
        assertEquals("999 B", FileSizeFormatter.byteCountToDisplaySize(999L));
        assertEquals("1.0 KB", FileSizeFormatter.byteCountToDisplaySize(1000L));
        assertEquals("1.0 KB", FileSizeFormatter.byteCountToDisplaySize(1023L));
        assertEquals("1.1 KB", FileSizeFormatter.byteCountToDisplaySize(1124L));
        assertEquals("1.1 KB", FileSizeFormatter.byteCountToDisplaySize(1164L));
        assertEquals("999 KB", FileSizeFormatter.byteCountToDisplaySize(1024L * 999L + 511L));
        assertEquals("1.0 MB", FileSizeFormatter.byteCountToDisplaySize(1024L * 999L + 512L));
        assertEquals("1.0 MB", FileSizeFormatter.byteCountToDisplaySize(1024L * 1024L - 1L));
        assertEquals("1.0 GB", FileSizeFormatter.byteCountToDisplaySize(1024L * 1024L * 1024L - 1L));
        assertEquals("1.0 TB", FileSizeFormatter.byteCountToDisplaySize(1024L * 1024L * 1024L * 1024L - 1L));
        assertEquals("1.0 PB", FileSizeFormatter.byteCountToDisplaySize(1024L * 1024L * 1024L * 1024L * 1024L - 1L));
        assertEquals("1.0 EB", FileSizeFormatter.byteCountToDisplaySize(
                1024L * 1024L * 1024L * 1024L * 1024L * 1024L - 1L));
        BigInteger bi = BigInteger.valueOf(1024L * 1024L * 1024L * 1024L * 1024L * 1024L);
        bi = bi.multiply(BigInteger.valueOf(1024L));
        assertEquals("1.0 ZB", FileSizeFormatter.byteCountToDisplaySize(bi));
        bi = bi.multiply(BigInteger.valueOf(1024L));
        assertEquals("1.0 YB", FileSizeFormatter.byteCountToDisplaySize(bi));
        bi = bi.multiply(BigInteger.valueOf(1024L));
        assertEquals("1024 YB", FileSizeFormatter.byteCountToDisplaySize(bi));
        bi = bi.multiply(BigInteger.valueOf(1024L));
        assertEquals("1048576 YB", FileSizeFormatter.byteCountToDisplaySize(bi));

        assertEquals("0 KB", FileSizeFormatter.byteCountToDisplaySize(100L, 2));
        assertEquals("1 KB", FileSizeFormatter.byteCountToDisplaySize(1000L, 2));
        assertEquals("1 KB", FileSizeFormatter.byteCountToDisplaySize(1023L, 2));
        assertEquals("20 KB", FileSizeFormatter.byteCountToDisplaySize(19L * 1024L + 512L, 2));
        assertEquals("20 KB", FileSizeFormatter.byteCountToDisplaySize(19L * 1024L + 512L, 3));
        assertEquals("19.5 KB", FileSizeFormatter.byteCountToDisplaySize(19L * 1024L + 512L, 4));
        assertEquals("196 KB", FileSizeFormatter.byteCountToDisplaySize(195L * 1024L + 512L, 4));

        assertEquals("0 MB", FileSizeFormatter.byteCountToDisplaySize(19L * 1024L + 512L, 1));

        assertEquals("1 KB", org.apache.commons.io.FileUtils.byteCountToDisplaySize(2047));
    }
}
