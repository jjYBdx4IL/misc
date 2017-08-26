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

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Base class for testing behavior regarding http status codes.
 *
 * @author jjYBdx4IL
 */
//CHECKSTYLE:OFF
@SuppressWarnings("restriction")
public class HttpStatusCodeTestBase {

    private static HttpServer server;
    private static ExecutorService executor;
    private static String serverUrl;
    protected static final String URI_200_OK = "/index.html";
    protected static final String URI_200_OK_IMG = "/image.png";
    protected static final String URI_404_NOT_FOUND = "/notexisting";
    protected static final String URI_500_ERROR = "/internalerror";
    protected static final String TESTRESPONSE = "some test response";
    protected static final String TESTRESPONSE_PART = "test res";
    protected static final String MIMETYPE_PNG = "image/png";

    protected static String getServerUrl() {
        return serverUrl;
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        Handler handler = new Handler();
        InetSocketAddress addr = new InetSocketAddress(0);
        server = HttpServer.create(addr, 0);
        serverUrl = "http://localhost:" + server.getAddress().getPort();
        server.createContext("/", handler);
        executor = Executors.newCachedThreadPool();
        server.setExecutor(executor);
        server.start();
    }

    private static class Handler implements HttpHandler {

        @Override
        public void handle(HttpExchange t)
                throws IOException {
            String uri = t.getRequestURI().toString();

            //Headers map = t.getRequestHeaders();
            Headers rmap = t.getResponseHeaders();

            // CHECKSTYLE IGNORE InnerAssignment FOR NEXT 1 LINE
            try (InputStream is = t.getRequestBody()) {
                IOUtils.toString(is);
            }

            int statusCode = HttpStatus.INTERNAL_SERVER_ERROR_500;
            if (uri.equals(URI_200_OK)) {
                statusCode = HttpStatus.OK_200;
            }
            if (uri.equals(URI_404_NOT_FOUND)) {
                statusCode = HttpStatus.NOT_FOUND_404;
            }
            if (uri.equals(URI_500_ERROR)) {
                statusCode = HttpStatus.INTERNAL_SERVER_ERROR_500;
            }
            if (uri.equals(URI_200_OK_IMG)) {
                statusCode = HttpStatus.OK_200;
                rmap.set("Content-Type", MIMETYPE_PNG);
            }

            t.sendResponseHeaders(statusCode, TESTRESPONSE.length());
            OutputStream os = t.getResponseBody();
            os.write(TESTRESPONSE.getBytes("ASCII"));
            t.close();
        }
    }

    @AfterClass
    public static void tearDownClass() {
        server.stop(2);
        executor.shutdown();
    }
}
