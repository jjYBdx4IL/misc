package com.github.jjYBdx4IL.cms.rest.app;

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
        LOG.trace("init()");
        this.closeService = closeService;
        this.emf = emf;
    }

    @Override
    public EntityManager provide() {
        LOG.trace("provide()");
        final EntityManager em = emf.createEntityManager();
        if (LOG.isTraceEnabled()) {
            LOG.trace("created " + em);
        }
        this.closeService.add(new Closeable() {
            @Override
            public void close() {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("closing " + em);
                }
            }
        });
        return em;
    }

    @Override
    public void dispose(EntityManager em) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("dispose() " + em);
        }
        em.close();
    }

}
