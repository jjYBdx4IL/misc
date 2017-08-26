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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * Simple URL testing methods.
 *
 * <p>
 * Think about using org.eclipse.jetty.http.HttpTester instead.</p>
 *
 * @author jjYBdx4IL
 */
//CHECKSTYLE:OFF
public class URLTester {

    public static void assertExists(String urlString) throws IOException {
        URL url = new URL(urlString);

        try (InputStream is = url.openStream()) {
        } catch (FileNotFoundException ex) {
            fail();
        }
    }

    public static void assertNotFound(String urlString) throws IOException {
        URL url = new URL(urlString);

        Map<String, List<String>> fields = url.openConnection().getHeaderFields();
        String httpStatusLine = fields.get(null).get(0);
        assertTrue(httpStatusLine, httpStatusLine.toLowerCase().matches("^\\S+\\s+404\\s+.*$"));
    }

    public static void assertNotExists(String urlString) throws IOException {
        URL url = new URL(urlString);

//        Map<String, List<String>> fields = url.openConnection().getHeaderFields();
//        for(String key : fields.keySet()) {
//            for(String value : fields.get(key)) {
//                log.info(key + " => " + value);
//            }
//        }

        try (InputStream is = url.openStream()) {
            fail();
        } catch (FileNotFoundException ex) {
        }
    }

    public static String getContent(String urlString) throws IOException {
        URL url = new URL(urlString);
        return IOUtils.toString(url.openStream());
    }

    /**
     * Requests and returns partial http content starting at a specific position.
     * <p>
     * <b>Untested!</b>
     * <p>
     * Reads until the end of the stream.
     * <p>
     *
     * @param urlString the url to decode
     * @param firstBytePosition the offset
     * @return partial http content starting at a specific position
     * @throws IOException on error
     */
    public static String getContent(String urlString, long firstBytePosition) throws IOException {
        return getContent(urlString, firstBytePosition, -1L);
    }

    /**
     * Requests and returns partial http content starting and ending at specific positions.
     * <p>
     * <b>Untested!</b>
     * <p>
     * Behaves like {@link #getContent(String,long)} if
     * <code>lastBytePosition &lt;= firstBytePosition</code>.
     * <p>
     *
     * @param urlString the url to decode
     * @param firstBytePosition starting at 0
     * @param lastBytePosition starting at 0
     * @return partial http content starting and ending at specific positions
     * @throws IOException on error
     */
    public static String getContent(String urlString, long firstBytePosition, long lastBytePosition)
            throws IOException {
        final String rangeParamValue;
        if (lastBytePosition <= firstBytePosition) {
            rangeParamValue = String.format("bytes=%1$d-", firstBytePosition);
        } else {
            rangeParamValue = String.format("bytes=%1$d-%2$d", firstBytePosition, lastBytePosition);
        }

        URL u = new URL(urlString);
        URLConnection c = u.openConnection();
        c.setRequestProperty("Range", rangeParamValue);
        return IOUtils.toString(c.getInputStream());
    }

    /**
     * Returns all header fields values.
     * 
     * @param urlString the url to decode
     * @return the map
     * @throws IOException on error
     */
    @SuppressWarnings("unused")
	public static Map<String, List<String>> getHeaderFields(String urlString) throws IOException {
        URL u = new URL(urlString);
        URLConnection c = u.openConnection();
        String content = IOUtils.toString(c.getInputStream());
        return c.getHeaderFields();
    }

    public static void assertContains(String urlString, String needle) throws IOException {
        assertTrue(getContent(urlString).indexOf(needle) > -1);
    }
}
