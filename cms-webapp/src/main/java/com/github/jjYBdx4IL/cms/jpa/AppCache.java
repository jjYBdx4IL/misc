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
package com.github.jjYBdx4IL.cms.jpa;

import com.github.jjYBdx4IL.cms.jpa.dto.ConfigKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

//CHECKSTYLE:OFF
@ApplicationScoped
public class AppCache {

    private static final Logger LOG = LoggerFactory.getLogger(AppCache.class);
    
    public static final String DEVEL_ADMIN = "devel-1";
    public static final String PROPNAME_CMS_DEVEL = "cms.devel";
    
    private Map<ConfigKey, String> values = null;
    private Map<String, String> admins = new ConcurrentHashMap<>();
    
    @Inject
    QueryFactory qf;
    
    public AppCache() {
    }
    
    @PostConstruct
    private void load() {
        values = Collections.synchronizedMap(qf.getAllConfigValuesAsMap());
        if (values.containsKey(ConfigKey.ADMINS) && values.get(ConfigKey.ADMINS) != null) {
            for (String admin : values.get(ConfigKey.ADMINS).split("[, \\t]+")) {
                if (!admin.isEmpty()) {
                    admins.put(admin, admin);
                }
            }
        }
        if (isDevel()) {
            LOG.warn("detected development environment, adding default admin: " + DEVEL_ADMIN);
            admins.put(DEVEL_ADMIN, DEVEL_ADMIN);
        }
    }
    
    public String get(ConfigKey key) {
        String value = values.get(key);
        return value == null ? "" : value;
    }
    
    public String getNonEmpty(ConfigKey key) {
        String value = values.get(key);
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException();
        }
        return value;
    }
    
    public boolean isAdmin(String uid) {
        return admins.containsKey(uid);
    }
    
    public boolean isDevel() {
        return Boolean.getBoolean(PROPNAME_CMS_DEVEL);
    }
    
}
