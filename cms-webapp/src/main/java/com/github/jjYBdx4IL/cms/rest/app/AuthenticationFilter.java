package com.github.jjYBdx4IL.cms.rest.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthenticationFilter implements javax.ws.rs.container.ContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Context
    private ResourceInfo resourceInfo;
    @Context
    private HttpServletRequest httpRequest;

    private static final Response ACCESS_FORBIDDEN = Response.status(Response.Status.FORBIDDEN).build();

    public AuthenticationFilter() {
        LOG.trace("init()");
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("filter " + requestContext + " " + httpRequest);
        }
        if (!new Permissions(httpRequest).isAllowed(resourceInfo.getResourceMethod())) {
            requestContext.abortWith(ACCESS_FORBIDDEN);
            return;
        }            
    }
}

