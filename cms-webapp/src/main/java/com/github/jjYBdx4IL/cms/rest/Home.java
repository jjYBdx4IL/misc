package com.github.jjYBdx4IL.cms.rest;

import static j2html.TagCreator.a;
import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.h4;
import static j2html.TagCreator.span;
import static j2html.TagCreator.text;

import com.github.jjYBdx4IL.cms.jpa.dto.Article;
import com.github.jjYBdx4IL.cms.jpa.dto.QueryFactory;
import com.github.jjYBdx4IL.cms.jpa.tx.TxRo;
import com.github.jjYBdx4IL.cms.rest.app.HtmlBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import j2html.tags.ContainerTag;

@Path("")
public class Home {

    private static final Logger LOG = LoggerFactory.getLogger(Home.class);

    @Context
    UriInfo uriInfo;
    @Inject
    public EntityManager em;
    @Inject
    private HtmlBuilder htmlBuilder;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @TxRo
    public Response get() {
        LOG.trace("get()");

        return byTag(null);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @TxRo
    @Path("byTag/{tag}")
    public Response byTag(@PathParam("tag") String selectedTag) {
        LOG.trace("byTag()");

        List<Article> articles = QueryFactory.getArticleDisplayList(em, selectedTag).getResultList();

        if (articles.isEmpty()) {
            return Response.status(HttpServletResponse.SC_NOT_FOUND).build();
        }
        
        UriBuilder uriBuilder = uriInfo.getBaseUriBuilder().path(Home.class, "byTag");
        
        ContainerTag articleListRow = div(
            each(articles,
                article -> div(
                    h4(article.getTitle()).withClass("articleTitle"),
                    div(article.getContent()).withClass("articleContent"),
                    span("Tags: ").withClass("tagLineHeader"),
                    each(article.getTags(),
                        tag -> a(tag.getName()).withHref(uriBuilder.build(tag.getName()).toString()).withClass("tag")
                    )
                ).withClass("col-12 article")
            )
        ).withClass("row");

        htmlBuilder.mainAdd(
            div(articleListRow).withClass("container articleManager")
        );

        return Response.ok(htmlBuilder.toString()).build();
   }
}