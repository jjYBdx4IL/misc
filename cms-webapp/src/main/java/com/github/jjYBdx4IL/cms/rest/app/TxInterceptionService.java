package com.github.jjYBdx4IL.cms.rest.app;

import com.github.jjYBdx4IL.cms.jpa.tx.Tx;
import com.github.jjYBdx4IL.cms.jpa.tx.TxRo;
import com.github.jjYBdx4IL.cms.rest.Home;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InterceptionService;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.Context;

@Service
public class TxInterceptionService implements InterceptionService {

    private static final Logger LOG = LoggerFactory.getLogger(TxInterceptionService.class);

    private final List<MethodInterceptor> txInterceptors;
    private final List<MethodInterceptor> txRoInterceptors;

    @Context
    EntityManager em;
    
    @Inject
    public TxInterceptionService(EntityManagerFactory emf) {
        LOG.info("init() " + emf);
        txInterceptors = Collections.<MethodInterceptor>singletonList(new TxMethodInterceptor(emf, false));
        txRoInterceptors = Collections.<MethodInterceptor>singletonList(new TxMethodInterceptor(emf, true));
    }

    @Override
    public Filter getDescriptorFilter() {
        return new Filter() {
            @Override
            public boolean matches(final Descriptor d) {
                final String clazz = d.getImplementation();
                return clazz.startsWith(Home.class.getPackage().getName());
            }
        };
    }

    @Override
    public List<MethodInterceptor> getMethodInterceptors(final Method method) {
//        if (LOG.isTraceEnabled()) {
            LOG.info("getMethodInterceptors(): " + em);
//        }
        if (method.isAnnotationPresent(TxRo.class)) {
            return txRoInterceptors;
        } else if (method.isAnnotationPresent(Tx.class)) {
            return txInterceptors;
        }
        return null;
    }

    @Override
    public List<ConstructorInterceptor> getConstructorInterceptors(final Constructor<?> constructor) {
        return null;
    }
}
