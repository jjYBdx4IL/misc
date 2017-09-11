package com.github.jjYBdx4IL.javaee.jpa;

import static j2html.TagCreator.body;
import static j2html.TagCreator.document;
import static j2html.TagCreator.form;
import static j2html.TagCreator.html;
import static j2html.TagCreator.input;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
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

@Path("")
public class Jpa {

    private static final Logger LOG = LoggerFactory.getLogger(Jpa.class);

    @Context
    UriInfo uriInfo;
//    @PersistenceContext(unitName = "db-default")
//    private EntityManager em;
    @Context
    HttpServletRequest req;

    @Produces(MediaType.TEXT_HTML)
    @GET
    public Response get() {
        LOG.info(req.getCharacterEncoding());
        return Response.ok(document(html(body(
            form().withAction("/db/jpa/").withMethod("post").attr("enctype", "multipart/form-data")
                .attr("accept-encoding", "utf-8").with(
                    input().withType("text").withName("text2"),
                    input().withType("submit").withName("submitButton").withValue("update")
                ),
            form().withAction("/db/jpa/").withMethod("post").attr("enctype", MediaType.APPLICATION_FORM_URLENCODED+";charset=utf-8")
                .attr("accept-charset", "iso-8859-1")
                .attr("accept-charset", "utf-8")
                .with(
                    input().withType("text").withName("text2"),
                    input().withType("submit").withName("submitButton").withValue("update")
                )

        )
        )).toString()).build();
    }

    @POST
    @Consumes("multipart/form-data")
    public Response post(@FormParam("submitButton") String button,
        @FormParam("text2") String text) {
        LOG.info("post(): text: " + text);
        return Response.ok("").build();
    }
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response post2(@FormParam("submitButton") String button,
        @FormParam("text2") String text) {
        LOG.info("post2(): text: " + text);
        return Response.ok("").build();
    }
}
