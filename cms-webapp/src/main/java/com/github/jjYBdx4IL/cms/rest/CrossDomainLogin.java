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
package com.github.jjYBdx4IL.cms.rest;

import com.github.jjYBdx4IL.cms.rest.app.SessionData;
import com.github.jjYBdx4IL.utils.text.PasswordGenerator;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

//CHECKSTYLE:OFF
@Path("crossDomainLogin")
@PermitAll
public class CrossDomainLogin {

    private static final Logger LOG = LoggerFactory.getLogger(CrossDomainLogin.class);

    @Context
    UriInfo uriInfo;
    @Inject
    private SessionData session;
    @Context
    HttpServletRequest req;
    @Inject
    @Named("subdomain")
    String subdomain;

    private static final Cache<String, String> uidXferCache = CacheBuilder.newBuilder()
        .expireAfterWrite(3, TimeUnit.MINUTES).build();

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response get(@QueryParam("callbackUrl") String callbackUrl, @QueryParam("state") String state)
        throws URISyntaxException {

        if (callbackUrl != null) {
            if (!subdomain.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("not main domain").build();
            }
            if (state == null || state.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("bad state param").build();
            }
            if (!session.isAuthenticated()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("not authenticated").build();
            }
            uidXferCache.put(state, session.getUid());
            return Response.temporaryRedirect(new URI(callbackUrl)).status(HttpServletResponse.SC_FOUND).build();
        }

        if (subdomain.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("not sub domain").build();
        }

        String stateSecret = PasswordGenerator.generate55(20);
        session.setGoogleOauth2StateSecret(stateSecret);
        URI callback = uriInfo.getBaseUriBuilder().path(CrossDomainLogin.class).path(CrossDomainLogin.class, "callback")
            .build();
        UriBuilder redir = uriInfo.getBaseUriBuilder()
            .path(CrossDomainLogin.class)
            .queryParam("callbackUrl", callback.toASCIIString())
            .queryParam("state", stateSecret);
        redir.host(uriInfo.getBaseUri().getHost().substring(subdomain.length() + 1));
        return Response.temporaryRedirect(redir.build()).status(HttpServletResponse.SC_FOUND).build();
    }

    @GET
    @Path("callback")
    @Produces(MediaType.TEXT_PLAIN)
    public Response callback() {
        session.setUid(uidXferCache.getIfPresent(session.getGoogleOauth2StateSecret()));
        uidXferCache.invalidate(session.getGoogleOauth2StateSecret());
        session.setGoogleOauth2StateSecret(null);
        req.getSession().setMaxInactiveInterval(86400);
        LOG.info("logged in as " + session.getUid() + " on subdomain " + subdomain);
        return Response.ok("login OK").build();
    }

}
