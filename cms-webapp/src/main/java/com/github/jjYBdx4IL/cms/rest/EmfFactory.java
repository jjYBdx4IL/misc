package com.github.jjYBdx4IL.cms.rest;

import org.glassfish.hk2.api.Factory;
import org.h2.Driver;
import org.hibernate.boot.SchemaAutoTooling;
import org.hibernate.cfg.AvailableSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EmfFactory implements Factory<EntityManagerFactory> {

    private static final Logger LOG = LoggerFactory.getLogger(EmfFactory.class);

    private final EntityManagerFactory emf;

    public EmfFactory() throws NamingException {
        LOG.info("init()");
        this.emf = Persistence.createEntityManagerFactory("default", getProps());
        LOG.info("created " + emf);
    }


    public EntityManagerFactory provide() {
        LOG.info("provide()");
        return emf;
    }

    @Override
    public void dispose(EntityManagerFactory instance) {
        LOG.info("dispose() " + instance);
        emf.close();
        LOG.info("emf closed: " + emf);
    }

    protected Map<String, String> getProps() throws NamingException {
        Map<String, String> props = new HashMap<>();
        InitialContext ic = new InitialContext();
        String jdbcUrl = (String) ic.lookup("java:comp/env/jdbc/url");

        props.put(AvailableSettings.HBM2DDL_AUTO, SchemaAutoTooling.UPDATE.name().toLowerCase(Locale.ROOT));
        props.put(AvailableSettings.SHOW_SQL, "true");
        props.put(AvailableSettings.JPA_JDBC_DRIVER, Driver.class.getName());
        props.put(AvailableSettings.JPA_JDBC_URL, jdbcUrl);
        return props;
    }

}
