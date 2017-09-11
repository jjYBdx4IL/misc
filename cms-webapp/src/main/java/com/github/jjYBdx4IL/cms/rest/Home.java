package com.github.jjYBdx4IL.cms.rest;

import static j2html.TagCreator.div;

import com.github.jjYBdx4IL.cms.jpa.QueryFactory;
import com.github.jjYBdx4IL.cms.jpa.dto.Article;
import com.github.jjYBdx4IL.cms.rest.app.HtmlBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("")
@PermitAll
public class Home {

    private static final Logger LOG = LoggerFactory.getLogger(Home.class);

    @Inject
    private HtmlBuilder htmlBuilder;
    @Inject
    QueryFactory qf;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response get() throws SQLException {
        LOG.trace("get()");

        return byTag(null);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("byTag/{tag}")
    public Response byTag(@PathParam("tag") String selectedTag) throws SQLException {
        LOG.trace("byTag()");

        List<Article> articles = qf.getArticleDisplayList(selectedTag, null).getResultList();

        htmlBuilder.mainAdd(
            div(
                htmlBuilder.createArticleListRow(articles)
            ).withClass("container")
        );
        return Response.ok(htmlBuilder.toString()).build();
    }

}
