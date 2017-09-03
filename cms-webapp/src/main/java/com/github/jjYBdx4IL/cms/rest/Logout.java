package com.github.jjYBdx4IL.cms.rest;

import com.github.jjYBdx4IL.cms.rest.app.SessionData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("logout")
public class Logout {
    
    private static final Logger LOG = LoggerFactory.getLogger(Logout.class);
    
    @Context
    UriInfo uriInfo;
    @Inject
    private SessionData session;
    
    @GET
    public Response logout() {
        LOG.trace("logout()");

        session.logout();

        return Response.temporaryRedirect(uriInfo.getBaseUriBuilder().path(Home.class).build())
            .status(HttpServletResponse.SC_FOUND)
            .build();
    }

}
