/*
 * Copyright © 2016 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.diskcache;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.github.jjYBdx4IL.test.AdHocHttpServer;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

//CHECKSTYLE:OFF
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class WebDiskCacheTest {

    private static final Logger LOG = LoggerFactory.getLogger(WebDiskCacheTest.class);
    private static final WebDiskCache cache = new WebDiskCache(null, null, true);

    private static AdHocHttpServer server;
    private static URL url;
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        server = new AdHocHttpServer();
        url = server.addStaticContent("/", new AdHocHttpServer.StaticResponse("example content"));
    }
    
    @AfterClass
    public static void afterClass() throws IOException, Exception {
        cache.close();
        server.close();
    }
    
    @Test
    public void testWebGet() throws Exception {
        byte[] a = cache.get(url.toExternalForm());
        byte[] b = cache.retrieve(url.toExternalForm());
        byte[] c = cache.get(url.toExternalForm());
        assertNull(a);
        assertNotNull(b);
        assertNotNull(c);
        assertArrayEquals(b, c);
        assertArrayEquals("example content".getBytes(), c);
    }

}
