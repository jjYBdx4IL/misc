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
package com.github.jjYBdx4IL.utils.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.github.jjYBdx4IL.utils.cache.SimpleDiskCacheEntry.UpdateMode;
import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.junit4.SysPropsRestorerRule;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jjYBdx4IL
 */
public class SimpleDiskCacheEntryTest extends AbstractHandler {

	private static final Logger LOG = LoggerFactory.getLogger(SimpleDiskCacheEntryTest.class);
    private static final File cacheDir = Maven.getTempTestDir(SimpleDiskCacheEntryTest.class);

    private Server server = null;
    private final Map<String, String> serverPages = new HashMap<>();
    private final Map<String, Integer> serverPageStatus = new HashMap<>();
    
    @Before
    public void before() throws Exception {
    	server = new Server(0);
    	server.setHandler(this);
    	server.start();
        FileUtils.deleteDirectory(cacheDir);
    }
    
    @After
    public void after() throws Exception {
        server.stop();
        if (cacheDir.exists()) {
            cacheDir.delete();
        }
    }
    
    @ClassRule
    public static TestRule restoreSysPropsRule = new SysPropsRestorerRule();

    @BeforeClass
    public static void beforeClass() {
        System.setProperty(SimpleDiskCacheEntry.PROPNAME_CACHE_DIR, cacheDir.getAbsolutePath());
    }

    @Test
    public void testServerError() throws Exception {
        URL url = add("/testServerError", "A", HttpServletResponse.SC_NOT_FOUND);
        try {
            retrieve(url, UpdateMode.NEVER);
            fail();
        } catch (IOException ex) {
        }

        url = add("/testServerError", "B");
        assertEquals("B", retrieve(url, UpdateMode.NEVER));
    }

    @Test
    public void testErrorRecovery() throws Exception {
        URL url = add("/testErrorRecovery", "A");
        assertEquals("A", retrieve(url, UpdateMode.NEVER));

        corruptCacheFiles();

        url = add("/testErrorRecovery", "B");
        assertEquals("B", retrieve(url, UpdateMode.NEVER));
    }

    @Test
    public void testErrorRecoveryWrongURL() throws Exception {
        URL url = add("/testErrorRecovery", "A");
        assertEquals("A", retrieve(url, UpdateMode.NEVER));

        corruptCacheFilesWrongURL();

        url = add("/testErrorRecovery", "B");
        assertEquals("B", retrieve(url, UpdateMode.NEVER));
    }

    @Test
    public void testErrorRecoveryWrongClass() throws Exception {
        URL url = add("/testErrorRecovery", "A");
        assertEquals("A", retrieve(url, UpdateMode.NEVER));

        corruptCacheFilesWrongClass();

        url = add("/testErrorRecovery", "B");
        assertEquals("B", retrieve(url, UpdateMode.NEVER));
    }

    @Test
    public void testUpdateModeNever() throws Exception {
        URL url = add("/test", "A");
        assertEquals("A", retrieve(url, UpdateMode.NEVER));

        url = add("/test", "B");
        assertEquals("A", retrieve(url, UpdateMode.NEVER));
    }

    @Test
    public void testUpdateModeDailyURLChanged() throws Exception {
        URL url = add("/test", "A");
        assertEquals("A", retrieveSameFile(url, UpdateMode.DAILY));

        // cache file gets removed when URL changes
        url = add("/test2", "C");
        assertEquals("C", retrieveSameFile(url, UpdateMode.DAILY));

        url = add("/test2", "E");
        assertEquals("C", retrieveSameFile(url, UpdateMode.DAILY));
    }

    @Test
    public void testUpdateModeNeverURLChanged() throws Exception {
        URL url = add("/test", "A");
        assertEquals("A", retrieveSameFile(url, UpdateMode.NEVER));

        // cache file gets removed when URL changes
        url = add("/test2", "C");
        assertEquals("C", retrieveSameFile(url, UpdateMode.NEVER));

        url = add("/test2", "E");
        assertEquals("C", retrieveSameFile(url, UpdateMode.NEVER));
    }

    @Test
    public void testUpdateModeAlways() throws Exception {
        URL url = add("/test", "A");
        assertEquals("A", retrieve(url, UpdateMode.ALWAYS));

        url = add("/test", "B");
        assertEquals("B", retrieve(url, UpdateMode.ALWAYS));
    }

    @Test
    public void testUpdateModeAlwaysWithFallback() throws Exception {
        URL url = add("/test", "A");
        assertEquals("A", retrieve(url, UpdateMode.ALWAYS));

        // no error -> no fallback required
        url = add("/test", "B");
        assertEquals("B", retrieveFallback(url, UpdateMode.ALWAYS));

        // error -> fallback required
        url = add("/test", "C", HttpServletResponse.SC_NOT_FOUND);
        assertEquals("B", retrieveFallback(url, UpdateMode.ALWAYS));
    }

    private void corruptCacheFiles() throws FileNotFoundException, IOException {
        byte[] ba = new byte[1024];
        for (File f : cacheDir.listFiles()) {
            try (OutputStream os = new FileOutputStream(f)) {
                IOUtils.write(ba, os);
            }
        }
    }

    private void corruptCacheFilesWrongClass() throws FileNotFoundException, IOException {
        byte[] ba;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(new Integer(123));
            }
            ba = baos.toByteArray();
        }

        for (File f : cacheDir.listFiles()) {
            try (OutputStream os = new FileOutputStream(f)) {
                IOUtils.write(ba, os);
            }
        }
    }

    private void corruptCacheFilesWrongURL() throws FileNotFoundException, IOException {
        byte[] ba;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(new SimpleDiskCacheEntryHeader(new URL("http:////random/things")));
            }
            ba = baos.toByteArray();
        }

        for (File f : cacheDir.listFiles()) {
            try (OutputStream os = new FileOutputStream(f)) {
                IOUtils.write(ba, os);
                IOUtils.write("lkjlkajsd".getBytes(), os);
            }
        }
    }

    private URL add(String relPath, String content) throws MalformedURLException, UnknownHostException {
    	return add(relPath, content, HttpServletResponse.SC_OK);
    }

    private URL add(String relPath, String content, int sc) throws MalformedURLException, UnknownHostException {
    	serverPages.put(relPath,  content);
    	serverPageStatus.put(relPath, sc);
    	return getUrl(relPath);
    }

    private String retrieveSameFile(URL url, UpdateMode updateMode) throws MalformedURLException, IOException {
        try (InputStream is = new SimpleDiskCacheEntry(url, new File(cacheDir, "test"), updateMode)
                .getInputStream(false)) {
            return IOUtils.toString(is, "UTF-8");
        }
    }

    private String retrieve(URL url, UpdateMode updateMode) throws MalformedURLException, IOException {
        try (InputStream is = new SimpleDiskCacheEntry(url, updateMode).getInputStream(false)) {
            return IOUtils.toString(is, "UTF-8");
        }
    }

    private String retrieveFallback(URL url, UpdateMode updateMode) throws MalformedURLException, IOException {
        try (InputStream is = new SimpleDiskCacheEntry(url, updateMode).getInputStream(true)) {
            return IOUtils.toString(is, "UTF-8");
        }
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

        if (!serverPages.containsKey(target)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("text/plain");
        } else {
	        response.setStatus(serverPageStatus.get(target));
	        response.setContentType("text/plain");
	        response.getWriter().print(serverPages.get(target));
        }

        baseRequest.setHandled(true);
    }
}
