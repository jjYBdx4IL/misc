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

import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.io.Closeable;
import java.util.Arrays;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public class BinLogger implements Closeable {

    private static final String BYTE_SPACE = " ";
    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
    private static final String EOF = "EO";
    public static final int DEFAULT_BYTES_PER_LINE = 16;
    public static final String DEFAULT_LINE_PREFIX = "";

    private final String linePrefix;
    private final int maxBytesPerLine;
    private int lineByteCount = 0;
    protected final int expectedLineLength;
    private final Logger logger;
    private final Level logLevel;
    private StringBuilder sb;
    private StringBuilder sbAscii;
    private final String emptyString;
    private long totalByteCount = 0L;

    public BinLogger(Logger logger, Level logLevel) {
        this(logger, logLevel, DEFAULT_BYTES_PER_LINE, DEFAULT_LINE_PREFIX);
    }

    public BinLogger(Logger logger, Level logLevel, int bytesPerLine) {
        this(logger, logLevel, bytesPerLine, DEFAULT_LINE_PREFIX);
    }

    public BinLogger(Logger logger, Level logLevel, String linePrefix) {
        this(logger, logLevel, DEFAULT_BYTES_PER_LINE, linePrefix);
    }

    public BinLogger(Logger logger, Level logLevel, int maxBytesPerLine, String linePrefix) {
        if (logger == null || maxBytesPerLine < 1 || logLevel == null || linePrefix == null) {
            throw new IllegalArgumentException();
        }
        this.logger = logger;
        this.logLevel = logLevel;
        this.maxBytesPerLine = maxBytesPerLine;
        this.linePrefix = linePrefix;
        this.expectedLineLength = linePrefix.length() + maxBytesPerLine * (2 + BYTE_SPACE.length()) + 20 + 10;
        sb = new StringBuilder(expectedLineLength);
        sbAscii = new StringBuilder(16);
        sb.append(linePrefix);

        byte[] spaces = new byte[maxBytesPerLine * 3];
        Arrays.fill(spaces, (byte) 0x20);
        emptyString = new String(spaces);
    }

    public int append(int c) {
        if (lineByteCount > 0) {
            sb.append(BYTE_SPACE);
        }
        if (c < 0) {
            sb.append(EOF);
        } else {
            sb.append(HEX_DIGITS[(c >> 4) & 0x0F]);
            sb.append(HEX_DIGITS[c & 0x0F]);
            sbAscii.append(c >= 32 && c < 127 ? Character.toString((char) c) : ".");
            totalByteCount++;
        }
        lineByteCount++;
        if (lineByteCount >= maxBytesPerLine) {
            switch (logLevel) {
                case TRACE: logger.trace(consumeLogLine()); break;
                case DEBUG: logger.debug(consumeLogLine()); break;
                case INFO: logger.info(consumeLogLine()); break;
                case WARN: logger.warn(consumeLogLine()); break;
                default: logger.error(consumeLogLine()); break;
            }
        }
        return c;
    }

    protected String consumeLogLine() {
        if (lineByteCount == 0) {
            return null;
        }

        sb.append(emptyString.substring(0, (maxBytesPerLine - lineByteCount) * 3));
        sb.append("   |");
        sb.append(sbAscii.toString());
        sb.append("|");

        String s = sb.toString();
        sb = new StringBuilder(expectedLineLength);
        sbAscii = new StringBuilder(16);
        sb.append(linePrefix);
        lineByteCount = 0;
        return s;
    }

    @Override
    public void close() {
        String s = consumeLogLine();
        String msg;
        if (s != null) {
            msg = String.format("%s (close, %,d bytes)", s, totalByteCount);
        } else {
            msg = String.format("%s(close, %,d bytes)", linePrefix, totalByteCount);
        }
        switch (logLevel) {
            case TRACE: logger.trace(msg); break;
            case DEBUG: logger.debug(msg); break;
            case INFO: logger.info(msg); break;
            case WARN: logger.warn(msg); break;
            default: logger.error(msg); break;
        }
    }

    void flush(String reason) {
        String s = consumeLogLine();
        String msg;
        if (s != null) {
            msg = s + " (" + reason + ")";
        } else {
            msg = linePrefix + "(" + reason + ")";
        }
        switch (logLevel) {
            case TRACE: logger.trace(msg); break;
            case DEBUG: logger.debug(msg); break;
            case INFO: logger.info(msg); break;
            case WARN: logger.warn(msg); break;
            default: logger.error(msg); break;
        }
    }

    void flush() {
        flush("flush");
    }

    /**
     * @return the totalByteCount
     */
    public long getTotalByteCount() {
        return totalByteCount;
    }
}
