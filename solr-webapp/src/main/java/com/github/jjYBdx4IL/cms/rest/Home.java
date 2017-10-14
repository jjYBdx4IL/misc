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
import com.github.jjYBdx4IL.cms.rest.app.HtmlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

//CHECKSTYLE:OFF
@Path("")
@PermitAll
@Transactional
public class Home {

    private static final Logger LOG = LoggerFactory.getLogger(Home.class);

    public static final int MAX_ARTICLES_PER_REQUEST = 10;

    @Context
    UriInfo uriInfo;
    @Inject
    HtmlBuilder htmlBuilder;
    @Inject
    QueryFactory qf;
    @Inject
    AppCache appCache;
    
    @Path("")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response get() {
        return Response.ok(htmlBuilder.toString()).build();
    }

    @Path("search")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response search() {
        htmlBuilder.setJsValue("searchResultContinuationEndpoint",
            uriInfo.getBaseUriBuilder().path(Home.class).path(Home.class, "cont").toTemplate());
        htmlBuilder.enableShareButtons();

//        List<Article> articles = qf.getArticleDisplayList(null, null, true)
//            .setMaxResults(MAX_ARTICLES_PER_REQUEST).getResultList();

//        htmlBuilder.mainAdd(
//            div(
//                htmlBuilder.createArticleListRow(articles, false, false)
//            ).withClass("container")
//        );
        return Response.ok(htmlBuilder.toString()).build();
    }
    
    @Path("continueSearch/{skip}")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response cont(@PathParam("skip") int skip) {
//        List<Article> articles = qf.getArticleDisplayList(null, null, true)
//            .setMaxResults(MAX_ARTICLES_PER_REQUEST).setFirstResult(skip).getResultList();

        return Response.ok(htmlBuilder.toString()).build();
    }

}
