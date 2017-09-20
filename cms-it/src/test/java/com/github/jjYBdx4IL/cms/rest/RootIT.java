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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.github.jjYBdx4IL.cms.jaxb.dto.ArticleDTO;
import com.github.jjYBdx4IL.cms.jaxb.dto.ExportDump;
import com.github.jjYBdx4IL.utils.jersey.JerseyClientUtils;
import com.github.jjYBdx4IL.utils.text.PasswordGenerator;
import com.github.jjYBdx4IL.wsverifier.WebsiteVerifier;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

public class RootIT {

    private static final Logger LOG = LoggerFactory.getLogger(RootIT.class);
    
    private static final String rootUrl = "http://localhost:" + System.getProperty("http.port", "8080") + "/";

    private static Client client = null;

    @BeforeClass
    public static void beforeClass() {
        Response response = (Response) getTarget("devel/prepareDb4It").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }
    
    @Test
    public void testRssFeed() throws Exception {
        LOG.info("testRssFeed()");
        Response response = (Response) getTarget("devel/login").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        final String title = "test title mit ö" + PasswordGenerator.generate55(11);
        final String content = "content" + PasswordGenerator.generate55(11);
        final String tag = "aTag" + PasswordGenerator.generate55(11);
        final String pathId = "p" + PasswordGenerator.generate55(11);

        Form form = new Form().param("title", title).param("content", content).param("tags", tag).param("pathId",
            pathId).param("processed", "processed bla");
        ;

        response = (Response) getTarget("articleManager/create").request()
            .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));
        assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, response.getStatus());

        response = (Response) getTarget("logout").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        response = (Response) getTarget("feed/rss.xml").request(MediaType.APPLICATION_ATOM_XML).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        String rss = response.readEntity(String.class);

        assertTrue(rss, rss.contains(">" + title + "<"));
    }

    @Test
    public void createSomeArticles() throws Exception {
        LOG.info("createSomeArticles()");
        Response response = (Response) getTarget("devel/login").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        final String title = "test title <script>window.alert('ups');</script>";
        final String content = "embed://youtube/d4e03F3lLco/19m29s";
        final String tag = "aTag";
        final String pathId = "p" + PasswordGenerator.generate55(11);

        Form form = new Form().param("title", title).param("content", content).param("tags", tag).param("pathId",
            pathId).param("processed", "processed bla");

        // rejected because title has stuff in it that needs sanitizing
        response = (Response) getTarget("articleManager/create").request()
            .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());

        // fix title and try again
        form = new Form().param("title", "asd").param("content", content).param("tags", tag).param("pathId",
            pathId).param("processed", "processed bla");
        response = (Response) getTarget("articleManager/create").request()
            .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));
        assertEquals(response.readEntity(String.class), HttpServletResponse.SC_MOVED_TEMPORARILY, response.getStatus());
    }

    @Test
    public void createSpam() throws Exception {
        LOG.info("createSpam()");
        Response response = (Response) getTarget("devel/login").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        for (int i = 0; i < 30; i++) {
            final String title = "t" + PasswordGenerator.generate55(11);
            final String content = "c" + PasswordGenerator.generate55(11);
            final String tag = "spam";
            final String pathId = "p" + PasswordGenerator.generate55(11);

            Form form = new Form().param("title", title).param("content", content).param("tags", tag).param("pathId",
                pathId).param("processed", "processed bla");

            response = (Response) getTarget("articleManager/create").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));
            assertEquals(response.readEntity(String.class), HttpServletResponse.SC_MOVED_TEMPORARILY,
                response.getStatus());
        }
    }

    @Test
    public void testWorkflow() throws Exception {
        LOG.info("testWorkflow()");
        // GET empty main page
        Response response = (Response) getTarget("").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        // test access
        response = (Response) getTarget("articleManager").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
        response.readEntity(String.class);

        response = (Response) getTarget("articleManager/export").request(MediaType.TEXT_HTML, MediaType.TEXT_XML).get();
        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
        response.readEntity(String.class);

        // devel login
        response = (Response) getTarget("devel/login").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        final String title = "t" + PasswordGenerator.generate55(11);
        final String content = "c" + PasswordGenerator.generate55(11);
        final String tag = "a" + PasswordGenerator.generate55(11);

        final String titleB = "ö" + PasswordGenerator.generate55(11);
        final String contentB = "ä" + PasswordGenerator.generate55(11);
        final String tagB = "a" + PasswordGenerator.generate55(11);
        final String pathIdB = "c" + contentB.substring(1);

        Form form = new Form().param("title", title).param("content", content).param("tags", tag).param("pathId",
            title).param("processed", content);

        response = (Response) getTarget("articleManager/create").request()
            .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));
        assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, response.getStatus());

        form = new Form().param("title", titleB).param("content", contentB).param("tags", tagB).param("pathId",
            pathIdB).param("processed", contentB);

        response = (Response) getTarget("articleManager/create").request()
            .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));
        assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, response.getStatus());

        // check for updated main page
        response = (Response) getTarget("").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        String responseContent = response.readEntity(String.class);
        assertTrue(responseContent.contains(title));
        assertTrue(responseContent, responseContent.contains(titleB)); // <--
                                                                       // also
                                                                       // checks
                                                                       // encoding
                                                                       // processing

        // check /byTag/...
        response = (Response) getTarget("byTag/" + tag).request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        responseContent = response.readEntity(String.class);
        assertTrue(responseContent.contains(title));
        assertFalse(responseContent.contains(titleB));

        response = (Response) getTarget("byTag/" + tag.toLowerCase()).request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertTrue(response.readEntity(String.class).contains(title));

        response = (Response) getTarget("byTag/" + tag.toUpperCase()).request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertTrue(response.readEntity(String.class).contains(title));

        response = (Response) getTarget("byTag/notexisting12345asd").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertFalse(response.readEntity(String.class).contains(title));

        // check /search?q=...
        response = (Response) getTarget("search?q=" + title).request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertTrue(response.readEntity(String.class).contains(title));

        response = (Response) getTarget("search?q=" + content).request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertTrue(response.readEntity(String.class).contains(title));

        response = (Response) getTarget("search?q=notexisting389jk4387d").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertFalse(response.readEntity(String.class).contains(title));

        // check export
        form = new Form().param("title", title).param("content", content).param("tags", tag).param("pathId",
            title + "2").param("processed", content);

        response = (Response) getTarget("articleManager/create").request()
            .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));
        assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, response.getStatus());

        response = (Response) getTarget("articleManager/export").request(MediaType.TEXT_XML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        responseContent = response.readEntity(String.class);

        JAXBContext jaxbContext = JAXBContext.newInstance(ExportDump.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        ExportDump dump = (ExportDump) jaxbUnmarshaller.unmarshal(new ByteArrayInputStream(responseContent.getBytes()));

        ArticleDTO article = null;
        for (ArticleDTO _article : dump.getArticles()) {
            if (title.equals(_article.getTitle())) {
                article = _article;
                break;
            }
        }
        assertEquals(content, article.getContent());
        assertEquals(1, article.getTags().size());
        assertTrue(article.getTags().contains(tag));

        // check import
        response = (Response) getTarget("articleManager/import").request()
            .post(Entity.entity(responseContent, MediaType.TEXT_XML));
        assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());

        // clean up
        response = (Response) getTarget("devel/clean").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    public void verifyLinks() {
        LOG.info("verifyLinks()");
        WebsiteVerifier verifier = new WebsiteVerifier();
        if (!verifier.verify(rootUrl, "^googleLogin$")) {
            fail(verifier.resultToString());
        }
    }
    
   @After
   public void after() {
       if (client != null) {
           client.close();
           client = null;
       }
   }

    protected static WebTarget getTarget(String path) {
        return getClient().target(rootUrl + path);
    }

    protected static Client getClient() {
        if (client != null) {
            return client;
        }
        client = JerseyClientUtils.createClient();
        return client;
    }
}
