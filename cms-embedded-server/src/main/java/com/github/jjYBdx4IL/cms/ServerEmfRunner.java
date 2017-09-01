package com.github.jjYBdx4IL.cms;

import com.github.jjYBdx4IL.cms.jpa.dto.ConfigKey;
import com.github.jjYBdx4IL.cms.jpa.dto.ConfigValue;
import com.github.jjYBdx4IL.utils.env.Env;

import org.eclipse.jetty.plus.jndi.Resource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.component.LifeCycle.Listener;
import org.h2.Driver;
import org.hibernate.boot.SchemaAutoTooling;
import org.hibernate.cfg.AvailableSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Start EMF instance on server side to avoid reloading it with webapp reloads.
 * 
 * @author jjYBdx4IL
 *
 */
public class ServerEmfRunner implements Listener {

    private static final Logger LOG = LoggerFactory.getLogger(ServerEmfRunner.class);

    private EntityManagerFactory emf = null;
    private final Server server;
    private final String jdbcUrl;

    public ServerEmfRunner(Server server, String jdbcUrl) {
        this.server = server;
        this.jdbcUrl = jdbcUrl;
    }

    protected void doStart() throws Exception {
        getEmfInstance();

        // do some db initialization when running in development environment:
        if (System.getProperty("basedir") != null) {
            doDevelInit();
        }
    }

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

        LOG.info("binding jpa/emf for " + server + " to " + emf);
        new Resource(server, "jpa/emf", emf);

        LOG.info("created " + emf);
    }

    private void doDevelInit() throws FileNotFoundException, IOException {
        LOG.info("doDevelInit()");
        File cfgFile = new File(Env.getConfigDir(ServerEmfRunner.class).getParentFile(),
            "com.google.api.client.GoogleOauth2ExampleTest" + File.separator +
                "googleOauth2Client.properties");
        EntityManager em = emf.createEntityManager();
        try {
            Properties p = new Properties();
            try (InputStream is = new FileInputStream(cfgFile)) {
                p.load(is);
            }
            em.getTransaction().begin();
            updateConfigValue(em, ConfigKey.GOOGLE_OAUTH2_CLIENT_ID, p.getProperty("clientId"));
            updateConfigValue(em, ConfigKey.GOOGLE_OAUTH2_CLIENT_SECRET, p.getProperty("clientSecret"));
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    private void updateConfigValue(EntityManager em, ConfigKey key, String value) {
        ConfigValue configValue = em.find(ConfigValue.class, key);
        if (configValue == null) {
            configValue = new ConfigValue(key, value);
        } else {
            configValue.setValue(value);
        }
        em.persist(configValue);
    }

    @Override
    public void lifeCycleStarting(LifeCycle event) {
        try {
            doStart();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void lifeCycleStarted(LifeCycle event) {
    }

    @Override
    public void lifeCycleFailure(LifeCycle event, Throwable cause) {
    }

    @Override
    public void lifeCycleStopping(LifeCycle event) {
    }

    @Override
    public void lifeCycleStopped(LifeCycle event) {
        try {
            doStop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
