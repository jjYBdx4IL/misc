package com.github.jjYBdx4IL.cms.rest.app;

import com.github.jjYBdx4IL.cms.rest.Home;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class RestApp extends ResourceConfig {

    public RestApp() {
        register(new AbstractBinder() {
            @Override
            public void configure() {
                bindFactory(EmfFactory.class).to(EntityManagerFactory.class).in(Singleton.class);
                bindFactory(EmFactory.class).to(EntityManager.class).in(RequestScoped.class);
                bindFactory(SessionDataFactory.class).to(SessionData.class).in(RequestScoped.class);
                bindFactory(HtmlBuilderFactory.class).to(HtmlBuilder.class).in(RequestScoped.class);
            }
        });
        packages(true, getClass().getPackage().getName());
        packages(true, Home.class.getPackage().getName());
    }

}