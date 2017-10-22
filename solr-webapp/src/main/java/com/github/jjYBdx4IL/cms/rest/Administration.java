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
import static j2html.TagCreator.hr;
import static j2html.TagCreator.span;

import com.github.jjYBdx4IL.cms.jpa.QueryFactory;
import com.github.jjYBdx4IL.cms.rest.app.HtmlBuilder;
import com.github.jjYBdx4IL.cms.rest.app.Role;
import com.github.jjYBdx4IL.cms.solr.IndexingTask;
import j2html.tags.ContainerTag;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

//CHECKSTYLE:OFF
@Path("admin")
@RolesAllowed(Role.ADMIN)
@Transactional
public class Administration {

    @Context
    UriInfo uriInfo;
    @Inject
    HtmlBuilder htmlBuilder;
    @Inject
    QueryFactory qf;
    @PersistenceContext
    EntityManager em;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response admin() throws Exception {

        htmlBuilder.setPageTitle("Administration");
        htmlBuilder.enableJsAminSupport();
        ContainerTag row = div().withClass("row");

        row.with(hr().withClass("col-12"));

        // DB snapshot dump download
        String dbSnapshotLink = uriInfo.getBaseUriBuilder().path(Administration.class)
            .path(Administration.class, "createDbSnapshot").build().toString();
        row.with(div(
            a("create database snapshot").withHref(dbSnapshotLink)
        ).withClass("col-12"));

        row.with(hr().withClass("col-12"));

        String pauseLink = uriInfo.getBaseUriBuilder().path(Administration.class)
            .path(Administration.class, "pauseIndexer").build().toString();
        ContainerTag statusTag;
        ContainerTag pauseLinkTag = null;
        if (IndexingTask.isTerminated.get()) {
            statusTag = span("terminated").withClass("error");
        } else if (IndexingTask.isPaused.get()) {
            statusTag = span("paused").withClass("warning");
            pauseLinkTag = a("unpause").withHref(pauseLink + "?pause=false");
        } else {
            statusTag = span(String.format("running (last activity: %d seconds ago)",
                (System.currentTimeMillis() - IndexingTask.ping.get()) / 1000l)).withClass("success");
            pauseLinkTag = a("pause").withHref(pauseLink + "?pause=true");
        }
        row.with(div(
            span("Indexer status: "),
            statusTag
        ).withClass("col-12"));
        row.with(div(
            pauseLinkTag
        ).withClass("col-12"));

        row.with(hr().withClass("col-12"));

        htmlBuilder.mainAdd(div(row).withClass("container"));
        return Response.ok().entity(htmlBuilder.toString()).build();
    }

    @Path("pauseIndexer")
    @GET
    public Response pauseIndexer(@QueryParam("pause") boolean pause) {
        IndexingTask.pauseRequested.set(pause);

        String adminLink = uriInfo.getBaseUriBuilder().path(Administration.class).build().toString();
        htmlBuilder.mainAdd(div(div(div(
            a("back to admin page").withHref(adminLink)
        ).withClass("col-12")).withClass("row")).withClass("container"));
        return Response.ok().entity(htmlBuilder.toString()).build();
    }

    @Path("createDbSnapshot")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response createDbSnapshot() {
        htmlBuilder.setPageTitle("Create DB Snapshot");

        File file = new File("backupws.zip");
        Query q = em.createNativeQuery(
            String.format(Locale.ROOT, "backup to '%s'", file.getAbsolutePath().replace("'", "''")));
        q.executeUpdate();

        if (!testZipFile(file)) {
            return Response.serverError().build();
        }

        ContainerTag row = div().withClass("row");
        row.with(div("Snapshot created.").withClass("col-12 success"));
        htmlBuilder.mainAdd(div(row).withClass("container"));
        return Response.ok().entity(htmlBuilder.toString()).build();
    }

    public static boolean testZipFile(File file) {
        long count = 0;
        byte[] buf = new byte[4096];
        try (ZipFile zipFile = new ZipFile(file)) {
            for (ZipEntry zipEntry : Collections.list(zipFile.entries())) {
                try (InputStream is = zipFile.getInputStream(zipEntry)) {
                    while (is.read(buf) != -1) {
                    }
                }
                zipEntry.getCrc();
                zipEntry.getCompressedSize();
                zipEntry.getName();
                count++;
            }
        } catch (IOException e) {
            return false;
        }
        return count > 0;
    }

}
