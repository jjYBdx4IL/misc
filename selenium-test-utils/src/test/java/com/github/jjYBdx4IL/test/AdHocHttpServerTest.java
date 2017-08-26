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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class AdHocHttpServerTest {

    private static final Logger LOG = LoggerFactory.getLogger(AdHocHttpServerTest.class);
    private static AdHocHttpServer server;

    @Before
    public void before() throws Exception {
        server = new AdHocHttpServer();
    }

    @After
    public void after() throws Exception {
        server.close();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        server.close();
    }

    @Test
    public void testAddStaticContent() throws MalformedURLException, IOException {
        URL url1 = server.addStaticContent("/test1", new AdHocHttpServer.StaticResponse("CONTENT1"));
        URL url2 = server.addStaticContent("/test2", new AdHocHttpServer.StaticResponse("CONTENT2"));
        LOG.info("url1 = " + url1.toExternalForm());
        LOG.info("url2 = " + url2.toExternalForm());
        assertEquals("CONTENT1", IOUtils.toString(url1, "UTF-8"));
        assertEquals("CONTENT2", IOUtils.toString(url2, "UTF-8"));
    }

    @Test
    public void testAddStaticContentOverwrite() throws MalformedURLException, IOException {
        URL url1 = server.addStaticContent("/test1", new AdHocHttpServer.StaticResponse("CONTENT1"));
        assertEquals("CONTENT1", IOUtils.toString(url1, "UTF-8"));
        url1 = server.addStaticContent("/test1", new AdHocHttpServer.StaticResponse("CONTENT2"));
        assertEquals("CONTENT2", IOUtils.toString(url1, "UTF-8"));
    }

    @Test
    public void testParallelInstancesAndAutoCloseable() throws Exception {
        try (AdHocHttpServer server2 = new AdHocHttpServer()) {
            // positive tests
            URL url1 = server.addStaticContent("/test1", new AdHocHttpServer.StaticResponse("CONTENT1"));
            URL url2 = server2.addStaticContent("/test2", new AdHocHttpServer.StaticResponse("CONTENT2"));
            LOG.info("url1 = " + url1.toExternalForm());
            LOG.info("url2 = " + url2.toExternalForm());
            assertEquals("CONTENT1", IOUtils.toString(url1, "UTF-8"));
            assertEquals("CONTENT2", IOUtils.toString(url2, "UTF-8"));

            // negative tests
            String url1b = url1.toExternalForm().replaceFirst("1$", "2");
            String url2b = url2.toExternalForm().replaceFirst("2$", "1");
            LOG.info("url1b = " + url1b);
            URLTester.assertNotFound(url1b);
            LOG.info("url2b = " + url2b);
            URLTester.assertNotFound(url2b);
        }
    }

    @Test
    public void testRedirect() throws MalformedURLException, IOException {
        URL url2 = server.addStaticContent("/test2", new AdHocHttpServer.StaticResponse("CONTENT2"));
        URL url1 = server.addStaticContent("/test1",
                new AdHocHttpServer.StaticResponse(url2.toExternalForm(), HttpServletResponse.SC_MOVED_PERMANENTLY));
        assertEquals("CONTENT2", IOUtils.toString(url1, "UTF-8"));
    }

    @Test
    public void testWithRootDir() throws MalformedURLException, IOException, Exception {
        server.close();
        server = new AdHocHttpServer(new File(getClass().getResource("someServerRoot").toURI()));

        URL url1 = server.addStaticContent("/test1", new AdHocHttpServer.StaticResponse("CONTENT1"));
        URL url2 = server.addStaticContent("/test2", new AdHocHttpServer.StaticResponse("CONTENT2"));
        LOG.info("url1 = " + url1.toExternalForm());
        LOG.info("url2 = " + url2.toExternalForm());
        assertEquals("CONTENT1", IOUtils.toString(url1, "UTF-8"));
        assertEquals("CONTENT2", IOUtils.toString(url2, "UTF-8"));

        assertEquals("aaa\n", IOUtils.toString(server.computeServerURL("/a.txt"), "UTF-8"));
    }

}
