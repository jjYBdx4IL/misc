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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public class Assert {

    private static final Logger log = LoggerFactory.getLogger(Assert.class);
    private static long FILE_READ_IVAL = 1000L;
    final static long FILE_READ_IVAL_TEST = 10L;

    public static void waitUntilFileContentEquals(String expectedContent, File file, long timeout) {
        String tmp = null;
        long startTime = System.currentTimeMillis();
        do {
            if (file.exists()) {
                try (InputStream is = new FileInputStream(file)) {
                    tmp = IOUtils.toString(is);
                } catch (IOException ex) {
                    log.debug("", ex);
                }
            }
            if (!expectedContent.equals(tmp)) {
                try {
                    Thread.sleep(FILE_READ_IVAL);
                } catch (InterruptedException ex) {
                }
            }
        } while (!expectedContent.equals(tmp) && System.currentTimeMillis() < startTime + timeout);
        assertEquals(file.getAbsolutePath() + "'s content", expectedContent, tmp);
    }

    static void enableTestMode() {
        FILE_READ_IVAL = FILE_READ_IVAL_TEST;
    }

    public static void assertMatches(String[] regexes, String testString) {
        for (String regex : regexes) {
            assertMatches(regex, testString);
        }
    }

    public static void assertMatches(String regex, String testString) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(testString);
        assertTrue(String.format("regex: %s; test string: %s", regex, testString), m.find());
    }

    private Assert() {
    }

}
