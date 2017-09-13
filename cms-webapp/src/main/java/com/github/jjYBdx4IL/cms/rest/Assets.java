/*
 * Copyright © 2017 jjYBdx4IL (https://github.com/jjYBdx4IL)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//CHECKSTYLE:OFF
@Path("assets")
@PermitAll
public class Assets {

    private static final Logger LOG = LoggerFactory.getLogger(Assets.class);

    @Context
    ServletContext ctx;

    @GET
    @Path("{filename: .*}")
    public Response get(@PathParam("filename") String filename) {
        LOG.trace("get()");

        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

        File file = new File(ctx.getRealPath("/assets/" + filename));
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            LOG.warn("asset file not found: " + file.getAbsolutePath());
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        String fileName = file.getName();
        String mimeType = mimeTypesMap.getContentType(fileName);
        if (fileName.contains(".")) {
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            if ("js".equals(suffix)) {
                mimeType = "application/javascript;charset=utf-8";
            }
            else if ("css".equals(suffix)) {
                mimeType = "text/css;charset=utf-8";
            }
        }
        LOG.info(mimeType + " " + file);
        return Response.ok(file, mimeType).lastModified(new Date(file.lastModified())).build();
    }

}
