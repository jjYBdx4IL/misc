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
import static j2html.TagCreator.link;

import com.github.jjYBdx4IL.cms.jpa.AppCache;
import com.github.jjYBdx4IL.cms.jpa.QueryFactory;
import com.github.jjYBdx4IL.cms.jpa.dto.Article;
import com.github.jjYBdx4IL.cms.jpa.dto.ConfigKey;
import com.github.jjYBdx4IL.cms.rest.app.HtmlBuilder;
import j2html.tags.EmptyTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
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
        LOG.trace("get()");
        
        htmlBuilder.setJsValue("articleDisplayContinuationEndpoint",
            uriInfo.getBaseUriBuilder().path(Home.class).path(Home.class, "cont").toTemplate());
        htmlBuilder.enableShareButtons();

        List<Article> articles = qf.getArticleDisplayList(null, null, true)
            .setMaxResults(MAX_ARTICLES_PER_REQUEST).getResultList();

        htmlBuilder.mainAdd(
            div(
                htmlBuilder.createArticleListRow(articles, false, false)
            ).withClass("container")
        );
        return Response.ok(htmlBuilder.toString()).build();
    }

    @Path("continue/{skip}")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response cont(@PathParam("skip") int skip) {
        LOG.trace("cont()");

        List<Article> articles = qf.getArticleDisplayList(null, null, true)
            .setMaxResults(MAX_ARTICLES_PER_REQUEST).setFirstResult(skip).getResultList();

        return Response.ok(htmlBuilder.createArticleListRowInner(articles, false, false).toString()).build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("byTag/{tag}")
    public Response byTag(@PathParam("tag") String selectedTag) {
        LOG.trace("byTag()");

        String tagRssFeedUrl = uriInfo.getBaseUriBuilder().path(RssFeed.class).path(RssFeed.class, "feedByTag")
            .build(selectedTag).toString();
        EmptyTag tagRssFeedLink = link().withRel("alternate").withType("application/rss+xml")
            .withTitle(appCache.get(ConfigKey.WEBSITE_TITLE) + " - " + selectedTag)
            .withHref(tagRssFeedUrl);
        
        htmlBuilder.setPageTitle("Tag: " + selectedTag);
        htmlBuilder.addPageTitleSubItem("room_service", "Rss feed for this tag", tagRssFeedUrl);
        htmlBuilder.addHeadContent(tagRssFeedLink);
        htmlBuilder.setJsValue("articleDisplayContinuationEndpoint",
            uriInfo.getBaseUriBuilder().path(Home.class).path(Home.class, "byTagCont")
                .resolveTemplate("tag", selectedTag).toTemplate());
        htmlBuilder.enableShareButtons();
        
        if ("impressum".equalsIgnoreCase(selectedTag)) {
            htmlBuilder.enableNoIndex();
        }

        List<Article> articles = qf.getArticleDisplayList(selectedTag, null, true)
            .setMaxResults(MAX_ARTICLES_PER_REQUEST).getResultList();

        htmlBuilder.mainAdd(
            div(
                htmlBuilder.createArticleListRow(articles, false, false)
            ).withClass("container")
        );
        return Response.ok(htmlBuilder.toString()).build();
    }

    @Path("continueByTag/{tag}/{skip}")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response byTagCont(@PathParam("tag") String selectedTag, @PathParam("skip") int skip) {
        LOG.trace("byTagCont()");

        List<Article> articles = qf.getArticleDisplayList(selectedTag, null, true)
            .setMaxResults(MAX_ARTICLES_PER_REQUEST).setFirstResult(skip).getResultList();

        return Response.ok(htmlBuilder.createArticleListRowInner(articles, false, false).toString()).build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("{year: \\d{4,4}}/{month: \\d{2,2}}/{day: \\d{2,2}}/{pathId}")
    public Response byPathId(@PathParam("pathId") String pathId) {
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

        htmlBuilder.setHeadTitlePrefix(article.getTitle());
        htmlBuilder.enableShareButtons();
        htmlBuilder.mainAdd(
            div(
                htmlBuilder.createArticleListRow(articles, true, true)
            ).withClass("container")
        );
        return Response.ok(htmlBuilder.toString()).build();
    }

}
