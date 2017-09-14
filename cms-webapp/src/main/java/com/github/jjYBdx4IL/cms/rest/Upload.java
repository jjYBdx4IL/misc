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
import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.security.PermitAll;
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

    @Path("")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response get() throws SQLException {
        LOG.trace("get()");

        // ContainerTag tplTag =
        // script().withType("text/template").withId("qq-template-gallery")
        // .withSrc(uriInfo.getBaseUriBuilder().build().toString() +
        // "assets/fineuploader.tpl");
        // htmlBuilder.addHeadContent(tplTag);
        // https://fineuploader.com/demos.html
        htmlBuilder.addHeadFragment(
            "    <script type=\"text/template\" id=\"qq-template-gallery\">     <div class=\"qq-uploader-selector qq-uploader qq-gallery\" qq-drop-area-text=\"Drop files here\">\n"
                +
                "            <div class=\"qq-total-progress-bar-container-selector qq-total-progress-bar-container\">\n"
                +
                "                <div role=\"progressbar\" aria-valuenow=\"0\" aria-valuemin=\"0\" aria-valuemax=\"100\" class=\"qq-total-progress-bar-selector qq-progress-bar qq-total-progress-bar\"></div>\n"
                +
                "            </div>\n" +
                "            <div class=\"qq-upload-drop-area-selector qq-upload-drop-area\" qq-hide-dropzone>\n" +
                "                <span class=\"qq-upload-drop-area-text-selector\"></span>\n" +
                "            </div>\n" +
                "            <div class=\"qq-upload-button-selector qq-upload-button\">\n" +
                "                <div>Upload a file</div>\n" +
                "            </div>\n" +
                "            <span class=\"qq-drop-processing-selector qq-drop-processing\">\n" +
                "                <span>Processing dropped files...</span>\n" +
                "                <span class=\"qq-drop-processing-spinner-selector qq-drop-processing-spinner\"></span>\n"
                +
                "            </span>\n" +
                "            <ul class=\"qq-upload-list-selector qq-upload-list\" role=\"region\" aria-live=\"polite\" aria-relevant=\"additions removals\">\n"
                +
                "                <li>\n" +
                "                    <span role=\"status\" class=\"qq-upload-status-text-selector qq-upload-status-text\"></span>\n"
                +
                "                    <div class=\"qq-progress-bar-container-selector qq-progress-bar-container\">\n" +
                "                        <div role=\"progressbar\" aria-valuenow=\"0\" aria-valuemin=\"0\" aria-valuemax=\"100\" class=\"qq-progress-bar-selector qq-progress-bar\"></div>\n"
                +
                "                    </div>\n" +
                "                    <span class=\"qq-upload-spinner-selector qq-upload-spinner\"></span>\n" +
                "                    <div class=\"qq-thumbnail-wrapper\">\n" +
                "                        <img class=\"qq-thumbnail-selector\" qq-max-size=\"120\" qq-server-scale>\n" +
                "                    </div>\n" +
                "                    <button type=\"button\" class=\"qq-upload-cancel-selector qq-upload-cancel\">X</button>\n"
                +
                "                    <button type=\"button\" class=\"qq-upload-retry-selector qq-upload-retry\">\n" +
                "                        <span class=\"qq-btn qq-retry-icon\" aria-label=\"Retry\"></span>\n" +
                "                        Retry\n" +
                "                    </button>\n" +
                "\n" +
                "                    <div class=\"qq-file-info\">\n" +
                "                        <div class=\"qq-file-name\">\n" +
                "                            <span class=\"qq-upload-file-selector qq-upload-file\"></span>\n" +
                "                            <span class=\"qq-edit-filename-icon-selector qq-edit-filename-icon\" aria-label=\"Edit filename\"></span>\n"
                +
                "                        </div>\n" +
                "                        <input class=\"qq-edit-filename-selector qq-edit-filename\" tabindex=\"0\" type=\"text\">\n"
                +
                "                        <span class=\"qq-upload-size-selector qq-upload-size\"></span>\n" +
                "                        <button type=\"button\" class=\"qq-btn qq-upload-delete-selector qq-upload-delete\">\n"
                +
                "                            <span class=\"qq-btn qq-delete-icon\" aria-label=\"Delete\"></span>\n" +
                "                        </button>\n" +
                "                        <button type=\"button\" class=\"qq-btn qq-upload-pause-selector qq-upload-pause\">\n"
                +
                "                            <span class=\"qq-btn qq-pause-icon\" aria-label=\"Pause\"></span>\n" +
                "                        </button>\n" +
                "                        <button type=\"button\" class=\"qq-btn qq-upload-continue-selector qq-upload-continue\">\n"
                +
                "                            <span class=\"qq-btn qq-continue-icon\" aria-label=\"Continue\"></span>\n"
                +
                "                        </button>\n" +
                "                    </div>\n" +
                "                </li>\n" +
                "            </ul>\n" +
                "\n" +
                "            <dialog class=\"qq-alert-dialog-selector\">\n" +
                "                <div class=\"qq-dialog-message-selector\"></div>\n" +
                "                <div class=\"qq-dialog-buttons\">\n" +
                "                    <button type=\"button\" class=\"qq-cancel-button-selector\">Close</button>\n" +
                "                </div>\n" +
                "            </dialog>\n" +
                "\n" +
                "            <dialog class=\"qq-confirm-dialog-selector\">\n" +
                "                <div class=\"qq-dialog-message-selector\"></div>\n" +
                "                <div class=\"qq-dialog-buttons\">\n" +
                "                    <button type=\"button\" class=\"qq-cancel-button-selector\">No</button>\n" +
                "                    <button type=\"button\" class=\"qq-ok-button-selector\">Yes</button>\n" +
                "                </div>\n" +
                "            </dialog>\n" +
                "\n" +
                "            <dialog class=\"qq-prompt-dialog-selector\">\n" +
                "                <div class=\"qq-dialog-message-selector\"></div>\n" +
                "                <input type=\"text\">\n" +
                "                <div class=\"qq-dialog-buttons\">\n" +
                "                    <button type=\"button\" class=\"qq-cancel-button-selector\">Cancel</button>\n" +
                "                    <button type=\"button\" class=\"qq-ok-button-selector\">Ok</button>\n" +
                "                </div>\n" +
                "            </dialog>\n" +
                "        </div>\n" +
                "</script>");

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
        InputStream inputStream = null; // input stream needs to remain open until TX commit
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
            em.persist(media);
            LOG.info("db id: " + media.getId());
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

}
