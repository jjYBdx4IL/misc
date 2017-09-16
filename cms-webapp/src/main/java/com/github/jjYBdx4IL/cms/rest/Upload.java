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

import com.github.jjYBdx4IL.cms.jpa.QueryFactory;
import com.github.jjYBdx4IL.cms.jpa.dto.MediaFile;
import com.github.jjYBdx4IL.cms.rest.app.HtmlBuilder;
import com.github.jjYBdx4IL.cms.rest.app.SessionData;
import org.hibernate.Session;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.annotation.security.PermitAll;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

//CHECKSTYLE:OFF
@Path("upload")
@PermitAll
@Transactional
public class Upload {

    private static final Logger LOG = LoggerFactory.getLogger(Upload.class);

    public static final int MAX_THUMBNAIL_WIDTH = 120;
    public static final int MAX_THUMBNAIL_HEIGHT = 75;
    
    @Context
    UriInfo uriInfo;
    @Inject
    SessionData session;
    @PersistenceContext
    EntityManager em;
    @Inject
    HtmlBuilder htmlBuilder;
    @Inject
    QueryFactory qf;

    private File tempFile = null;
    
    @Path("")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response get() throws SQLException {
        LOG.trace("get()");

        htmlBuilder.addHeadFragment(getClass().getResource("fineuploader.tpl"));

        htmlBuilder.setJsValue("fineUploaderEndpoint",
            uriInfo.getBaseUriBuilder().path(Upload.class).build().toString());
        htmlBuilder.addCssUrl(
            "//cdnjs.cloudflare.com/ajax/libs/file-uploader/5.15.0/jquery.fine-uploader/fine-uploader-new.min.css");
        htmlBuilder.setPageTitle("Upload");
        htmlBuilder.mainAdd(
            div(
                div(
                    div().withClass("col-12").withId("fine-uploader-gallery")
                ).withClass("row")
            ).withClass("container")
        );
        return Response.ok(htmlBuilder.toString()).build();
    }

    // https://docs.fineuploader.com/branch/master/endpoint_handlers/traditional.html
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({ "text/plain", MediaType.APPLICATION_JSON })
    @Transactional
    public Response post(MultipartFormDataInput input) {
        LOG.info("post " + input + " " + session.isAuthenticated());

        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        InputStream inputStream = null; // input stream needs to remain open
                                        // until TX commit
        InputStream thumbnailStream = null; // ^- same
        try {
            inputStream = uploadForm.get("qqfile").get(0).getBody(InputStream.class, null);
            String filename = uploadForm.get("qqfilename").get(0).getBodyAsString();
            long filesize = Long.parseLong(uploadForm.get("qqtotalfilesize").get(0).getBodyAsString());
            MediaType contentType = uploadForm.get("qqfile").get(0).getMediaType();
            MediaFile media = new MediaFile();
            media.setContentType(contentType.toString());
            media.setCreatedAt(new Date());
            media.setLastModified(media.getCreatedAt());
            media.setFilesize(filesize);
            media.setFilename(filename);
            media.setOwner(qf.getUserByUid(session.getUid()));

            Session session = (Session) em.getDelegate();
            Blob blob = session.getLobHelper().createBlob(inputStream, filesize);
            media.setData(blob);

            // create thumbnail
            BufferedImage image;
            try (InputStream is = uploadForm.get("qqfile").get(0).getBody(InputStream.class, null)) {
                image = ImageIO.read(is);
            }
            BufferedImage thumbnail = Scalr.resize(image,
                Method.ULTRA_QUALITY, MAX_THUMBNAIL_WIDTH, MAX_THUMBNAIL_HEIGHT);
            image.flush();
            tempFile = File.createTempFile("tmp", ".jpg");
            try {
                ImageIO.write(thumbnail, "jpeg", tempFile);
                thumbnail.flush();
                thumbnailStream = new FileInputStream(tempFile);
                Blob blob2 = session.getLobHelper().createBlob(thumbnailStream, tempFile.length());
                media.setPreview(blob2);
            } finally {
                thumbnail.flush();
            }

            em.persist(media);
            LOG.debug("db id: " + media.getId());
        } catch (Exception ex) {
            LOG.error("", ex);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOG.warn("", e);
                }
            }
            return Response.ok("{\"success\": false}").build();
        }

        return Response.ok("{\"success\": true}").build();
    }

    @PreDestroy
    public void preDestroy() {
        if (tempFile != null) {
            if (!tempFile.delete()) {
                LOG.warn("failed to delete " + tempFile);
            }
        }
    }
}
