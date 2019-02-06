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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.github.jjYBdx4IL.cms.jaxb.dto.ArticleDTO;
import com.github.jjYBdx4IL.cms.jaxb.dto.ExportDump;
import com.github.jjYBdx4IL.utils.jersey.JerseyClientUtils;
import com.github.jjYBdx4IL.utils.text.PasswordGenerator;
import com.github.jjYBdx4IL.wsverifier.WebsiteVerifier;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class RootIT {

    private static final Logger LOG = LoggerFactory.getLogger(RootIT.class);

    private static final String rootUrl = "http://localhost:" + System.getProperty("http.port", "8081") + "/";

    private static Client client = null;

    @BeforeClass
    public static void beforeClass() {
        Response response = (Response) getTarget("devel/prepareDb4It").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }
    
    @Test
    public void testRobotsTxtAndSiteMap() throws Exception {
        LOG.info("testRobotsTxtAndSiteMap()");

        // create some site content
        Response response = (Response) getTarget("devel/login").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        final String title = "test title mit ö" + PasswordGenerator.generate55(11);
        final String content = "content" + PasswordGenerator.generate55(11);
        final String tag = "aTag" + PasswordGenerator.generate55(11);
        final String pathId = "p" + PasswordGenerator.generate55(11);

        Form form = new Form().param("title", title).param("content", content).param("tags", tag).param("pathId",
            pathId).param("processed", "processed bla").param("published", "on");

        response = (Response) getTarget("articleManager/create").request()
            .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));
        assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, response.getStatus());

        response = (Response) getTarget("logout").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        
        // get sitemap url from robots.txt
        response = (Response) getTarget("robots.txt").request(MediaType.TEXT_PLAIN_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        String responseContent = response.readEntity(String.class);
        
        Pattern pat = Pattern.compile("^sitemap:\\s*(.+)\\s*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher m = pat.matcher(responseContent);
        assertTrue(m.find());
        String siteMapLink = m.group(1);
        assertNotNull(siteMapLink);
        
        // and fetch sitemap at that url
        response = (Response) getClient().target(siteMapLink).request(MediaType.APPLICATION_XML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        responseContent = response.readEntity(String.class);
        assertTrue(responseContent.contains("<urlset"));
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
            pathId).param("processed", "processed bla").param("published", "on");

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

        final String title = "test title";
        final String content = "embed://youtube/d4e03F3lLco/19m29s <script>window.alert('ups');</script>";
        final String tag = "aTag";
        final String pathId = "p" + PasswordGenerator.generate55(11);

        Form form = new Form().param("title", title).param("content", content).param("tags", tag).param("pathId",
            pathId).param("processed", "processed bla").param("published", "on");

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
                pathId).param("processed", "processed bla").param("published", "on");

            response = (Response) getTarget("articleManager/create").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));
            assertEquals(response.readEntity(String.class), HttpServletResponse.SC_MOVED_TEMPORARILY,
                response.getStatus());
        }
    }

    @Test
    public void testContinueBug() {
        LOG.info("testContinueBug()");
        // GET continue page
        Response response = (Response) getTarget("continue/1000000").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        String content = response.readEntity(String.class);
        assertFalse(content, content.toLowerCase().contains("j2html.tags.ContainerTag@".toLowerCase()));
    }
    
    @Test
    public void testWorkflow() throws Exception {
        LOG.info("testWorkflow()");
        // GET main page
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
            title).param("processed", content).param("published", "on");

        response = (Response) getTarget("articleManager/create").request()
            .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));
        assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, response.getStatus());

        form = new Form().param("title", titleB).param("content", contentB).param("tags", tagB).param("pathId",
            pathIdB).param("processed", contentB).param("published", "on");

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
    }

    @Test
    public void testPublishedFlag() throws JAXBException {
        // clean up
        Response response = (Response) getTarget("devel/clean").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        // login
        response = (Response) getTarget("devel/login").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        final String title = "t" + PasswordGenerator.generate55(11);
        final String content = "c" + PasswordGenerator.generate55(11);
        final String tag = "a" + PasswordGenerator.generate55(11);
        final String pathId = title + "2";

        Form form = new Form().param("title", title).param("content", content).param("tags", tag).param("pathId",
            pathId).param("processed", content);

        response = (Response) getTarget("articleManager/create").request()
            .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));
        assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, response.getStatus());

        // saved but not published
        response = (Response) getTarget("").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertFalse(response.readEntity(String.class).contains(title));

        response = (Response) getTarget("byTag/" + tag).request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertFalse(response.readEntity(String.class).contains(title));

        response = (Response) getTarget("0000/00/00/" + pathId).request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        assertFalse(response.readEntity(String.class).contains(title));

        response = (Response) getTarget("search?q=" + title).request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        String responseContent = response.readEntity(String.class);
        assertFalse(responseContent, responseContent.contains(content));

        response = (Response) getTarget("feed/rss.xml").request(MediaType.APPLICATION_ATOM_XML).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        responseContent = response.readEntity(String.class);
        assertFalse(responseContent, responseContent.contains(">" + title + "<"));

        response = (Response) getTarget("sitemap.xml").request(MediaType.APPLICATION_XML).get();
        assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
        responseContent = response.readEntity(String.class);
        assertFalse(responseContent, responseContent.contains("/" + pathId + "</loc>"));

        // get article id for editing
        response = (Response) getTarget("articleManager").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        String html = response.readEntity(String.class);
        Pattern pat = Pattern.compile("articleManager/edit/\\d+");
        Matcher m = pat.matcher(html);
        assertTrue(m.find());

        // publish it
        form = new Form().param("title", title).param("content", content).param("tags", tag).param("pathId",
            title + "2").param("processed", content).param("published", "on");

        response = (Response) getTarget(m.group(0)).request()
            .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));
        assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, response.getStatus());

        // saved and published
        response = (Response) getTarget("").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertTrue(response.readEntity(String.class).contains(title));

        response = (Response) getTarget("byTag/" + tag).request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertTrue(response.readEntity(String.class).contains(title));

        response = (Response) getTarget("0000/00/00/" + pathId).request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertTrue(response.readEntity(String.class).contains(title));

        response = (Response) getTarget("search?q=" + title).request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        responseContent = response.readEntity(String.class);
        assertTrue(responseContent, responseContent.contains(content));

        response = (Response) getTarget("feed/rss.xml").request(MediaType.APPLICATION_ATOM_XML).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        responseContent = response.readEntity(String.class);
        assertTrue(responseContent, responseContent.contains(">" + title + "<"));

        response = (Response) getTarget("sitemap.xml").request(MediaType.APPLICATION_XML).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        responseContent = response.readEntity(String.class);
        assertTrue(responseContent, responseContent.contains("/" + pathId + "</loc>"));

        // unpublish it again
        form = new Form().param("title", title).param("content", content).param("tags", tag).param("pathId",
            title + "2").param("processed", content);

        response = (Response) getTarget(m.group(0)).request()
            .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));
        assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, response.getStatus());

        // unpublished
        response = (Response) getTarget("").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertFalse(response.readEntity(String.class).contains(title));

        response = (Response) getTarget("byTag/" + tag).request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertFalse(response.readEntity(String.class).contains(title));

        response = (Response) getTarget("0000/00/00/" + pathId).request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        assertFalse(response.readEntity(String.class).contains(title));

        response = (Response) getTarget("search?q=" + title).request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        responseContent = response.readEntity(String.class);
        assertFalse(responseContent, responseContent.contains(content));

        response = (Response) getTarget("feed/rss.xml").request(MediaType.APPLICATION_ATOM_XML).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        responseContent = response.readEntity(String.class);
        assertFalse(responseContent, responseContent.contains(">" + title + "<"));
        
        response = (Response) getTarget("sitemap.xml").request(MediaType.APPLICATION_XML).get();
        assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
        responseContent = response.readEntity(String.class);
        assertFalse(responseContent, responseContent.contains("/" + pathId + "</loc>"));
    }

    @Test
    public void testSearch() {
        Response response = (Response) getTarget("devel/login").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        final String title = "t" + PasswordGenerator.generate55(11);
        final String content = "c" + PasswordGenerator.generate55(11);
        final String tag = "a" + PasswordGenerator.generate55(11);

        Form form = new Form().param("title", title).param("content", content).param("tags", tag).param("pathId",
            title + "2").param("processed", content).param("published", "on");

        response = (Response) getTarget("articleManager/create").request()
            .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));
        assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, response.getStatus());

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
    }

    @Test
    public void testExportImport() throws JAXBException, IOException {
        Response response = (Response) getTarget("devel/login").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        final String title = "t" + PasswordGenerator.generate55(11);
        final String content = "c" + PasswordGenerator.generate55(11);
        final String tag = "a" + PasswordGenerator.generate55(11);

        Form form = new Form().param("title", title).param("content", content).param("tags", tag).param("pathId",
            title + "2").param("processed", content).param("published", "on");

        response = (Response) getTarget("articleManager/create").request()
            .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));
        assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, response.getStatus());

        response = (Response) getTarget("articleManager/export").request(MediaType.TEXT_XML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        String export = response.readEntity(String.class);

        LOG.info("export: " + export);

        JAXBContext jaxbContext = JAXBContext.newInstance(ExportDump.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        ExportDump dump = (ExportDump) jaxbUnmarshaller.unmarshal(new ByteArrayInputStream(export.getBytes("UTF-8")));

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

        // clean up
        response = (Response) getTarget("devel/clean").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        // verify it's deleted
        response = (Response) getTarget("").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        String responseContent = response.readEntity(String.class);
        assertFalse(responseContent.contains(title));

        // check import
        response = (Response) getTarget("articleManager/import").request()
            .post(Entity.entity(export, MediaType.TEXT_XML));
        assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());

        // verify it's restored
        response = (Response) getTarget("").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        responseContent = response.readEntity(String.class);
        assertTrue(responseContent.contains(title));

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
