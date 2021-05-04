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

import com.github.jjYBdx4IL.cms.jpa.dto.Article;
import com.github.jjYBdx4IL.cms.jpa.dto.Article_;
import com.github.jjYBdx4IL.cms.jpa.dto.ConfigKey;
import com.github.jjYBdx4IL.cms.jpa.dto.ConfigValue;
import com.github.jjYBdx4IL.cms.jpa.dto.ConfigValue_;
import com.github.jjYBdx4IL.cms.jpa.dto.MediaFile;
import com.github.jjYBdx4IL.cms.jpa.dto.MediaFile_;
import com.github.jjYBdx4IL.cms.jpa.dto.Tag;
import com.github.jjYBdx4IL.cms.jpa.dto.Tag_;
import com.github.jjYBdx4IL.cms.jpa.dto.User;
import com.github.jjYBdx4IL.cms.jpa.dto.User_;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

//CHECKSTYLE:OFF
@RequestScoped
public class QueryFactory {

    @PersistenceContext
    EntityManager em;

    public QueryFactory() {
    }

    public TypedQuery<MediaFile> getImagesMeta(Long maxId) {
        if (maxId == null) {
            maxId = Long.MAX_VALUE;
        }
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<MediaFile> cq = cb.createQuery(MediaFile.class);
        final Root<MediaFile> root = cq.from(MediaFile.class);
        cq.where(cb.and(cb.like(root.get(MediaFile_.contentType), "image/%"),
            cb.lessThanOrEqualTo(root.get(MediaFile_.id), maxId)
        ));
        cq.orderBy(cb.desc(root.get(MediaFile_.id)));
        return em.createQuery(cq);
    }

    public TypedQuery<Tag> getTag(String tagName) {
        if (tagName == null) {
            throw new IllegalArgumentException();
        }
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Tag> cq = cb.createQuery(Tag.class);
        final Root<Tag> root = cq.from(Tag.class);
        cq.where(cb.equal(root.get(Tag_.id), tagName.toLowerCase()));
        return em.createQuery(cq);
    }

    public TypedQuery<Tag> getTags(String tagPrefix) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Tag> cq = cb.createQuery(Tag.class);
        final Root<Tag> root = cq.from(Tag.class);
        if (tagPrefix != null) {
            cq.where(cb.like(root.get(Tag_.id), tagPrefix.toLowerCase() + "%"));
        }
        cq.orderBy(cb.asc(root.get(Tag_.id)));
        return em.createQuery(cq);
    }

    public Article getArticleByPathId(String pathId) {
        if (pathId == null) {
            return null;
        }

        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Article> cq = cb.createQuery(Article.class);
        final Root<Article> root = cq.from(Article.class);
        cq.where(cb.and(cb.equal(root.get(Article_.pathId), pathId), cb.equal(root.get(Article_.published), true)));

        List<Article> articles = em.createQuery(cq).getResultList();
        if (articles.isEmpty()) {
            return null;
        }
        return articles.get(0);
    }

    public TypedQuery<Article> getArticleDisplayList(String tag, String uid, boolean publishedOnly, String subdomain) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Article> cq = cb.createQuery(Article.class);
        final Root<Article> root = cq.from(Article.class);

        List<Predicate> restrictions = new ArrayList<>();
        
        if (tag != null) {
            final Subquery<Long> sq = cq.subquery(Long.class);
            final Root<Article> fromArticle = sq.from(Article.class);
            final Join<Article, Tag> articleTag = fromArticle.join(Article_.tags);
            sq.select(fromArticle.get(Article_.id))
                .where(cb.equal(articleTag.get(Tag_.id), tag.toLowerCase()));

            restrictions.add(cb.in(root.get(Article_.id)).value(sq));
        }

        if (uid != null) {
            final Join<Article, User> userRoot = root.join(Article_.owner, JoinType.LEFT);
            restrictions.add(cb.equal(userRoot.get(User_.uid), uid));
        }

        if (publishedOnly) {
            restrictions.add(cb.equal(root.get(Article_.published), true));
        }
        
        if (subdomain != null) {
            restrictions.add(cb.equal(root.get(Article_.subdomain), subdomain));
        }
        
        if (!restrictions.isEmpty() ) {
            cq.where(restrictions.toArray(new Predicate[restrictions.size()]));
        }

        cq.orderBy(cb.desc(root.get(Article_.createdAt)));
        return em.createQuery(cq);
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
}
