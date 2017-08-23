package com.github.jjYBdx4IL.cms;

import org.eclipse.jetty.plus.jndi.Resource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.h2.Driver;
import org.hibernate.boot.SchemaAutoTooling;
import org.hibernate.cfg.AvailableSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Start EMF instance on server side to avoid reloading it with webapp reloads.
 * 
 * @author jjYBdx4IL
 *
 */
public class ServerEmfRunner extends AbstractLifeCycle {

    private static final Logger LOG = LoggerFactory.getLogger(ServerEmfRunner.class);

    private EntityManagerFactory emf = null;
    private final Server server;
    private final String jdbcUrl;

    public ServerEmfRunner(Server server, String jdbcUrl) {
        this.server = server;
        this.jdbcUrl = jdbcUrl;
    }

    @Override
    protected void doStart() throws Exception {
        getEmfInstance();
    }

    @Override
    protected void doStop() throws Exception {
        if (emf != null) {
            LOG.info("closing " + emf);
            emf.close();
            emf = null;
        }
    }

    protected void getEmfInstance() throws NamingException {
        Map<String, String> props = new HashMap<>();
        props.put(AvailableSettings.HBM2DDL_AUTO, SchemaAutoTooling.UPDATE.name().toLowerCase(Locale.ROOT));
        props.put(AvailableSettings.SHOW_SQL, "true");
        props.put(AvailableSettings.JPA_JDBC_DRIVER, Driver.class.getName());
        props.put(AvailableSettings.JPA_JDBC_URL, jdbcUrl);
        emf = Persistence.createEntityManagerFactory("default", props);

        new Resource(server, "jpa/emf", emf);

        LOG.info("created " + emf);
    }

}
