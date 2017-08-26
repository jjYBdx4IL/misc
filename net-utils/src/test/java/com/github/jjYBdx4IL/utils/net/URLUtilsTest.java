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
package com.github.jjYBdx4IL.utils.net;

//CHECKSTYLE:OFF
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class URLUtilsTest {

    @Test
    public void testHyperlinkText() {
        assertEquals("http://", URLUtils.hyperlinkText("http://", null));
        assertEquals("<a href=\"http://a\">http://a</a>", URLUtils.hyperlinkText("http://a", null));
        assertEquals("<a href=\"hTTp://a\">hTTp://a</a>", URLUtils.hyperlinkText("hTTp://a", null));
        assertEquals("<a href=\"http://a/%20c\">http://a/%20c</a>", URLUtils.hyperlinkText("http://a/%20c", null));
        assertEquals("&amp;&lt;&gt;   <a href=\"http://a\">http://a</a>", URLUtils.hyperlinkText("&<>   http://a", null));
    }

    @Test
    public void testHyperlinkUrls() {
        assertEquals(0, URLUtils.hyperlinkUrls("http://").size());
        assertEquals("http://a", URLUtils.hyperlinkUrls("http://a").get(0));
        assertEquals("hTTp://a", URLUtils.hyperlinkUrls("hTTp://a").get(0));
        assertEquals("http://a/%20c", URLUtils.hyperlinkUrls("http://a/%20c").get(0));
        assertEquals("http://a", URLUtils.hyperlinkUrls("&<>   http://a").get(0));
    }

    @Test
    public void testReadParamsIntoMap() throws URISyntaxException {
        Map<String, String> params = URLUtils.readParamsIntoMap("http://test/?&arg1=value1%3D&arg2=value2", "UTF-8");
        assertEquals("value1=", params.get("arg1"));
        assertEquals("value2", params.get("arg2"));
        assertEquals(2, params.size());
    }

    @Test
    public void testGetQueryParams() throws UnsupportedEncodingException {
        Map<String, List<String>> params = URLUtils.getQueryParams("http://test/?&arg1=value1%3D&arg2=value2&&arg2=value3");
        assertEquals("value1=", params.get("arg1").get(0));
        assertEquals("value2", params.get("arg2").get(0));
        assertEquals("value3", params.get("arg2").get(1));
        assertEquals(2, params.size());
    }

}
