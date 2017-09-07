package com.github.jjYBdx4IL.cms.rest.app;

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ext.Provider;

@Provider
public class MyApplicationEventListener implements ApplicationEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(MyApplicationEventListener.class);

    private volatile int requestCnt = 0;

    @Override
    public void onEvent(ApplicationEvent event) {
        LOG.info(event.getType() + " " + event);
        switch (event.getType()) {
            case INITIALIZATION_FINISHED:
                LOG.info("Application "
                    + event.getResourceConfig().getApplicationName()
                    + " was initialized.");
                break;
            case DESTROY_FINISHED:
                LOG.info("Application "
                    + event.getResourceConfig().getApplicationName() + " destroyed.");
                break;
            default:
                break;
        }
    }

    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        requestCnt++;
        if (LOG.isTraceEnabled()) {
            LOG.trace("Request " + requestCnt + " started.");
        }
        // return the listener instance that will handle this request.
        return new MyRequestEventListener(requestCnt);
    }

}