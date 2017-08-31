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

    private EntityManagerFactory emf = null;
    private boolean isOwner = false;

    public EmfFactory() {
        LOG.info("init()");
    }

    @Override
    public EntityManagerFactory provide() {
        LOG.info("provide()");
        if (emf == null) {
            getEmfInstance();
        }
        return emf;
    }

    @Override
    public void dispose(EntityManagerFactory instance) {
        LOG.info("dispose() " + instance);
        if (isOwner) {
            emf.close();
            emf = null;
            LOG.info("emf closed: " + emf);
        }
    }

    protected void getEmfInstance() {
        try {
            InitialContext ic = new InitialContext();
            try {
                // use an EMF instance provided by the app server if possible
                emf = (EntityManagerFactory) ic.lookup("java:comp/env/jpa/emf");
            } catch (NamingException ex) {
                // fall back to an EMF instance that we manage ourselves inside the webapp if needed
                // (bad for development because of mostly unnecessary EMF re-initializations on webapp reloads)
                Map<String, String> props = new HashMap<>();
                String jdbcUrl = (String) ic.lookup("java:comp/env/jdbc/url");
                props.put(AvailableSettings.HBM2DDL_AUTO, SchemaAutoTooling.UPDATE.name().toLowerCase(Locale.ROOT));
                props.put(AvailableSettings.SHOW_SQL, "true");
                props.put(AvailableSettings.JPA_JDBC_DRIVER, Driver.class.getName());
                props.put(AvailableSettings.JPA_JDBC_URL, jdbcUrl);
                //props.put("connection.pool_size", "100");
                emf = Persistence.createEntityManagerFactory("default", props);
                LOG.info("created " + emf);
                isOwner = true;
            }
        } catch (NamingException ex) {
            throw new RuntimeException(ex);
        }
    }

}
