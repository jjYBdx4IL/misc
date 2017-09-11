package com.github.jjYBdx4IL.javaee.jpa;

import com.github.jjYBdx4IL.javaee.h2frontend.InetAddr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Context
    private HttpServletRequest request;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("filter " + requestContext + " " + request);
        }
        if (!InetAddr.isLocalhostAddress(request)) {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
            return;
        }            
    } 
}

