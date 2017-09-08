package com.github.jjYBdx4IL.cms.rest;

import static j2html.TagCreator.body;
import static j2html.TagCreator.document;
import static j2html.TagCreator.h3;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.link;
import static j2html.TagCreator.meta;
import static j2html.TagCreator.title;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("error")
@PermitAll
public class Error {

    private static final Logger LOG = LoggerFactory.getLogger(Error.class);
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String errorPage() {
        LOG.trace("errorPage()");
        return document(
            html(
                head(
                    title("Error"),
                    meta().attr("http-equiv", "Content-Type").attr("content", "text/html;charset=UTF-8"),
                    link().withRel("stylesheet").withType("text/css").withHref("assets/style.css")),
                body(
                    h3("We encountered an internal error."))));
    }

}
