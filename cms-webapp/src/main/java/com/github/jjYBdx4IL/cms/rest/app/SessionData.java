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

import javax.enterprise.context.SessionScoped;

//CHECKSTYLE:OFF
@SuppressWarnings("serial")
@SessionScoped
public class SessionData implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(SessionData.class);
    
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

    @Override
    public synchronized String toString() {
        return "SessionDataImpl [uid=" + uid + ", googleOauth2StateSecret=" + googleOauth2StateSecret + "]";
    }


}
