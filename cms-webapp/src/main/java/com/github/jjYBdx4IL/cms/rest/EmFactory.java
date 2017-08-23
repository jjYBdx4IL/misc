package com.github.jjYBdx4IL.cms.rest;

import org.glassfish.hk2.api.Factory;
import org.glassfish.jersey.server.CloseableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class EmFactory implements Factory<EntityManager> {

    private static final Logger LOG = LoggerFactory.getLogger(EmFactory.class);

    private final EntityManagerFactory emf;
    private final CloseableService closeService;

    @Inject
    public EmFactory(EntityManagerFactory emf, CloseableService closeService) {
        LOG.info("init()");
        this.closeService = closeService;
        this.emf = emf;
    }

    @Override
    public EntityManager provide() {
        LOG.info("provide()");
        final EntityManager em = emf.createEntityManager();
        LOG.info("created " + em);
        this.closeService.add(new Closeable() {
            @Override
            public void close() {
                LOG.info("closing " + em);
                em.close();
            }
        });
        return em;
    }

    @Override
    public void dispose(EntityManager instance) {
        LOG.info("dispose() " + instance);
    }

}
