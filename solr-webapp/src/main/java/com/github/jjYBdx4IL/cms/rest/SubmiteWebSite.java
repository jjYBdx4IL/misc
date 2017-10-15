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

import static j2html.TagCreator.div;
import static j2html.TagCreator.form;
import static j2html.TagCreator.input;

import com.github.jjYBdx4IL.cms.jpa.AppCache;
import com.github.jjYBdx4IL.cms.jpa.QueryFactory;
import com.github.jjYBdx4IL.cms.rest.app.HtmlBuilder;
import j2html.tags.ContainerTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

//CHECKSTYLE:OFF
@Path("submitWebSite")
@PermitAll
@Transactional
public class SubmiteWebSite {

    private static final Logger LOG = LoggerFactory.getLogger(SubmiteWebSite.class);

    @Context
    UriInfo uriInfo;
    @Inject
    HtmlBuilder htmlBuilder;
    @Inject
    QueryFactory qf;
    @Inject
    AppCache appCache;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response submit(@QueryParam("site") String site) throws Exception {
        htmlBuilder.setPageTitle("Submit Website");

        ContainerTag container = div().withClass("container");

        if (site != null) {
            
        }
        
        container.with(
            div(
                div("Indexing websites currently works via site-maps located via sitemap entries in robots.txt.")
                    .withClass("col-12")
            ).withClass("row")
        ).with(
            form().withMethod("GET").attr("accept-charset", "utf-8").with(
                input().withName("q")
                    .withPlaceholder("https://your.website.com (only domain, no pages/query parts, no port)")
                    .isRequired()
                    .attr("autofocus")
                    .withClass("col-12")
            ).withClass("row submitWebSiteForm")
        );

        htmlBuilder.mainAdd(container);

        return Response.ok(htmlBuilder.toString()).build();
    }
}
