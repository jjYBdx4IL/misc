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
package com.github.jjYBdx4IL.wsverifier;

import static org.junit.Assert.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brucezee.jspider.Page;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebsiteVerifierTest extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(WebsiteVerifierTest.class);

    Server server = null;
    String serverUrl = null;

    @Before
    public void before() throws Exception {
        server = new Server(0);
        server.setHandler(this);
        server.start();
        serverUrl = getURL().toExternalForm();
        LOG.info("server URL: " + serverUrl);
    }

    @After
    public void after() throws Exception {
        if (server != null) {
            server.stop();
            server = null;
        }
    }

    @Test
    public void test() throws Exception {
        WebsiteVerifier verifier = new WebsiteVerifier();
        assertFalse(verifier.verify(serverUrl));
        assertFalse(verifier.isOk());

        LOG.info(verifier.resultToString());

        Set<String> badUrls = verifier.getBadUrls();
        assertEquals(3, badUrls.size());
        LOG.info("" + badUrls);
        assertTrue(badUrls.contains(serverUrl + "a.png"));
        assertTrue(badUrls.contains(serverUrl + "invalid"));
        assertTrue(badUrls.contains(serverUrl + "bad.zip"));
        assertFalse(badUrls.contains(serverUrl + "good.zip"));
        Set<String> referralUrls = verifier.getPagesContainingUrl(serverUrl + "a.png");
        assertEquals(1, referralUrls.size());
        assertTrue(referralUrls.contains(serverUrl));
    }
    
    @Test
    public void testExclusions() throws Exception {
        WebsiteVerifier verifier = new WebsiteVerifier();
        assertFalse(verifier.verify(serverUrl, "^invalid$"));
        assertFalse(verifier.isOk());

        LOG.info(verifier.resultToString());

        Set<String> badUrls = verifier.getBadUrls();
        assertEquals(2, badUrls.size());
        LOG.info("" + badUrls);
        assertTrue(badUrls.contains(serverUrl + "a.png"));
        assertFalse(badUrls.contains(serverUrl + "invalid"));
        Set<String> referralUrls = verifier.getPagesContainingUrl(serverUrl + "a.png");
        assertEquals(1, referralUrls.size());
        assertTrue(referralUrls.contains(serverUrl));
    }
    
    @Test
    public void testBadInitialUrl() {
        WebsiteVerifier verifier = new WebsiteVerifier();
        assertFalse(verifier.verify(serverUrl + "//lakjsdasf8usadfn/a/s/df"));
        assertFalse(verifier.isOk());
        // check resultToString for this special case:
        verifier.resultToString();
    }

    public URL getURL() throws MalformedURLException, UnknownHostException {
        ServerConnector connector = (ServerConnector) server.getConnectors()[0];
        InetAddress addr = InetAddress.getLocalHost();
        return new URL(
            String.format(Locale.ROOT, "%s://%s:%d/", "http", addr.getHostAddress(), connector.getLocalPort()));
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        LOG.info(String.format(Locale.ROOT, "handle(%s, ...)", target));

        if ("/".equals(target)) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html");
            response.getWriter().print("<!DOCTYPE html><html><body>"
                + "<a href=\"http://ahsdlkfhasdf/asdfasjdh/asdlsd\"></a>"
                + "<a href=\"/valid\"></a>"
                + "<a href=\"/invalid\"></a>"
                + "<a href=\"/bad.zip\"></a>"
                + "<a href=\"/good.zip\"></a>"
                + "<img src=\"a.png\">"
                + "</body></html>");
        } else if ("/valid".equals(target)) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html");
            response.getWriter().print("<!DOCTYPE html><html><body>"
                + "<a href=\"http://ahsdlkfhasdf/asdfasjdh/asdlsd2\"></a>"
                + "<a href=\"/\"></a>"
                + "<a href=\"/invalid\"></a>"
                + "<img src=\"b.png\">"
                + "</body></html>");
        } else if ("/b.png".equals(target)) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("image/png");
        } else if ("/good.zip".equals(target)) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/zip");
            response.getOutputStream().write(createZip());
        } else if ("/bad.zip".equals(target)) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/zip");
            response.getWriter().print("garbage");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("text/html");
        }

        baseRequest.setHandled(true);
    }
    
    private byte[] createZip() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry ze = new ZipEntry("name");
            zos.putNextEntry(ze);
            zos.write("teststring".getBytes());
            zos.closeEntry();
            zos.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
