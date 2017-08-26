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
package com.github.jjYBdx4IL.utils.junit4;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author jjYBdx4IL
 */
public class AssertTest {

    private static final Logger LOG = LoggerFactory.getLogger(AssertTest.class);
    private final static long TIMEOUT = 120000L;
    private final static File tempDir = new File(new File(System.getProperty("basedir"), "target"), AssertTest.class.getName());
    private final static File outputFile = new File(tempDir, "output");

    @BeforeClass
    public static void beforeClass() {
        Assert.enableTestMode();
    }

    @Before
    public void before() throws IOException {
        FileUtils.deleteDirectory(tempDir);
        assertTrue(tempDir.mkdirs());
    }

    @Test(timeout = TIMEOUT)
    public void testWaitUntilFileContentEqualsNoFileTimeout() {
        final long assertTimeout = Assert.FILE_READ_IVAL_TEST * 30L;
        assertTrue(assertTimeout <= TIMEOUT / 2);

        final String expectedOutput = "Hello!";
        try {
            Assert.waitUntilFileContentEquals(expectedOutput, outputFile, assertTimeout);
            fail();
        } catch (AssertionError ex) {
        }
    }

    @Test(timeout = TIMEOUT)
    public void testWaitUntilFileContentEqualsBadContentTimeout() throws FileNotFoundException, IOException {
        final long assertTimeout = Assert.FILE_READ_IVAL_TEST * 30L;
        assertTrue(assertTimeout <= TIMEOUT / 2);

        final String expectedOutput = "Hello!";
        final String unexpectedOutput = "ByeBye!";

        try (OutputStream os = new FileOutputStream(outputFile)) {
            os.write(unexpectedOutput.getBytes());
        }

        try {
            Assert.waitUntilFileContentEquals(expectedOutput, outputFile, assertTimeout);
            fail();
        } catch (AssertionError ex) {
        }
    }

    @Test(timeout = TIMEOUT)
    public void testWaitUntilFileContentEqualsImmediateOK() throws FileNotFoundException, IOException {
        final long assertTimeout = Assert.FILE_READ_IVAL_TEST * 30L;
        assertTrue(assertTimeout <= TIMEOUT / 2);

        final String expectedOutput = "Hello!";

        try (OutputStream os = new FileOutputStream(outputFile)) {
            os.write(expectedOutput.getBytes());
        }

        Assert.waitUntilFileContentEquals(expectedOutput, outputFile, assertTimeout);
    }

    @Test(timeout = TIMEOUT)
    public void testWaitUntilFileContentEquals() throws InterruptedException {
        final long writeSleepDelayMillis = Assert.FILE_READ_IVAL_TEST * 100L;
        final long assertTimeout = writeSleepDelayMillis * 30L;

        assertTrue(assertTimeout <= TIMEOUT / 2);

        final String expectedOutput = "Hello!";
        final String unexpectedOutput = "ByeBye!";
        final AtomicBoolean started = new AtomicBoolean(false);
        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    synchronized (started) {
                        started.set(true);
                        started.notifyAll();
                    }
                    Thread.sleep(writeSleepDelayMillis);
                    FileUtils.writeByteArrayToFile(outputFile, unexpectedOutput.getBytes("UTF-8"));
                    Thread.sleep(writeSleepDelayMillis);
                    FileUtils.writeByteArrayToFile(outputFile, expectedOutput.getBytes("UTF-8"));
                } catch (Exception ex) {
                    LOG.error("", ex);
                }
            }
        };
        Thread t = new Thread(r);
        synchronized (started) {
            t.start();
            while (!started.get()) {
                try {
                    started.wait();
                } catch (InterruptedException ex) {
                    LOG.error("", ex);
                }
            }
        }
        Assert.waitUntilFileContentEquals(expectedOutput, outputFile, assertTimeout);
        t.join(TIMEOUT * 11 / 10);
    }

}
