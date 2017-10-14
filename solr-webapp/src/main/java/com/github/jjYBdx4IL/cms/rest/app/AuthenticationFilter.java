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
package com.github.jjYBdx4IL.cms.rest.app;

import com.github.jjYBdx4IL.cms.jpa.AppCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Context
    private ResourceInfo resourceInfo;
    @Inject
    private SessionData session;
    @Inject
    private AppCache appCache;

    private static final Response ACCESS_FORBIDDEN = Response.status(Response.Status.FORBIDDEN).build();

    public AuthenticationFilter() {
        LOG.trace("init()");
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("filter " + requestContext);
        }
        if (!isAllowed(resourceInfo.getResourceMethod())) {
            requestContext.abortWith(ACCESS_FORBIDDEN);
            return;
        }            
    }
    
    private boolean isAllowed(Method method) {
        if (method.isAnnotationPresent(DenyAll.class)) {
            return appCache.isDevel();
        }
        if (method.isAnnotationPresent(PermitAll.class)) {
            return true;
        }
        if (checkRoles(method.getAnnotation(RolesAllowed.class))) {
            return true;
        }

        Class<?> klazz = method.getDeclaringClass();

        if (klazz.isAnnotationPresent(DenyAll.class)) {
            return appCache.isDevel();
        }
        if (klazz.isAnnotationPresent(PermitAll.class)) {
            return true;
        }
        if (checkRoles(klazz.getAnnotation(RolesAllowed.class))) {
            return true;
        }
        
        return false;
    }

    private boolean checkRoles(RolesAllowed rolesAllowed) {
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

    private boolean isAdmin() {
        if (!session.isAuthenticated()) {
            return false;
        }
        if (appCache.isAdmin(session.getUid())) {
            return true;
        }
        return false;
    }
    
    
}

