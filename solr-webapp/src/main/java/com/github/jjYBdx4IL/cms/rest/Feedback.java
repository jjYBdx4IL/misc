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
import static j2html.TagCreator.textarea;

import com.github.jjYBdx4IL.cms.jpa.AppCache;
import com.github.jjYBdx4IL.cms.jpa.QueryFactory;
import com.github.jjYBdx4IL.cms.jpa.dto.ConfigKey;
import com.github.jjYBdx4IL.cms.rest.app.HtmlBuilder;
import j2html.tags.ContainerTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

//CHECKSTYLE:OFF
@Path("feedback")
@PermitAll
@Transactional
public class Feedback {

    private static final Logger LOG = LoggerFactory.getLogger(Feedback.class);

    @Context
    UriInfo uriInfo;
    @Inject
    HtmlBuilder htmlBuilder;
    @Inject
    QueryFactory qf;
    @Inject
    AppCache appCache;
    @Resource(name = "java:jboss/mail/Default")
    Session mailSession;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response get() throws Exception {
        htmlBuilder.setPageTitle("Feedback");

        ContainerTag container = div().withClass("container");

        container.with(
            form().withMethod("POST").attr("accept-charset", "utf-8").with(
                input().withName("from")
                    .withPlaceholder("Your e-mail address goes here.")
                    .isRequired()
                    .attr("autofocus")
                    .withClass("col-12"),
                textarea().withName("msg")
                    .withPlaceholder("Enter your message here.")
                    .isRequired()
                    .withClass("col-12"),
                input().withType("submit")
                    .withName("submitButton")
                    .withValue("send")
                    .withClass("col-12")
            ).withClass("row feedbackForm")
        );

        htmlBuilder.mainAdd(container);

        return Response.ok(htmlBuilder.toString()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response post(@FormParam("from") String from, @FormParam("msg") String msg) throws Exception {
        htmlBuilder.setPageTitle("Feedback");

        if (from == null || from.isEmpty() || msg == null || msg.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        boolean success = send(from, msg);

        ContainerTag container = div().withClass("container");

        if (success) {
            container.with(
                div().with(
                    div("Your message has been sent. Thank you!")
                        .withClass("col-12 success")
                ).withClass("row")
            );
        } else {
            container.with(
                div().with(
                    div("There was an error sending your message. Please come back later.")
                        .withClass("col-12 error")
                ).withClass("row")
            );
        }

        htmlBuilder.mainAdd(container);

        return Response.ok(htmlBuilder.toString()).build();
    }

    private boolean send(String from, String content) {
        Transport t = null;
        try {
            MimeMessage msg = new MimeMessage(mailSession);
            msg.setRecipient(Message.RecipientType.TO,
                new InternetAddress(appCache.get(ConfigKey.FEEDBACK_MAILTO_ADDR)));
            msg.setSubject("Feedback");
            msg.setFrom(new InternetAddress(from));
            Multipart mp = new MimeMultipart();
            BodyPart bp = new MimeBodyPart();
            bp.setText(content);
            mp.addBodyPart(bp);
            msg.setContent(mp);
            // set the message content here
            t = mailSession.getTransport();
            t.connect();
            t.sendMessage(msg, msg.getAllRecipients());
            return true;
        } catch (MessagingException ex) {
            LOG.error("", ex);
            Status.ERROR_COUNTER.incrementAndGet();
        } finally {
            try {
                if (t != null) {
                    t.close();
                }
            } catch (Exception ex) {
            }
        }
        return false;
    }
}
