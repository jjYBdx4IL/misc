package com.github.jjYBdx4IL.cms.rest.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

public class Permissions {

    private static final Logger LOG = LoggerFactory.getLogger(Permissions.class);

    protected final SessionData session;

    public Permissions(HttpServletRequest httpRequest) {
        session = SessionData.getOrCreate(httpRequest);
    }

    public boolean isAllowed(Method method) {
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

    protected boolean checkRoles(RolesAllowed rolesAllowed) {
        if (rolesAllowed == null) {
            return false;
        }
        for (String role : rolesAllowed.value()) {
            if (Role.USER.equals(role)) {
                if (session.isAuthenticated()) {
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

    protected boolean isAdmin() {
        if (!session.isAuthenticated()) {
            return false;
        }
        if (isDevel() && session.getUser().getGoogleUniqueId().equals("1")) {
            return true;
        }
        if ("101939159763736852726".equals(session.getUser().getGoogleUniqueId())) {
            return true;
        }
        return false;
    }
    
    public static boolean isDevel() {
        return false;
    }

}
