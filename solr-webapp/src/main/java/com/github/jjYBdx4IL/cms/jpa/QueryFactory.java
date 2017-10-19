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

import com.github.jjYBdx4IL.cms.Env;
import com.github.jjYBdx4IL.cms.jpa.dto.ConfigKey;
import com.github.jjYBdx4IL.cms.jpa.dto.ConfigValue;
import com.github.jjYBdx4IL.cms.jpa.dto.ConfigValue_;
import com.github.jjYBdx4IL.cms.jpa.dto.Domain;
import com.github.jjYBdx4IL.cms.jpa.dto.Domain_;
import com.github.jjYBdx4IL.cms.jpa.dto.User;
import com.github.jjYBdx4IL.cms.jpa.dto.User_;
import com.github.jjYBdx4IL.cms.jpa.dto.WebPageMeta;
import com.github.jjYBdx4IL.cms.jpa.dto.WebPageMeta_;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

//CHECKSTYLE:OFF
public class QueryFactory {

    public static final long SPAM_DELAY_MS = Env.isDevel() ? 0L : 900L * 1000L;
    
    @PersistenceContext
    EntityManager em;

    public QueryFactory() {
    }

    public User getUserByUid(String uid) {
        if (uid == null) {
            return null;
        }
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        final CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        final Root<User> userRoot = criteriaQuery.from(User.class);
        Predicate predicateUser = criteriaBuilder.equal(
            userRoot.get(User_.uid),
            uid);
        criteriaQuery.where(predicateUser);
        List<User> users = em.createQuery(criteriaQuery).getResultList();
        if (users.size() > 1) {
            throw new IllegalStateException();
        }
        return users.isEmpty() ? null : users.get(0);
    }

    /**
     * Throws {@link NoResultException} if the config value is not set.
     * 
     * @param key
     *            the config key
     * @return the config value
     */
    public String getConfigValue(ConfigKey key) {
        return getConfigValueQuery(key).getSingleResult().getValue();
    }

    private TypedQuery<ConfigValue> getConfigValueQuery(ConfigKey key) {
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        final CriteriaQuery<ConfigValue> criteriaQuery = criteriaBuilder.createQuery(ConfigValue.class);
        final Root<ConfigValue> userRoot = criteriaQuery.from(ConfigValue.class);
        Predicate predicateKey = criteriaBuilder.equal(
            userRoot.get(ConfigValue_.key),
            key);
        criteriaQuery.where(predicateKey);
        return em.createQuery(criteriaQuery);
    }

    public String getConfigValue(ConfigKey key, String defaultValue) {
        List<ConfigValue> results = getConfigValueQuery(key).getResultList();
        String res = defaultValue;
        if (!results.isEmpty() && results.get(0).getValue() != null) {
            res = results.get(0).getValue();
        }
        return res;
    }

    public TypedQuery<ConfigValue> getAllConfigValues() {
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        final CriteriaQuery<ConfigValue> criteriaQuery = criteriaBuilder.createQuery(ConfigValue.class);
        criteriaQuery.from(ConfigValue.class);
        return em.createQuery(criteriaQuery);
    }

    public Map<ConfigKey, String> getAllConfigValuesAsMap() {
        final Map<ConfigKey, String> result = new HashMap<>();
        for (ConfigValue cv : getAllConfigValues().getResultList()) {
            result.put(cv.getKey(), cv.getValue());
        }
        return result;
    }

    public Domain getDomainByUrl(String url) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Domain> cq = cb.createQuery(Domain.class);
        final Root<Domain> root = cq.from(Domain.class);
        Predicate predicateKey = cb.equal(root.get(Domain_.url), url);
        cq.where(predicateKey);
        List<Domain> results = em.createQuery(cq).getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    public TypedQuery<Domain> getDomain4Processing() {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Domain> cq = cb.createQuery(Domain.class);
        final Root<Domain> root = cq.from(Domain.class);
        cq.where(cb.and(
            cb.isNotNull(root.get(Domain_.scheduledUpdate)),
            cb.lessThan(root.get(Domain_.scheduledUpdate), new Date()),
            cb.lessThan(root.get(Domain_.lastProcessed), new Date(System.currentTimeMillis() - SPAM_DELAY_MS))));
        cq.orderBy(cb.asc(root.get(Domain_.scheduledUpdate)));
        TypedQuery<Domain> tq = em.createQuery(cq);
        tq.setMaxResults(1);
        return tq;
    }

    public TypedQuery<WebPageMeta> getWebPageMetaQuery() {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<WebPageMeta> cq = cb.createQuery(WebPageMeta.class);
        final Root<WebPageMeta> root = cq.from(WebPageMeta.class);
        Predicate predicateKey = cb.equal(
            root.get(WebPageMeta_.url),
            cb.parameter(WebPageMeta_.url.getJavaType(), "url"));
        cq.where(predicateKey);
        return em.createQuery(cq);
    }

}
