package com.github.jjYBdx4IL.cms.rest;

import static org.junit.Assert.*;

import com.github.jjYBdx4IL.wsverifier.WebsiteVerifier;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RootIT {

    private static final Logger LOG = LoggerFactory.getLogger(RootIT.class);
    private Client client = null;

    private static final String rootUrl = "http://localhost:" + System.getProperty("jetty.http.port", "9999") + "/";

    @Test
    public void testGetMainPage() {
        WebTarget webTarget = getClient().target(rootUrl);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.TEXT_HTML);

        // GET non-existing element
        Response response = (Response) invocationBuilder.get();
        assertNotNull(response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        //assertTrue(response.readEntity(String.class).contains("<h3>Please enter key-value pair</h3>"));
    }

    @Test
    public void verifyLinks() {
        WebsiteVerifier verifier = new WebsiteVerifier();
        if (!verifier.verify(rootUrl)) {
            fail(verifier.resultToString());
        }
    }

    protected Client getClient() {
        if (client != null) {
            return client;
        }
        java.util.logging.Logger LOGJ = java.util.logging.Logger.getLogger(RootIT.class.getName());
        client = ClientBuilder.newClient(new ClientConfig());
        LOGJ.setLevel(java.util.logging.Level.FINEST);
        LOGJ.addHandler(new Handler() {

            @Override
            public void publish(LogRecord record) {
                LOG.info(record.getSourceClassName() + System.lineSeparator() + record.getMessage());

            }

            @Override
            public void flush() {
                // TODO Auto-generated method stub

            }

            @Override
            public void close() throws SecurityException {
                // TODO Auto-generated method stub

            }
        });
        LOGJ.log(java.util.logging.Level.FINEST, "test");
        client.register(new LoggingFeature(LOGJ, LoggingFeature.Verbosity.PAYLOAD_ANY));
        return client;
    }
}
