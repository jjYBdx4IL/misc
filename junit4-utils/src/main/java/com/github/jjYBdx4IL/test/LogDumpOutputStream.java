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
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.io.IOException;
import java.io.OutputStream;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public class LogDumpOutputStream extends OutputStream {

    private static final Logger log = LoggerFactory.getLogger(LogDumpOutputStream.class);
    private final OutputStream orig;
    private final BinLogger binLogger;

    public LogDumpOutputStream(Logger logger, Level logLevel, OutputStream orig) {
        this(logger, logLevel, orig, BinLogger.DEFAULT_LINE_PREFIX);
    }

    public LogDumpOutputStream(Logger logger, Level logLevel, OutputStream orig, String linePrefix) {
        if (orig == null || logger == null || logLevel == null || linePrefix == null) {
            throw new IllegalArgumentException();
        }
        this.orig = orig;
        this.binLogger = new BinLogger(logger, logLevel, linePrefix);
    }

    @Override
    public void write(int c) throws IOException {
        binLogger.append(c & 0xFF);
        orig.write(c);
    }

    @Override
    public void close() throws IOException {
        try {
            binLogger.close();
        } catch (Exception ex) {
            log.error("failed to close binary stream logger", ex);
        }
        orig.close();
    }

    @Override
    public void flush() throws IOException {
        binLogger.flush();
        orig.flush();
    }

    public long getByteCount() {
        return binLogger.getTotalByteCount();
    }
}
