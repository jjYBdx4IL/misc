package com.github.jjYBdx4IL.cms.rest;

import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.h4;

import com.github.jjYBdx4IL.cms.jpa.dto.Article;
import com.github.jjYBdx4IL.cms.jpa.dto.QueryFactory;
import com.github.jjYBdx4IL.cms.jpa.tx.TxRo;
import com.github.jjYBdx4IL.cms.rest.app.HtmlBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
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
    public String get() {
        LOG.trace("get()");

        List<Article> articles = QueryFactory.getArticleDisplayList(em, null).getResultList();

        ContainerTag articleListRow = div(
            each(articles,
                article -> div(
                    h4(article.getTitle()).withClass("articleTitle"),
                    div(article.getContent()).withClass("articleContent")
                ).withClass("col-12 article")
            )
        ).withClass("row");

        htmlBuilder.mainAdd(
                div(articleListRow).withClass("container articleManager")
            );

        return htmlBuilder.toString();
    }

}