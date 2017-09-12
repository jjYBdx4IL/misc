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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Method;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.SessionScoped;

//CHECKSTYLE:OFF
@SuppressWarnings("serial")
@SessionScoped
public class SessionData implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(SessionData.class);
    
    public static final String SESSION_ATTRNAME = "session.data";
    
    private String uid = null;
    private String googleOauth2StateSecret = null;

    public synchronized String getUid() {
        return uid;
    }

    public synchronized void setUid(String uid) {
        this.uid = uid;
    }
    
    public synchronized String getGoogleOauth2StateSecret() {
        return googleOauth2StateSecret;
    }

    public synchronized void setGoogleOauth2StateSecret(String googleOauth2StateSecret) {
        this.googleOauth2StateSecret = googleOauth2StateSecret;
    }

    public synchronized boolean isAuthenticated() {
        return getUid() != null;
    }

    public synchronized void logout() {
        setUid(null);
    }

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
    
    public synchronized boolean isDevel() {
        return Boolean.parseBoolean(System.getProperty("env.devel", "false"));
    }

    @Override
    public synchronized String toString() {
        return "SessionDataImpl [uid=" + uid + ", googleOauth2StateSecret=" + googleOauth2StateSecret + "]";
    }


}
