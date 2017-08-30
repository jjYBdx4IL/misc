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

import static org.junit.Assert.*;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//CHECKSTYLE:OFF
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class WebDiskCacheTest extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(WebDiskCacheTest.class);
    private static final WebDiskCache cache = new WebDiskCache(null, null, true);

    private static URL url;

    private Server server = null;
    private volatile int requests = 0;
    
    @Before
    public void before() throws Exception {
        server = new Server(0);
        server.setHandler(this);
        server.start();
        
        url = getUrl("/");
    }
    
    @After
    public void after() throws Exception {
        server.stop();
    }
    
    public URL getUrl(String path) throws MalformedURLException, UnknownHostException {
        ServerConnector connector = (ServerConnector) server.getConnectors()[0];
        InetAddress addr = InetAddress.getLocalHost();
        return new URL(
                String.format(Locale.ROOT, "%s://%s:%d%s", "http", addr.getHostAddress(), connector.getLocalPort(), path));
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        LOG.info(String.format(Locale.ROOT, "handle(%s, ...)", target));

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/plain");
        response.getWriter().print("example content");

        baseRequest.setHandled(true);
        
        requests++;
    }
    
    @AfterClass
    public static void afterClass() throws IOException, Exception {
        cache.close();
    }
    
    @Test
    public void testWebGet() throws Exception {
        requests = 0;
        byte[] a = cache.get(url.toExternalForm());
        assertEquals(0, requests);
        byte[] b = cache.retrieve(url.toExternalForm());
        assertEquals(1, requests);
        byte[] c = cache.get(url.toExternalForm());
        assertEquals(1, requests);
        byte[] d = cache.retrieve(url.toExternalForm());
        assertEquals(1, requests);
        assertNull(a);
        assertNotNull(b);
        assertNotNull(c);
        assertNotNull(d);
        assertArrayEquals("example content".getBytes(), b);
        assertArrayEquals("example content".getBytes(), c);
        assertArrayEquals("example content".getBytes(), d);
    }

}
