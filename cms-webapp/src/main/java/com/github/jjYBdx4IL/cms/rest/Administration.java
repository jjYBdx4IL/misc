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

import static j2html.TagCreator.a;
import static j2html.TagCreator.div;
import static j2html.TagCreator.form;
import static j2html.TagCreator.hr;
import static j2html.TagCreator.input;
import static j2html.TagCreator.label;
import static j2html.TagCreator.li;
import static j2html.TagCreator.span;
import static j2html.TagCreator.text;
import static j2html.TagCreator.textarea;
import static j2html.TagCreator.ul;

import com.github.jjYBdx4IL.cms.jpa.QueryFactory;
import com.github.jjYBdx4IL.cms.rest.app.HtmlBuilder;
import j2html.tags.ContainerTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
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
@Path("admin")
@PermitAll
@Transactional
public class Administration {

    private static final Logger LOG = LoggerFactory.getLogger(Administration.class);

    @Context
    UriInfo uriInfo;
    @Inject
    HtmlBuilder htmlBuilder;
    @Inject
    QueryFactory qf;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response admin() throws MalformedURLException, IllegalArgumentException, UriBuilderException {
        LOG.trace("admin()");

        htmlBuilder.setPageTitle("Administration");
        htmlBuilder.enableJsAminSupport();

        String exportDumpLink = uriInfo.getBaseUriBuilder().path(ArticleManager.class)
            .path(ArticleManager.class, "exportDump").build().toString();
        String importDumpLink = uriInfo.getBaseUriBuilder().path(ArticleManager.class)
            .path(ArticleManager.class, "importDump").build().toString();

        ContainerTag row = div().withClass("row");
        row.with(hr().withClass("col-12"));
        row.with(div(
            a("export XML dump (does not include media files, images etc.)").withHref(exportDumpLink)
        ).withClass("col-12"));
        row.with(hr().withClass("col-12"));
        row.with(
            form().withMethod("post").attr("accept-charset", "utf-8").withAction(importDumpLink)
                .with(
                    input().withName("file").withPlaceholder("file").isRequired()
                        .withType("file").withClass("col-12"),
                    input().withType("submit").withName("uploadExportDumpButton")
                        .withValue("import XML dump").withClass("col-12")
                ).withClass("importXmlForm")
        );
        row.with(ul().withClass("col-12").with(
            li("Will erase all existing articles."),
            li("No import of configuration values."),
            li("Articles will be imported under your current login.")
        ));
        row.with(hr().withClass("col-12"));

        htmlBuilder.mainAdd(div(row).withClass("container"));

        return Response.ok().entity(htmlBuilder.toString()).build();
    }

}
