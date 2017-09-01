package com.github.jjYBdx4IL.cms.rest;

import static j2html.TagCreator.a;
import static j2html.TagCreator.body;
import static j2html.TagCreator.br;
import static j2html.TagCreator.button;
import static j2html.TagCreator.div;
import static j2html.TagCreator.document;
import static j2html.TagCreator.each;
import static j2html.TagCreator.form;
import static j2html.TagCreator.h3;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.img;
import static j2html.TagCreator.input;
import static j2html.TagCreator.link;
import static j2html.TagCreator.meta;
import static j2html.TagCreator.span;
import static j2html.TagCreator.textarea;
import static j2html.TagCreator.title;

import com.github.jjYBdx4IL.cms.jpa.dto.Article;
import com.github.jjYBdx4IL.cms.jpa.dto.QueryFactory;
import com.github.jjYBdx4IL.cms.jpa.tx.Tx;
import com.github.jjYBdx4IL.cms.jpa.tx.TxRo;
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
import j2html.tags.Tag;

@Path("")
public class Root {

    private static final Logger LOG = LoggerFactory.getLogger(Root.class);

    private static final String SIGNIN_IMG_LOC = "assets/google_signin_buttons/web/1x/btn_google_signin_light_normal_web.png";

    @Context
    UriInfo uriInfo;
    @Inject
    public EntityManager em;
    @Inject
    private SessionData session;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @TxRo
    public String get() {
        LOG.trace("get()");

        List<Article> articles = QueryFactory.getArticleDisplayList(em, null).getResultList();

        ContainerTag loginButton = div(session.isAuthenticated()
            ? div(span("logged in as: " + session.getUser().getEmail() + " ("),
                a("logout").withHref("logout"), span(")"))
            : a(img().withSrc(SIGNIN_IMG_LOC))
                .withHref(uriInfo.getBaseUriBuilder().path(GoogleLogin.class).build().toString()))
                    .withClass("loginButton");
        ContainerTag articleList = div(each(articles, article -> div(
            div(article.getTitle()).withClass("title"),
            div(article.getContent()).withClass("content")).withClass("article"))).withClass("articles");
        ContainerTag editForm = div(session.isAuthenticated()
            ? form().withMethod("post").with(
                input().withName("title").withPlaceholder("title").isRequired(),
                br(),
                textarea().withName("content").isRequired(),
                br(),
                input().withType("submit").withName("submitButton").withValue("save"))
            : null).withClass("editForm");
        return htmlDoc("Embedded Jetty + Jersey + JPA + J2HTML Demo",
            div(loginButton, articleList, editForm).withClass("cssgrid"));
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

    @GET
    @Path("/logout")
    public Response logout() {
        LOG.trace("logout()");

        session.logout();

        return Response.temporaryRedirect(uriInfo.getBaseUriBuilder().path(Root.class).build())
            .status(HttpServletResponse.SC_FOUND)
            .build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/error")
    public String errorPage() {
        LOG.trace("errorPage()");
        return htmlDoc("Error",
            h3("We encountered an internal error."));
    }

    public static String htmlDoc(String title, Tag<?>... bodyTags) {
        return document(
            html(
                head(
                    meta().attr("http-equiv", "Content-Type").attr("content", "text/html;charset=UTF-8"),
                    link().withRel("stylesheet").withType("text/css").withHref("assets/style.css"),
                    title(title)),
                body(
                    bodyTags)));
    }

}