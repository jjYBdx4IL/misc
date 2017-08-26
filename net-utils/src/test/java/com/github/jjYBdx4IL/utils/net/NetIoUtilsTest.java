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

import static org.junit.Assert.assertEquals;

import com.github.jjYBdx4IL.utils.net.NetIoUtils;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
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

public class NetIoUtilsTest extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(NetIoUtilsTest.class);

    private URL getUrl(Server server) throws MalformedURLException, UnknownHostException {
        ServerConnector connector = (ServerConnector) server.getConnectors()[0];
        InetAddress addr = InetAddress.getLocalHost();
        return new URL(
            String.format(Locale.ROOT, "%s://%s:%d", "http", addr.getHostAddress(), connector.getLocalPort()));
    }

    @Test
    public void testToStringDoPost() throws Exception {
        Server server = new Server(0);
        server.setHandler(this);
        server.start();
        URL serverUrl = getUrl(server);
        LOG.info("server URL: " + serverUrl);
        String pageContents = NetIoUtils.toStringDoPost(serverUrl, "param1", "value1ö", "param2", "value2");
        LOG.info("test page contents: " + pageContents);
        assertEquals("value1ö;value2", pageContents);
        server.stop();
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        LOG.info(String.format(Locale.ROOT, "handle(%s, ...)", target));

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/plain;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(StringUtils.join(request.getParameterValues("param1"), ",")
            + ";"
            + StringUtils.join(request.getParameterValues("param2"), ","));

        baseRequest.setHandled(true);
    }
}
