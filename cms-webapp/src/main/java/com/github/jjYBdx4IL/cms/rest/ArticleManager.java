package com.github.jjYBdx4IL.cms.rest;

import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.h3;
import static j2html.TagCreator.i;
import static j2html.TagCreator.span;

import com.github.jjYBdx4IL.cms.jpa.dto.Article;
import com.github.jjYBdx4IL.cms.jpa.dto.QueryFactory;
import com.github.jjYBdx4IL.cms.jpa.tx.Tx;
import com.github.jjYBdx4IL.cms.jpa.tx.TxRo;
import com.github.jjYBdx4IL.cms.rest.app.HtmlBuilder;
import com.github.jjYBdx4IL.cms.rest.app.SessionData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import j2html.tags.ContainerTag;

@Path("articleManager")
public class ArticleManager {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleManager.class);

    @Context
    UriInfo uriInfo;
    @Inject
    public EntityManager em;
    @Inject
    private SessionData session;
    @Inject
    private HtmlBuilder htmlBuilder;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @TxRo
    public String get() {
        LOG.trace("get()");

        List<Article> articles = QueryFactory.getArticleDisplayList(em, null).getResultList();

        ContainerTag titleRow = div(
            h3("Article Manager").withClass("col-6 articleManagerTitle"),
            htmlBuilder.iconTextLink("col-6", "add_box", "Create new", ArticleManager.class, "create"))
                .withClass("row articleManagerTitleBar");
        ContainerTag articleListRow = div(
            each(articles, article -> div(
                div(article.getTitle()).withClass("articleTitle"))
                    .withClass("col-12 article")))
                        .withClass("row");

        htmlBuilder.mainAdd(div(titleRow, articleListRow).withClass("container articleManager"));

        return htmlBuilder.toString();
        //
        // List<Article> articles = QueryFactory.getArticleDisplayList(em,
        // null).getResultList();
        //
        // ContainerTag loginButton = div(session.isAuthenticated()
        // ? div(span("logged in as: " + session.getUser().getEmail() + " ("),
        // a("logout").withHref("logout"), span(")"))
        // : a(img().withSrc(""))
        // .withHref(uriInfo.getBaseUriBuilder().path(GoogleLogin.class).build().toString()))
        // .withClass("loginButton");
        // ContainerTag articleList = div(each(articles, article -> div(
        // div(article.getTitle()).withClass("title"),
        // div(article.getContent()).withClass("content")).withClass("article"))).withClass("articles");
        // ContainerTag editForm = div(session.isAuthenticated()
        // ? form().withMethod("post").with(
        // input().withName("title").withPlaceholder("title").isRequired(),
        // br(),
        // textarea().withName("content").isRequired(),
        // br(),
        // input().withType("submit").withName("submitButton").withValue("save"))
        // : null).withClass("editForm");
        // return HtmlUtils.htmlDoc("Embedded Jetty + Jersey + JPA + J2HTML
        // Demo",
        // div(loginButton, articleList, editForm).withClass("cssgrid"));
    }
    @GET
    @Produces(MediaType.TEXT_HTML)
    @TxRo
    @Path("create")
    public String create() {
        LOG.trace("create()");
        return "";
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Tx
    public Response post(@FormParam("title") String title, @FormParam("content") String content) {
        LOG.trace("post()");

        if (title == null || title.isEmpty()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("title required").build();
        }
        if (content == null || content.isEmpty()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("content required").build();
        }

        Article article = new Article();
        article.setTitle(title);
        article.setContent(content);
        article.setOwner(session.getUser());
        article.setCreatedAt(new Date());
        article.setLastModified(article.getCreatedAt());
        em.persist(article);

        return Response.temporaryRedirect(uriInfo.getAbsolutePathBuilder().build()).status(HttpServletResponse.SC_FOUND)
            .build();
    }

}
