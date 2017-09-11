package com.github.jjYBdx4IL.javaee.jpa;

import static j2html.TagCreator.body;
import static j2html.TagCreator.document;
import static j2html.TagCreator.form;
import static j2html.TagCreator.html;
import static j2html.TagCreator.input;

import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
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
    @PersistenceContext(unitName = "db-default")
    private EntityManager em;
    
    @Produces(MediaType.TEXT_HTML)
    @GET
    public Response get() {
        return Response.ok(document(html(body(
            form().withMethod("post").with(
//                input().withName("title").withPlaceholder("title").isRequired()
//                    .withValue(article != null ? article.getTitle() : ""),
//                br(),
//                input().withName("pathId").withPlaceholder("path id").isRequired()
//                    .withValue(article != null ? article.getPathId() : ""),
//                br(),
//                textarea().withName("content").isRequired()
//                    .withText(article != null ? article.getContent() : ""),
//                br(),
//                input().withName("tags").withId("tags").withPlaceholder("Tags")
//                    .withValue(createTagsString(article)),
//                br(),
                input().withType("submit").withName("submitButton").withValue("update"),
                input().withType("submit").withName("submitButton").withValue("create"),
                input().withType("submit").withName("submitButton").withValue("create-drop"),
                input().withType("submit").withName("submitButton").withValue("dump model")
            )
            
        )
        )).toString()).build();
    }
    
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @POST
    @Transactional
    public Response post(@FormParam("submitButton") String button) {
        LOG.info(""+em);

        SchemaExport.main(new String[] {"--help"});
        
        return Response.ok("").build();
    }
}
