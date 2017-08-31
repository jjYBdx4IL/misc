package com.github.jjYBdx4IL.cms.rest;

import static j2html.TagCreator.a;
import static j2html.TagCreator.attrs;
import static j2html.TagCreator.body;
import static j2html.TagCreator.button;
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
import static j2html.TagCreator.table;
import static j2html.TagCreator.tbody;
import static j2html.TagCreator.td;
import static j2html.TagCreator.title;
import static j2html.TagCreator.tr;

import com.github.jjYBdx4IL.cms.jpa.dto.KeyValuePair;
import com.github.jjYBdx4IL.cms.jpa.dto.QueryFactory;
import com.github.jjYBdx4IL.cms.jpa.tx.Tx;
import com.github.jjYBdx4IL.cms.jpa.tx.TxRo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import j2html.tags.Tag;

@Path("/")
public class Root {

    private static final Logger LOG = LoggerFactory.getLogger(Root.class);

    private static final String SIGNIN_IMG_LOC = "assets/google_signin_buttons/web/1x/btn_google_signin_light_normal_web.png";
    private static final String SESSION_USER_ID = "session.user.id";

    @Context
    UriInfo uriInfo;

    @Inject
    public EntityManager em;
    @Context
    public HttpServletRequest request;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @TxRo
    public String get() {
        LOG.trace("get()");

        List<KeyValuePair> pairs = QueryFactory.getAll(em).getResultList();

        return htmlDoc("Embedded Jetty + Jersey + JPA + J2HTML Demo",
            isAuthenticated()
                ? a("Logout").withHref("logout")
                : a(img().withSrc(SIGNIN_IMG_LOC)).withHref("login"),
            h3("Please enter key-value pair " + request),
            table(
                attrs("#table-example"),
                tbody(
                    each(pairs, kvp -> tr(
                        td(kvp.getKey()),
                        td(kvp.getValue()))))),
            form().withMethod("post").with(
                input().withName("key").withPlaceholder("key").isRequired(),
                input().withName("value").withPlaceholder("value").isRequired(),
                button("add").withType("submit")));
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Tx
    public Response post(@FormParam("key") String key, @FormParam("value") String value) {
        LOG.trace("post()");

        if (key == null || key.isEmpty()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("key required").build();
        }
        if (value == null || value.isEmpty()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("value required").build();
        }

        KeyValuePair kvp = new KeyValuePair();
        kvp.setKey(key);
        kvp.setValue(value);
        em.persist(kvp);

        return Response.temporaryRedirect(uriInfo.getAbsolutePathBuilder().build()).status(HttpServletResponse.SC_FOUND)
            .build();
    }

    @GET
    @Path("/login")
    public Response login() {
        LOG.trace("login()");
        HttpSession session = request.getSession();
        session.setAttribute(SESSION_USER_ID, "someId");
        return Response.temporaryRedirect(UriBuilder.fromResource(Root.class).path(Root.class).build()).status(HttpServletResponse.SC_FOUND)
            .build();
    }
    
    @GET
    @Path("/logout")
    public Response logout() {
        LOG.trace("logout()");
        HttpSession session = request.getSession();
        session.removeAttribute(SESSION_USER_ID);
        return Response.temporaryRedirect(UriBuilder.fromResource(Root.class).path(Root.class).build()).status(HttpServletResponse.SC_FOUND)
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

    public boolean isAuthenticated() {
        HttpSession session = request.getSession();
        return session.getAttribute(SESSION_USER_ID) != null;
    }
}