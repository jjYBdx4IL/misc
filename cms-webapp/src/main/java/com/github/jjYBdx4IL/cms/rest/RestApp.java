package com.github.jjYBdx4IL.cms.rest;

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
            }
        });
        packages(true, getClass().getPackage().getName());
    }

}