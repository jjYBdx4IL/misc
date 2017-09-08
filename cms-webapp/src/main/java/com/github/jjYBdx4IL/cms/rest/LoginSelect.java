package com.github.jjYBdx4IL.cms.rest;

import static j2html.TagCreator.a;
import static j2html.TagCreator.div;
import static j2html.TagCreator.h3;
import static j2html.TagCreator.img;

import com.github.jjYBdx4IL.cms.rest.app.HtmlBuilder;
import com.github.jjYBdx4IL.cms.rest.app.SessionData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import j2html.tags.ContainerTag;

@Path("loginSelect")
@PermitAll
public class LoginSelect {

    private static final Logger LOG = LoggerFactory.getLogger(LoginSelect.class);

    private static final String SIGNIN_IMG_LOC = "assets/google_signin_buttons/web/1x/btn_google_signin_light_normal_web.png";

    @Context
    UriInfo uriInfo;
    @Inject
    private SessionData session;
    @Inject
    private HtmlBuilder htmlBuilder;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String get() {
        LOG.trace("get()");

        session.logout();

        ContainerTag googleSignInButton = a(img().withSrc(SIGNIN_IMG_LOC))
            .withHref(uriInfo.getBaseUriBuilder().path(GoogleLogin.class).build().toString());

        htmlBuilder.mainAdd(
            div(
                div(h3("Choose a Login Service:").withClass("col-12 center")).withClass("row"),
                div(googleSignInButton.withClass("col-12 center")).withClass("row")
            ).withClass("container")
        );

        return htmlBuilder.toString();
    }

}
