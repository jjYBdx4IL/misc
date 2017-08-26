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
package com.github.jjYBdx4IL.utils.ci.jenkins;

import static org.junit.Assert.assertEquals;

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

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public class DependencyGraphTest extends AbstractHandler {

	private static final Logger LOG = LoggerFactory.getLogger(DependencyGraphTest.class);
	
	private Server server = null;

	@After
	public void after() throws Exception {
		if (server != null) {
			server.stop();
			server = null;
		}
	}

	@Before
	public void before() throws Exception {
		server = new Server(0);
		server.setHandler(this);
		server.start();
	}
	
    @Test
    public void testParseStream() throws Exception {
        DependencyGraph dg = new DependencyGraph(getUrl(""));
        List<String> queuedJobs = dg.queue(new String[]{"a", "b"}, null, false);
        assertEquals(10, queuedJobs.size());
    }

	public URL getUrl(String path) throws MalformedURLException, UnknownHostException {
		ServerConnector connector = (ServerConnector) server.getConnectors()[0];
		InetAddress addr = InetAddress.getLocalHost();
		return new URL(String.format(Locale.ROOT, "%s://%s:%d%s", "http", addr.getHostAddress(),
				connector.getLocalPort(), path));
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		LOG.info(String.format(Locale.ROOT, "handle(%s, ...)", target));

		if (DependencyGraph.URL_SUFFIX.equals(target)) {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
	        try (InputStream is = getClass().getResourceAsStream("jenkinsDep.json")) {
	        	IOUtils.copy(is, response.getWriter(), "UTF-8");
	        }
		} else {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("text/html");
		}
		baseRequest.setHandled(true);
	}
    
}
