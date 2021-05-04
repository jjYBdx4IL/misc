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
import com.github.jjYBdx4IL.utils.net.URIBuilder;

import java.net.URISyntaxException;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class URIBuilderTest {

	@Ignore
    @Test
    public void testToString() throws URISyntaxException {
        assertEquals("http://host.de", new URIBuilder("http", "host.de", null).toString());
        assertEquals("http://host.de", new URIBuilder("http", "host.de", "").toString());
        try {
            new URIBuilder("http", "host.de", "path", "arg1");
            fail();
        } catch (URISyntaxException ex) {
        }
        assertEquals("http://host.de?key=",
                new URIBuilder("http", "host.de", null, "key", null).toString());
        assertEquals("http://host.de?key=",
                new URIBuilder("http", "host.de", "", "key", null).toString());
        assertEquals("http://host.de/path?key=",
                new URIBuilder("http", "host.de", "/path", "key", null).toString());
        assertEquals("http://host.de/path?key=",
                new URIBuilder("http", "host.de", "/path", "key", "").toString());
        assertEquals("http://host.de/path?key=%20",
                new URIBuilder("http", "host.de", "/path", "key", " ").toString());
        assertEquals("http://host.de:81/path?key=%20",
                new URIBuilder("http", "host.de", 81, "/path", "key", " ").toString());
        assertEquals("http://host.de:81/path?key=%5ETNX",
                new URIBuilder("http", "host.de", 81, "/path", "key", "^TNX").toString());
        // no list support:
        assertEquals("http://host.de:81/path?key=2",
                new URIBuilder("http", "host.de", 81, "/path", "key", "1", "key", "2").toString());
    }
}
