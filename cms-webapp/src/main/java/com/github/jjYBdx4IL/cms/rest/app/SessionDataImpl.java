package com.github.jjYBdx4IL.cms.rest.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Method;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.SessionScoped;

@SuppressWarnings("serial")
@SessionScoped
public class SessionDataImpl implements Serializable, SessionData {

    private static final Logger LOG = LoggerFactory.getLogger(SessionDataImpl.class);
    
    private String uid = null;
    private String googleOauth2StateSecret = null;

    @Override
    public synchronized String getUid() {
        return uid;
    }

    @Override
    public synchronized void setUid(String uid) {
        this.uid = uid;
    }
    
    @Override
    public synchronized String getGoogleOauth2StateSecret() {
        return googleOauth2StateSecret;
    }

    @Override
    public synchronized void setGoogleOauth2StateSecret(String googleOauth2StateSecret) {
        this.googleOauth2StateSecret = googleOauth2StateSecret;
    }

    @Override
    public synchronized boolean isAuthenticated() {
        return getUid() != null;
    }

    @Override
    public synchronized void logout() {
        setUid(null);
    }

    @Override
    public synchronized boolean isAllowed(Method method) {
        if (method.isAnnotationPresent(DenyAll.class)) {
            return isDevel();
        }
        if (method.isAnnotationPresent(PermitAll.class)) {
            return true;
        }
        if (checkRoles(method.getAnnotation(RolesAllowed.class))) {
            return true;
        }

        Class<?> klazz = method.getDeclaringClass();

        if (klazz.isAnnotationPresent(DenyAll.class)) {
            return isDevel();
        }
        if (klazz.isAnnotationPresent(PermitAll.class)) {
            return true;
        }
        if (checkRoles(klazz.getAnnotation(RolesAllowed.class))) {
            return true;
        }
        
        return false;
    }

    protected synchronized boolean checkRoles(RolesAllowed rolesAllowed) {
        if (rolesAllowed == null) {
            return false;
        }
        for (String role : rolesAllowed.value()) {
            if (Role.USER.equals(role)) {
                if (isAuthenticated()) {
                    return true;
                }
            } else if (Role.ADMIN.equals(role)) {
                if (isAdmin()) {
                    return true;
                }
            }
        }
        return false;
    }

    protected synchronized boolean isAdmin() {
        LOG.info("isAdmin " + this);
        if (!isAuthenticated()) {
            return false;
        }
        if (isDevel() && getUid().equals("devel-1")) {
            return true;
        }
        if (System.getProperty("cms.webapp.admin.uid", "none").equals(getUid())) {
            return true;
        }
        return false;
    }
    
    @Override
    public synchronized boolean isDevel() {
        return Boolean.parseBoolean(System.getProperty("env.devel", "false"));
    }

    @Override
    public synchronized String toString() {
        return "SessionDataImpl [uid=" + uid + ", googleOauth2StateSecret=" + googleOauth2StateSecret + "]";
    }


}
