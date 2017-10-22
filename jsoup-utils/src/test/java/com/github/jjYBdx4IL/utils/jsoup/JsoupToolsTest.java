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
package com.github.jjYBdx4IL.utils.jsoup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author jjYBdx4IL
 */
public class JsoupToolsTest {

    public JsoupToolsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of prettyFormatHtml method, of class JsoupTools.
     */
    @Test
    public void testPrettyFormatHtml() {
        String html = "<html><head><style>body{margin:0;font-family:defaultFont;letter-spacing:"
            + "0.02em;-webkit-font-smoothing:antialiased;}"
            + "h1,h2{font-family:headingFont;letter-spacing:0.05em;}#gwtfrontend{width:100%;}.GLBA</style>"
            + "</head><body><img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAANoAAABkCA"
            + "YAAADg3S7eAABP10lEQVR42u2dB5gVRdaGhyxBARUkSVBAAREliCCC\" /></body></html>";
        String expResult = "<html>\n"
            + "    <head>\n"
            + "        <style>body{margin:0;font-family:d...</style>\n"
            + "    </head>\n"
            + "    <body>\n"
            + "        <img src=\"data:image/png;base64,iVBOR...\">\n"
            + "    </body>\n"
            + "</html>";
        String result = JsoupTools.prettyFormatHtml(html, true);
        assertEquals(expResult, result);
    }

    @Test
    public void testExtractLinks() throws IOException {
        String html = "<a href=\"hTTp://one\">123</a>" +
            "<a href=\"two\">" +
            "<a href=\"javascript:asd\">" +
            "<a href=\"hTTps://three/123\">123</a>";
        List<String> urls = JsoupTools.extractLinks(html.getBytes(), "UTF-8", "http://baseuri/");
        assertEquals(3, urls.size());
        assertTrue(urls.contains("http://one"));
        assertTrue(urls.contains("http://baseuri/two"));
        assertTrue(urls.contains("https://three/123"));
    }
}
