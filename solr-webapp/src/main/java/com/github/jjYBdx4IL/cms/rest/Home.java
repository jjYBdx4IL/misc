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
import com.github.jjYBdx4IL.cms.solr.SolrConfig;
import com.github.jjYBdx4IL.cms.solr.WebPageBean;
import j2html.tags.ContainerTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

//CHECKSTYLE:OFF
@Path("")
@PermitAll
@Transactional
public class Home {

    private static final Logger LOG = LoggerFactory.getLogger(Home.class);

    @Context
    UriInfo uriInfo;
    @Inject
    HtmlBuilder htmlBuilder;
    @Inject
    QueryFactory qf;
    @Inject
    AppCache appCache;

    // https://www.finalist.nl/techblog/2015/06/improving-search-result-with-search-api-solr-better-search-excerpts/
    @Path("")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response get(@QueryParam("q") String searchTerm) throws Exception {
        if (searchTerm == null) {
            htmlBuilder.setPageTitle("Search");
        } else {
            htmlBuilder.setPageTitle("Search Results");
            htmlBuilder.enableNoIndex();
        }
        
        htmlBuilder.addPageTitleSubItem("add_to_queue", "Submit website", SubmitWebSite.class);
        
        ContainerTag container = div().withClass("container");

        container.with(
            form().withMethod("GET").attr("accept-charset", "utf-8").with(
                input().withName("q").withPlaceholder("Enter search term(s)").isRequired()
                    .withCondValue(searchTerm != null, searchTerm)
                    .attr("autofocus")
                    .withClass("col-12")
            ).withClass("row searchForm")
        );

        appendSearchResults(container, searchTerm, 0);

        htmlBuilder.mainAdd(container);

        return Response.ok(htmlBuilder.toString()).build();
    }

    @Path("continue/{skip}")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response cont(@PathParam("skip") int skip) {

        return Response.ok(htmlBuilder.toString()).build();
    }

    protected void appendSearchResults(ContainerTag container, String searchTerm, int pageIndex) throws Exception {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return;
        }

        List<WebPageBean> pages = SolrConfig.queryWebPages(searchTerm, pageIndex);
        pages.forEach(page -> container.with(
            div(
                div(

                ).withClass("col-12")
            ).withClass("row")
        ));
    }

}
