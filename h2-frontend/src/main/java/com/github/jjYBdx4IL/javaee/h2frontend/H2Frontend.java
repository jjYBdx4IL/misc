package com.github.jjYBdx4IL.javaee.h2frontend;

import org.h2.server.web.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@javax.servlet.annotation.WebServlet("/h2/*")
public class H2Frontend extends WebServlet {

    private static final Logger LOG = LoggerFactory.getLogger(H2Frontend.class);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!InetAddr.isLocalhostAddress(req)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("rejecting request from non-localhost remote address " + req.getRemoteAddr());
            }
            return;
        }
        super.service(req, resp);
    }
}
