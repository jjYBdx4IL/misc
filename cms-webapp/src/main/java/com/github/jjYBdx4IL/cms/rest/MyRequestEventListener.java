package com.github.jjYBdx4IL.cms.rest;

import com.github.jjYBdx4IL.cms.jpa.tx.Tx;
import com.github.jjYBdx4IL.cms.jpa.tx.TxRo;

import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

// for this to work, we need to supply it to Jersey via the ApplicationEventListener implemented
// in MyApplicationEventListener
public class MyRequestEventListener implements RequestEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(MyRequestEventListener.class);

    private final int requestNumber;
    private final long startTime;

    public MyRequestEventListener(int requestNumber) {
        this.requestNumber = requestNumber;
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onEvent(RequestEvent event) {
        LOG.info("onEvent(): " + event.getType() + " " + event.getUriInfo().getPath() + " " + this);
        switch (event.getType()) {
            case RESOURCE_METHOD_START:
                LOG.info("Resource method "
                    + event.getUriInfo().getMatchedResourceMethod()
                        .getHttpMethod()
                    + " started for request " + requestNumber);
                beginTransaction(event);
                break;
            case ON_EXCEPTION:
            case FINISHED:
                LOG.info("Request " + requestNumber
                    + " finished. Processing time "
                    + (System.currentTimeMillis() - startTime) + " ms.");
                // this can be used for transaction handling:
                LOG.info("exception thrown: " + event.getException());
                LOG.info("" + event.getUriInfo().getMatchedResources());
                endTransaction(event);
                break;
            default:
                break;
        }
    }

    protected void beginTransaction(RequestEvent event) {
        Method method = event.getUriInfo().getMatchedResourceMethod().getInvocable().getDefinitionMethod();
        LOG.info("method: " + method);
        if (!method.isAnnotationPresent(TxRo.class) && !method.isAnnotationPresent(Tx.class)) {
            return;
        }
        for (Object object : event.getUriInfo().getMatchedResources()) {
            EntityManager em = getInjectedEntityManager(object);
            if (em == null) {
                LOG.info("skipping starting transaction on " + object);
                continue;
            }
            LOG.info("starting transaction on " + object);
            EntityTransaction transaction = em.getTransaction();
            if (method.isAnnotationPresent(TxRo.class)) {
                transaction.setRollbackOnly();
            }
            transaction.begin();
        }
    }

    protected void endTransaction(RequestEvent event) {
        for (Object object : event.getUriInfo().getMatchedResources()) {
            EntityManager em = getInjectedEntityManager(object);
            if (em == null) {
                LOG.info("skipping rollback/commit processing on " + object);
                continue;
            }
            EntityTransaction tx = em.getTransaction();
            if (tx.getRollbackOnly()) {
                LOG.info("rolling back transaction because read-only is set");
                em.getTransaction().rollback();
            } else if (event.getException() != null) {
                LOG.info("rolling back transaction because an exception was thrown");
                em.getTransaction().rollback();
            } else {
                LOG.info("committing transaction");
                em.getTransaction().commit();
            }
        }
    }

    protected EntityManager getInjectedEntityManager(Object object) {
        LOG.info("getInjectedEntityManager() " + object);
        Class<?> klazz = object.getClass();
        String fieldName = findInjectedEntityManagerFieldName(klazz);
        if (fieldName == null) {
            return null;
        }
        EntityManager em = null;
        LOG.info("retrieving entity manager from instance " + object);
        try {
            em = (EntityManager) klazz.getField(fieldName).get(object);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
        LOG.info("em = " + em);
        if (em == null) {
            throw new RuntimeException("couldn't find injected EntityManager");
        }
        return em;
    }

    protected final Map<Class<?>, String> entityManagerFieldName = new ConcurrentHashMap<>();

    protected String findInjectedEntityManagerFieldName(Class<?> klazz) {
        String fieldName = entityManagerFieldName.get(klazz);
        if (fieldName != null) {
            return "".equals(fieldName) ? null : fieldName;
        }
        for (Field field : klazz.getFields()) {
            LOG.info("field: " + field + " " + field.getType().equals(EntityManager.class));
            if (field.isAnnotationPresent(Inject.class) && field.getType().equals(EntityManager.class)) {
                if (fieldName != null) {
                    throw new RuntimeException("duplicate found");
                }
                fieldName = field.getName();
            }
        }
        entityManagerFieldName.put(klazz, fieldName == null ? "" : fieldName);
        return fieldName;
    }

}