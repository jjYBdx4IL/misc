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

import java.net.MalformedURLException;

import javax.annotation.security.PermitAll;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;

//CHECKSTYLE:OFF
@Path("robots.txt")
@PermitAll
@Transactional
public class RobotsTxt {

    @Context
    UriInfo uriInfo;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response robotsTxt() throws MalformedURLException, IllegalArgumentException, UriBuilderException {
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("SITEMAP: ");
        sb.append(uriInfo.getBaseUriBuilder().path(SiteMap.class).build().toString());
        sb.append("\n");
        
        return Response.ok().entity(sb.toString()).build();
    }

}
