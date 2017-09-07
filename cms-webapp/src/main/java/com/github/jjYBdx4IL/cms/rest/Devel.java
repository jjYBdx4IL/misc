package com.github.jjYBdx4IL.cms.rest;

import com.github.jjYBdx4IL.cms.EmbeddedMain;
import com.github.jjYBdx4IL.cms.jpa.dto.QueryFactory;
import com.github.jjYBdx4IL.cms.jpa.dto.User;
import com.github.jjYBdx4IL.cms.jpa.tx.TxRo;
import com.github.jjYBdx4IL.cms.rest.app.SessionData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("devel")
public class Devel {

    private static final Logger LOG = LoggerFactory.getLogger(Devel.class);

    @Context
    UriInfo uriInfo;
    @Inject
    public EntityManager em;
    @Inject
    private SessionData session;

    public Devel() {
        if (!EmbeddedMain.isDevel) {
            throw new IllegalStateException();
        }
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @TxRo
    @Path("login")
    public Response login() {
        LOG.trace("login()");

        String testUid = "1";
        
        List<User> users = QueryFactory.getUserByGoogleUid(em, testUid).getResultList();
        User user = null;
        if (!users.isEmpty()) {
            user = users.get(0);
        } else {
            user = new User();
            user.setGoogleUniqueId(testUid);
            user.setCreatedAt(new Date());
        }

        user.setLastLoginAt(new Date());
        user.setLoginCount(user.getLoginCount() + 1);
        user.setEmail("test@tester.com");
        em.persist(user);

        session.setUser(user);
        
        return Response.ok().build();
    }

}
