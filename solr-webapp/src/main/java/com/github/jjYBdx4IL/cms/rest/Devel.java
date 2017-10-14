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

import com.github.jjYBdx4IL.cms.jpa.AppCache;
import com.github.jjYBdx4IL.cms.jpa.QueryFactory;
import com.github.jjYBdx4IL.cms.jpa.dto.ConfigKey;
import com.github.jjYBdx4IL.cms.jpa.dto.ConfigValue;
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

//CHECKSTYLE:OFF
@Path("devel")
@DenyAll
public class Devel {

    public static final String TEST_UID = "devel-1";

    private static final Logger LOG = LoggerFactory.getLogger(Devel.class);

    @Context
    UriInfo uriInfo;
    @PersistenceContext
    EntityManager em;
    @Inject
    SessionData session;
    @Inject
    QueryFactory qf;
    @Inject
    private AppCache appCache;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Transactional
    @Path("login")
    public Response login() {
        LOG.warn("login()");

        User user = qf.getUserByUid(TEST_UID);

        if (user == null) {
            user = new User();
            user.setUid(TEST_UID);
            user.setCreatedAt(new Date());
        }

        user.setLastLoginAt(new Date());
        user.setLoginCount(user.getLoginCount() + 1);
        user.setEmail("test@tester.com");
        em.persist(user);

        session.setUid(TEST_UID);

        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Transactional
    @Path("clean")
    public Response clean() {
        LOG.warn("clean()");

        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Transactional
    @Path("prepareDb4It")
    public Response prepareDb4It() {
        LOG.warn("prepareDb4It()");

        if (appCache.get(ConfigKey.GOOGLE_OAUTH2_CLIENT_ID).isEmpty()) {
            ConfigValue v = new ConfigValue(ConfigKey.GOOGLE_OAUTH2_CLIENT_ID, "dummy");
            em.persist(v);
            v = new ConfigValue(ConfigKey.GOOGLE_OAUTH2_CLIENT_SECRET, "dummy");
            em.persist(v);
            appCache.load();
        }

        return Response.ok().build();
    }
}
