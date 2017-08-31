package com.github.jjYBdx4IL.cms.rest.app;

import org.glassfish.hk2.api.Factory;
import org.glassfish.jersey.server.CloseableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionDataFactory implements Factory<SessionData> {

    private static final Logger LOG = LoggerFactory.getLogger(SessionDataFactory.class);

    private final HttpServletRequest request;

    @Inject
    public SessionDataFactory(HttpServletRequest request, CloseableService closeService) {
        LOG.trace("init()");
        this.request = request;
    }

    @Override
    public SessionData provide() {
        LOG.trace("provide()");
        return SessionData.getOrCreate(request);
    }

    @Override
    public void dispose(SessionData instance) {
    }

}
