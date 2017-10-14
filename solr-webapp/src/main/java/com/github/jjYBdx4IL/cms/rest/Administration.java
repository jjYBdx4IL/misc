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
import static j2html.TagCreator.li;
import static j2html.TagCreator.ul;

import com.github.jjYBdx4IL.cms.jpa.QueryFactory;
import com.github.jjYBdx4IL.cms.rest.app.HtmlBuilder;
import com.github.jjYBdx4IL.cms.rest.app.Role;
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
        String dbDumpLink = uriInfo.getBaseUriBuilder().path(Administration.class)
            .path(Administration.class, "dbDump").build().toString();
        row.with(div(
            a("download database snapshot dump (includes all data except SSL key/cert)").withHref(dbDumpLink)
        ).withClass("col-12"));

        row.with(hr().withClass("col-12"));

        // Submit sitemap to BING
        String siteMapLink = uriInfo.getBaseUriBuilder().path(SiteMap.class).build().toString();
        String bingSiteMapSubLink = "http://www.bing.com/ping?sitemap=" + URLEncoder.encode(siteMapLink, "UTF-8");        
        row.with(div(
            a("submit sitemap to BING").withHref(bingSiteMapSubLink)
        ).withClass("col-12"));

        row.with(hr().withClass("col-12"));
        
        htmlBuilder.mainAdd(div(row).withClass("container"));
        return Response.ok().entity(htmlBuilder.toString()).build();
    }

    @Path("dbDump.zip")
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response dbDump() {

        File file = new File("backup.zip");
        Query q = em.createNativeQuery(
            String.format(Locale.ROOT, "backup to '%s'", file.getAbsolutePath().replace("'", "''")));
        q.executeUpdate();

        if (!testZipFile(file)) {
            return Response.serverError().build();
        }

        return Response.ok().entity(file).build();
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
