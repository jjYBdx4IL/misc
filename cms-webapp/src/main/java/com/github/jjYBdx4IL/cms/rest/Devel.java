package com.github.jjYBdx4IL.cms.rest;

import com.github.jjYBdx4IL.cms.jpa.QueryFactory;
import com.github.jjYBdx4IL.cms.jpa.dto.User;
import com.github.jjYBdx4IL.cms.rest.app.SessionData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import javax.annotation.security.DenyAll;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("devel")
@DenyAll
public class Devel {

    private static final Logger LOG = LoggerFactory.getLogger(Devel.class);

    @Context
    UriInfo uriInfo;
    @PersistenceContext
    EntityManager em;
    @Inject
    SessionData session;
    @Inject
    QueryFactory qf;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Transactional
    @Path("login")
    public Response login() {
        LOG.trace("login()");

        String testUid = "devel-1";
        
        User user = qf.getUserByUid(testUid);
        
        if (user == null) {
            user = new User();
            user.setUid(testUid);
            user.setCreatedAt(new Date());
        }

        user.setLastLoginAt(new Date());
        user.setLoginCount(user.getLoginCount() + 1);
        user.setEmail("test@tester.com");
        em.persist(user);

        session.setUid(testUid);
        
        return Response.ok().build();
    }

}
