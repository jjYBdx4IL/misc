package com.github.jjYBdx4IL.cms.rest;

import com.github.jjYBdx4IL.cms.jpa.dto.ExampleItem;
import com.github.jjYBdx4IL.cms.jpa.tx.Tx;
import com.github.jjYBdx4IL.cms.jpa.tx.TxRo;

import org.glassfish.jersey.process.internal.RequestScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/hello")
@RequestScoped
public class Hello {

    private static final Logger LOG = LoggerFactory.getLogger(Hello.class);

    public Hello() {
        LOG.info("init()");
    }

    @Inject
    public EntityManager em;

    // This method is called if TEXT_PLAIN is request
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sayPlainTextHello() {
        return "Hello Jersey";
    }

    // This method is called if XML is request
    @GET
    @Produces(MediaType.TEXT_XML)
    public String sayXMLHello() {
        return "<?xml version=\"1.0\"?>" + "<hello> Hello Jersey" + "</hello>";
    }

    // This method is called if HTML is request
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Tx
    public String sayHtmlHello(
        @DefaultValue("") @QueryParam("data1") String data1,
        @DefaultValue("") @QueryParam("data2") String data2,
        @DefaultValue("false") @QueryParam("fail") boolean fail) {

        LOG.info("sayHtmlHello()");

        ExampleItem item = new ExampleItem();
        item.setData1(data1);
        item.setData2(data2);
        em.persist(item);

        if (fail) {
            throw new RuntimeException();
        }

        return "<html><body><h1>id = " + item.getId() + "</h1></body></html>";
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @TxRo
    @Path("/get/{id}")
    public String sayHtmlHelloGet(@DefaultValue("0") @PathParam("id") int id) {
        LOG.info("sayHtmlHello()");

        ExampleItem item = em.find(ExampleItem.class, id);

        return "<html><body><h1>" + item + "</h1></body></html>";
    }
}