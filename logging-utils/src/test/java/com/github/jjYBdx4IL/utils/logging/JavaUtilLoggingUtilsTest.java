/*
 * Copyright © 2014 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.utils.logging;

import static org.junit.Assert.assertTrue;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//CHECKSTYLE:OFF
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class JavaUtilLoggingUtilsTest extends AbstractHandler {

    private Server server = null;
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private final String token = Long.toString(Math.abs(new Random().nextLong()));
    
    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog();
    
    // do NOT explicitly log the token!
    @Test
    public void testSetJavaNetURLConsoleLoggingLevel() throws Exception {
        server = new Server(0);
        server.setHandler(this);
        server.start();
        
        JavaUtilLoggingUtils.setJavaNetURLConsoleLoggingLevel(Level.FINEST);
        URL url = new URL("http", server.getURI().getHost(), server.getURI().getPort(), "/"+token);
        try (InputStream is = url.openStream()){
        }

        countDownLatch.await();
        
        assertTrue(systemErrRule.getLog().contains(token));
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        baseRequest.setHandled(true);
        countDownLatch.countDown();
    }    
    
    @After
    public void after() throws Exception {
        if (server != null) {
            server.stop();
            server = null;
        }
    }
}
