package com.github.jjYBdx4IL.cms.rest.app;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/")
public class RestApp extends Application {

    public RestApp() {
        //setApplicationName(RestApp.class.getSimpleName());

        // preload the LogicGraphClassifier to bootstrap the database
        // serviceLocator.getService(LogicGraphClassifier.class);

        // Register Features allowing for Multipart file uploads
        // register(MultiPartFeature.class);
        // register(JacksonFeature.class);

        // Enable Tracing support.
        //property(ServerProperties.TRACING, "ALL");

//        register(new AbstractBinder() {
//            @Override
//            public void configure() {
//                bindFactory(EmfFactory.class).to(EntityManagerFactory.class).in(Singleton.class);
//                bindFactory(EmFactory.class).to(EntityManager.class)
//                    .in(RequestScoped.class);
//                bindFactory(SessionDataFactory.class).to(SessionData.class).in(RequestScoped.class);
//                bindFactory(HtmlBuilderFactory.class).to(HtmlBuilder.class).in(RequestScoped.class);
//            }
//        });
//        packages(true, getClass().getPackage().getName(), Home.class.getPackage().getName());
    }

}