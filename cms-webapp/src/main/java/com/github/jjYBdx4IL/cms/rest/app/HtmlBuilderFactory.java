package com.github.jjYBdx4IL.cms.rest.app;

import org.glassfish.hk2.api.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

public class HtmlBuilderFactory implements Factory<HtmlBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(HtmlBuilderFactory.class);

    private final SessionData session;
    private final UriInfo uriInfo;
    

    @Inject
    public HtmlBuilderFactory(SessionData session, UriInfo uriInfo) {
        LOG.trace("init()");
        this.session = session;
        this.uriInfo = uriInfo;
    }

    @Override
    public HtmlBuilder provide() {
        LOG.trace("provide()");
        return HtmlBuilder.createDefault().setSession(session).setUriInfo(uriInfo);
    }

    @Override
    public void dispose(HtmlBuilder em) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("dispose() " + em);
        }
    }

}
