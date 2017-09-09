package com.github.jjYBdx4IL.cms.rest.app;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.ws.rs.core.Response;

public class TxMethodInterceptor implements MethodInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(TxMethodInterceptor.class);

    private final boolean rollbackOnly;
    private final EntityManagerFactory emf;
    
    public TxMethodInterceptor(EntityManagerFactory emf, boolean rollbackOnly) {
        this.rollbackOnly = rollbackOnly;
        this.emf = emf;
    }
    
    @Override
    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
        LOG.info("" + methodInvocation);

        EntityManager em = null; // get entity manager
        try {
            // methodInvocation.getThis() to inject entity manager

            // start transaction
            // EntityTransaction tx = em.getTransaction();
            // tx.begin();

            // force read-only if annotation is found
            // if
            // (methodInvocation.getMethod().isAnnotationPresent(ReadOnly.class))
            // {
            // tx.setRollbackOnly();
            // }

            // returning from this block without committing will force a
            // rollback

            Object ret = null;
            try {
                ret = methodInvocation.proceed();
            } catch (Throwable t) {
                throw t;
            }
            if (ret == null) {
                return null;
            }
            if (ret instanceof Response) {
                Response r = (Response) ret;
                switch (r.getStatusInfo().getFamily()) {
                    case SUCCESSFUL:
                    case INFORMATIONAL:
                        break;
                    default:
                        return ret;
                }
            }
            // commit
            return ret;
        } finally {
            // closeEm(em);
        }
    }

    protected void closeEm(EntityManager em) {
        try {
            EntityTransaction tx = em.getTransaction();
            if (tx.isActive()) {
                tx.rollback();
            }
        } finally {
            em.close();
        }
    }
}