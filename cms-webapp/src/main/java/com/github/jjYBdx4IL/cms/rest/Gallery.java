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

import static j2html.TagCreator.div;

import com.github.jjYBdx4IL.cms.jpa.AppCache;
import com.github.jjYBdx4IL.cms.jpa.QueryFactory;
import com.github.jjYBdx4IL.cms.jpa.dto.MediaFile;
import com.github.jjYBdx4IL.cms.json.dto.MediaFileDTO;
import com.github.jjYBdx4IL.cms.rest.app.HtmlBuilder;
import com.github.jjYBdx4IL.cms.rest.app.Role;
import com.github.jjYBdx4IL.cms.rest.app.SessionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//CHECKSTYLE:OFF
@Path("gallery")
public class Gallery {

    private static final Logger LOG = LoggerFactory.getLogger(Gallery.class);
    
    public static final int PREVIEWS_PER_REQUEST = 12;

    @Inject
    SessionData session;
    @Inject
    HtmlBuilder htmlBuilder;
    @Inject
    private AppCache appCache;
    @Inject
    QueryFactory qf;
    @PersistenceContext
    EntityManager em;

    @RolesAllowed(Role.ADMIN)
    @GET
    @Path("")
    public Response get() {
        LOG.trace("get()");

        htmlBuilder.enableGallerySupport();
        htmlBuilder.setPageTitle("Gallery");
        htmlBuilder.enableNoIndex();
        htmlBuilder.mainAdd(div().withClass("container gallery"));

        return Response.ok(htmlBuilder.toString()).build();
    }

    @Transactional
    @RolesAllowed(Role.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("imageList")
    public Response imageList(@QueryParam("maxId") Long maxId) throws SQLException {
        LOG.info("imageList()" + maxId);

        List<MediaFile> results = qf.getImagesMeta(maxId).setMaxResults(PREVIEWS_PER_REQUEST).getResultList();
        List<MediaFileDTO> dtos = new ArrayList<>();
        for (MediaFile mf : results) {
            dtos.add(MediaFileDTO.createFrom(mf));
        }
        return Response.ok(dtos).build();
    }
}
