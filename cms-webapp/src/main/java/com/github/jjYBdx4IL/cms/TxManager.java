/*
 * Copyright © 2017 jjYBdx4IL (https://github.com/jjYBdx4IL)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jjYBdx4IL.cms;

import com.github.jjYBdx4IL.cms.jpa.tx.TxEntityManager;
import com.github.jjYBdx4IL.cms.rest.Hello;

import org.eclipse.jetty.webapp.WebAppContext;
import org.h2.Driver;
import org.hibernate.boot.SchemaAutoTooling;
import org.hibernate.cfg.AvailableSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Transaction manager.
 * 
 * @author jjYBdx4IL
 */
public class TxManager implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(TxManager.class);

    protected static TxManager txManagerSingleton = null;
    protected static final String PU_NAME = "default";

    /**
     * Get a global transaction manager singleton. Because this is using a
     * static field, it is actually a singleton per class loader.
     * 
     * @return the global transaction manager singleton
     */
    public static synchronized TxManager getSingleton() {
        if (txManagerSingleton == null) {
//            try {
                txManagerSingleton = new TxManager();
//            } catch (NamingException ex) {
//                throw new RuntimeException(ex);
//            }
        }
        return txManagerSingleton;
    }

    protected EntityManagerFactory entityManagerFactory = null;
    // keep track of who is using the EMF:
    protected Set<Object> emfUsedByRefs = new HashSet<>();
    protected ThreadLocal<EntityManager> entityManagers = new ThreadLocal<>();

    /**
     * Constructor for the transaction manager.
     * 
     * @throws NamingException
     *             if the "java:comp/env/jdbc/url" JNDI resource is not found.
     */
    public TxManager() {
    }

    protected Map<String, String> getProps() {
        Map<String, String> props = new HashMap<>();
        String jdbcUrl;
        try {
            InitialContext ic = new InitialContext();
            jdbcUrl = (String) ic.lookup("java:comp/env/jdbc/url");
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }

        LOG.info("new " + TxManager.class.getName() + ", db url: " + jdbcUrl);
        props.put(AvailableSettings.HBM2DDL_AUTO, SchemaAutoTooling.UPDATE.name().toLowerCase(Locale.ROOT));
        props.put(AvailableSettings.SHOW_SQL, "true");
        props.put(AvailableSettings.JPA_JDBC_DRIVER, Driver.class.getName());
        props.put(AvailableSettings.JPA_JDBC_URL, jdbcUrl);
        return props;
    }

    /**
     * Get the entity manager factory.
     * 
     * @param usedBy
     *            used to keep track who is using the factory
     * @return the {@link EntityManagerFactory} singleton
     * @throws NamingException 
     */
    public synchronized EntityManagerFactory getEntityManagerFactory(Object usedBy) {
        LOG.info("getEntityManagerFactory() for " + usedBy);
        if (entityManagerFactory == null) {
            LOG.info("creating EntityManagerFactory singleton for persistence unit " + PU_NAME);
            entityManagerFactory = Persistence.createEntityManagerFactory(PU_NAME, getProps());
        }
        if (usedBy != null) {
            emfUsedByRefs.add(usedBy);
        }
        LOG.info("EntityManagerFactory for persistence unit " + PU_NAME
            + " now in use by " + emfUsedByRefs.size() + " objects");
        return entityManagerFactory;
    }

    /**
     * Release the entity manager factory.
     * 
     * @param usedBy
     *            used to keep track who is using the factory
     */
    public synchronized void releaseEntityManagerFactory(Object usedBy) {
        LOG.info("releaseEntityManagerFactory() for " + usedBy);
        emfUsedByRefs.remove(usedBy);
        LOG.info("EntityManagerFactory for persistence unit " + PU_NAME
            + " now in use by " + emfUsedByRefs.size() + " objects");
        if (emfUsedByRefs.isEmpty() && entityManagerFactory != null) {
            LOG.info("closing EntityManagerFactory singleton for persistence unit "
                + PU_NAME + " because it is not in use any more");
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }

    /**
     * Get an entity manager/transaction for the current thread.
     * 
     * @param usedBy
     *            used to keep track of who is using the entity manager factory.
     * @return the entity manager/transaction
     */
    public EntityManager getNewEntityManager(Object usedBy) {
        if (entityManagers.get() != null && entityManagers.get().isOpen()) {
            LOG.error("replacing unclosed entity manager for " + usedBy);
            entityManagers.get().close();
        }
        entityManagers.set(getSingleton().getEntityManagerFactory(null).createEntityManager());
        return entityManagers.get();
    }

    /**
     * Get an existing entity manager/transaction. Returns null if there is none
     * associated to the current thread.
     * 
     * @param usedBy
     *            used to keep track of who is using the entity manager factory.
     * @return the entity manager/transaction
     */
    public EntityManager getExistingEntityManager(Object usedBy) {
        return entityManagers.get();
    }

    /**
     * Release the entity manager assocatied with the current thread.
     */
    public void releaseEntityManager() {
        EntityManager em = entityManagers.get();
        if (em == null) {
            LOG.error("trying to release unset entity manager");
            return;
        }
        try {
            if (em.isOpen()) {
                try {
                    EntityTransaction transaction = em.getTransaction();
                    if (transaction.isActive()) {
                        LOG.error("open transaction found, rolling back");
                        transaction.rollback();
                    }
                } finally {
                    em.close();
                }
            }
        } finally {
            entityManagers.set(null);
        }
    }

    // EMF life cycle control
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOG.info("contextInitialized()");
        TxManager.getSingleton().getEntityManagerFactory(sce.getServletContext());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOG.info("contextDestroyed()");
        TxManager.getSingleton().releaseEntityManagerFactory(sce.getServletContext());
    }

    public int injectEntityManager(Object object) {
        LOG.info("injectEntityManager() " + object);
        EntityManager em = entityManagers.get();
        if (em == null) {
            throw new RuntimeException("trying to inject unset entity manager into " + object);
        }
        int count = 0;
        for (Field field: object.getClass().getFields()) {
            LOG.info("field: " + field);
            if (field.isAnnotationPresent(TxEntityManager.class)) {
                LOG.info("injecting entity manager into " + field + " of instance " + object);
                try {
                    field.set(object, em);
                    count++;
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
//        if (object instanceof Hello) {
//            ((Hello)object).em = em;
//            count++;
//        }
        return count;
    }

}
