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

import org.junit.Test;

import java.io.ByteArrayInputStream;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

public class RootIT {

    private Client client = null;

    private static final String rootUrl = "http://localhost:" + System.getProperty("jetty.http.port", "8080") + "/";

    @Test
    public void testWorkflow() throws Exception {
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

        final String titleB = "t" + PasswordGenerator.generate55(11);
        final String contentB = "c" + PasswordGenerator.generate55(11);
        final String tagB = "a" + PasswordGenerator.generate55(11);

        response = (Response) getTarget("articleManager/create").request()
            .post(Entity.entity(String.format("title=%s&content=%s&tags=%s&pathId=%s", title, content, tag, title),
                MediaType.APPLICATION_FORM_URLENCODED));
        assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, response.getStatus());

        response = (Response) getTarget("articleManager/create").request()
            .post(Entity.entity(String.format("title=%s&content=%s&tags=%s&pathId=%s", titleB, contentB, tagB, titleB),
                MediaType.APPLICATION_FORM_URLENCODED));
        assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, response.getStatus());

        // check for updated main page
        response = (Response) getTarget("").request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertTrue(response.readEntity(String.class).contains(title));

        // check /byTag/...
        response = (Response) getTarget("byTag/" + tag).request(MediaType.TEXT_HTML_TYPE).get();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        String responseContent = response.readEntity(String.class);
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
        response = (Response) getTarget("articleManager/create").request()
            .post(Entity.entity(
                String.format("title=%s123&content=%s123&tags=%s&pathId=%s123", title, content, tag, title),
                MediaType.APPLICATION_FORM_URLENCODED));
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
    }

    @Test
    public void verifyLinks() {
        WebsiteVerifier verifier = new WebsiteVerifier();
        if (!verifier.verify(rootUrl)) {
            fail(verifier.resultToString());
        }
    }

    protected WebTarget getTarget(String path) {
        return getClient().target(rootUrl + path);
    }

    protected Client getClient() {
        if (client != null) {
            return client;
        }
        client = JerseyClientUtils.createClient();
        return client;
    }
}
