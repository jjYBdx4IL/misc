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

import com.github.jjYBdx4IL.cms.jpa.QueryFactory;
import com.github.jjYBdx4IL.cms.jpa.dto.Article;
import com.github.jjYBdx4IL.cms.rest.app.HtmlBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//CHECKSTYLE:OFF
@Path("")
@PermitAll
@Transactional
public class Home {

    private static final Logger LOG = LoggerFactory.getLogger(Home.class);

    @Inject
    HtmlBuilder htmlBuilder;
    @Inject
    QueryFactory qf;

    @Path("")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response get() throws SQLException {
        LOG.trace("get()");

        List<Article> articles = qf.getArticleDisplayList(null, null).getResultList();

        htmlBuilder.mainAdd(
            div(
                htmlBuilder.createArticleListRow(articles, false, false)
            ).withClass("container")
        );
        return Response.ok(htmlBuilder.toString()).build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("byTag/{tag}")
    public Response byTag(@PathParam("tag") String selectedTag) throws SQLException {
        LOG.trace("byTag()");

        htmlBuilder.setPageTitle("Tag: " + selectedTag);
        
        if ("impressum".equalsIgnoreCase(selectedTag)) {
            htmlBuilder.enableNoIndex();
        }
        
        List<Article> articles = qf.getArticleDisplayList(selectedTag, null).getResultList();

        htmlBuilder.mainAdd(
            div(
                htmlBuilder.createArticleListRow(articles, false, false)
            ).withClass("container")
        );
        return Response.ok(htmlBuilder.toString()).build();
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("{year: \\d{4,4}}/{month: \\d{2,2}}/{day: \\d{2,2}}/{pathId}")
    public Response byPathId(@PathParam("pathId") String pathId) throws SQLException {
        LOG.trace("byPathId()");

        if (pathId == null || pathId.isEmpty()) {
            return Response.status(HttpServletResponse.SC_NOT_FOUND).build();
        }
        
        Article article = qf.getArticleByPathId(pathId);
        
        if (article == null) {
            return Response.status(HttpServletResponse.SC_NOT_FOUND).build();
        }
        
        List<Article> articles = new ArrayList<>();
        articles.add(article);
        
        htmlBuilder.mainAdd(
            div(
                htmlBuilder.createArticleListRow(articles, true, true)
            ).withClass("container")
        );
        return Response.ok(htmlBuilder.toString()).build();
    }

}
