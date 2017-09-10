package com.github.jjYBdx4IL.cms.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.security.PermitAll;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("assets")
@PermitAll
public class Assets {

    private static final Logger LOG = LoggerFactory.getLogger(Assets.class);

    @Context
    UriInfo uriInfo;
    @Context
    ServletContext ctx;

    @GET
    @Path("{filename: .*}")
    public Response get(@PathParam("filename") String filename) {
        LOG.trace("get()");
        
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

        File file = new File(ctx.getRealPath("/assets/" + filename));
        String mimeType = mimeTypesMap.getContentType(file.getAbsolutePath());
//        String mimeType= URLConnection.guessContentTypeFromName(file.getAbsolutePath());
        LOG.info(mimeType + " " + file);
        return Response.ok(file).lastModified(new Date(file.lastModified())).build();
    }

}
