package com.github.jjYBdx4IL.cms.rest;

import static j2html.TagCreator.div;

import com.github.jjYBdx4IL.cms.jpa.QueryFactory;
import com.github.jjYBdx4IL.cms.jpa.dto.Article;
import com.github.jjYBdx4IL.cms.rest.app.HtmlBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("")
@PermitAll
public class Home {

    private static final Logger LOG = LoggerFactory.getLogger(Home.class);

    @Context
    UriInfo uriInfo;
    @PersistenceContext
    EntityManager em;
    @Inject
    private HtmlBuilder htmlBuilder;

    @GET
    @Produces(MediaType.TEXT_HTML)
//    @TxRo
    public Response get() {
        LOG.trace("get()");

        return byTag(null);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
//    @TxRo
    @Path("byTag/{tag}")
    public Response byTag(@PathParam("tag") String selectedTag) {
        LOG.trace("byTag()");

        List<Article> articles = QueryFactory.getArticleDisplayList(em, selectedTag, null).getResultList();
LOG.info(""+htmlBuilder);
        htmlBuilder.mainAdd(
            div(
                htmlBuilder.createArticleListRow(articles)
            ).withClass("container")
        );
        return Response.ok(htmlBuilder.toString()).build();
    }

}
