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

import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//CHECKSTYLE:OFF
/**
 * 
 * @author jjYBdx4IL
 *
 */
public class AdHocHttpServer implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(AdHocHttpServer.class);
    private static final String HOSTNAME = "127.0.0.1";
    private Server server = new Server(new InetSocketAddress(HOSTNAME, 0));
    private final StaticContentHandler handler;

    public AdHocHttpServer() throws Exception {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param rootDir resolve requests relative to this directory, optional
     * @throws Exception on error
     */
    public AdHocHttpServer(File rootDir) throws Exception {
        handler = new StaticContentHandler(rootDir);
        server.setHandler(handler);
        server.setStopAtShutdown(false);
        server.setStopTimeout(1000L);
        server.start();
        log.info(String.format("%s started at %s (root dir = %s)",
                getClass().getSimpleName(),
                computeServerURL("/"),
                rootDir));
    }

    public URL computeServerURL(String target) throws MalformedURLException {
        return new URL("http", "127.0.0.1", getPort(), target);
    }

    public URL addStaticContent(String target, StaticResponse responseDef) throws MalformedURLException {
        getHandler().addStaticContent(target, responseDef);
        return computeServerURL(target);
    }

    public int getPort() {
        return ((ServerConnector) server.getConnectors()[0]).getLocalPort();
    }

    public String getHostname() {
        return HOSTNAME;
    }

    @Override
    public void close() throws Exception {
        server.stop();
    }

    /**
     * @return the handler
     */
    public StaticContentHandler getHandler() {
        return handler;
    }

    public static class StaticResponse {

        public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";
        private final String contentType;
        private final String content;
        private final int responseStatus;

        public StaticResponse(String contentType, String content) {
            this(contentType, content, HttpServletResponse.SC_OK);
        }

        public StaticResponse(String content) {
            this(DEFAULT_CONTENT_TYPE, content, HttpServletResponse.SC_OK);
        }

        /**
         *
         * @param contentType the content type
         * @param content the location in case of redirects
         * @param responseStatus the response status
         */
        public StaticResponse(String contentType, String content, int responseStatus) {
            this.content = content;
            this.contentType = contentType;
            this.responseStatus = responseStatus;
            if (responseStatus == HttpServletResponse.SC_MOVED_PERMANENTLY
                    || responseStatus == HttpServletResponse.SC_MOVED_TEMPORARILY) {
                if (this.content == null) {
                    throw new IllegalArgumentException("content must contain location value, "
                            + "and contentType must be null for redirects");
                }
            }
        }

        public StaticResponse(String content, int responseStatus) {
            this(DEFAULT_CONTENT_TYPE, content, responseStatus);
        }

        public String getContentType() {
            return contentType;
        }

        public String getContent() {
            return content;
        }

        public int getResponseStatus() {
            return responseStatus;
        }
    }

    public class StaticContentHandler extends AbstractHandler {

        private final Map<String, StaticResponse> responseByTarget;
        private final File rootDir;
        private final Tika tika;
        private StaticResponse defaultResponse
                = new StaticResponse("text/html;charset=utf-8", "<h1>File not found.</h1>", HttpServletResponse.SC_NOT_FOUND);

        public StaticContentHandler(File rootDir) {
            this.responseByTarget = new HashMap<>();
            this.rootDir = rootDir;
            if (rootDir != null) {
                tika = new Tika();
            } else {
                tika = null;
            }
        }

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
                throws IOException, ServletException {
            log.info(String.format("handle(%s, ...)", target));

            StaticResponse responseDef = responseByTarget.get(target);

            if (responseDef == null && rootDir != null) {
                responseDef = createResponseDefFromRootDir(target);
            }

            if (responseDef == null) {
                responseDef = defaultResponse;
            }

            response.setStatus(responseDef.getResponseStatus());

            if (responseDef.getResponseStatus() == HttpServletResponse.SC_MOVED_PERMANENTLY
                    || responseDef.getResponseStatus() == HttpServletResponse.SC_MOVED_TEMPORARILY) {
                response.addHeader(HttpHeader.LOCATION.name(), responseDef.getContent());
            } else {
                response.setContentType(responseDef.getContentType());
                if (responseDef.getContent() != null) {
                    response.getWriter().print(responseDef.getContent());
                }
            }

            baseRequest.setHandled(true);
        }

        private StaticResponse createResponseDefFromRootDir(String target) {
            File f = new File(rootDir, target);
            if (!f.exists() || !f.isFile()) {
                return null;
            }
            try {
                return new StaticResponse(tika.detect(f), IOUtils.toString(f.toURI(), "ISO-8859-1"));
            } catch (IOException ex) {
                log.warn("", ex);
            }
            return null;
        }

        public void addStaticContent(String target, StaticResponse responseDef) throws MalformedURLException {
            responseByTarget.put(target, responseDef);
        }

        public void setDefaultResponseCodeOK() {
            setDefaultResponseCode(HttpServletResponse.SC_OK);
        }

        public void setDefaultResponseCode(int code) {
            defaultResponse = new StaticResponse(
                    defaultResponse.getContentType(),
                    defaultResponse.getContent(),
                    code
            );
        }
    }
}
