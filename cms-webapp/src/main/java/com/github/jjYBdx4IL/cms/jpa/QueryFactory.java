package com.github.jjYBdx4IL.cms.jpa;

import com.github.jjYBdx4IL.cms.jpa.dto.Article;
import com.github.jjYBdx4IL.cms.jpa.dto.Article_;
import com.github.jjYBdx4IL.cms.jpa.dto.ConfigKey;
import com.github.jjYBdx4IL.cms.jpa.dto.ConfigValue;
import com.github.jjYBdx4IL.cms.jpa.dto.ConfigValue_;
import com.github.jjYBdx4IL.cms.jpa.dto.Tag;
import com.github.jjYBdx4IL.cms.jpa.dto.Tag_;
import com.github.jjYBdx4IL.cms.jpa.dto.User;
import com.github.jjYBdx4IL.cms.jpa.dto.User_;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author jjYBdx4IL
 */
public class QueryFactory {

    private QueryFactory() {
    }

    public static TypedQuery<Tag> getTag(EntityManager em, String tagName) {
        if (tagName == null) {
            throw new IllegalArgumentException();
        }
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Tag> cq = cb.createQuery(Tag.class);
        final Root<Tag> root = cq.from(Tag.class);
        cq.where(cb.equal(root.get(Tag_.id), tagName.toLowerCase()));
        return em.createQuery(cq);
    }

    public static TypedQuery<Tag> getTags(EntityManager em, String tagPrefix) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Tag> cq = cb.createQuery(Tag.class);
        final Root<Tag> root = cq.from(Tag.class);
        if (tagPrefix != null) {
            cq.where(cb.like(root.get(Tag_.id), tagPrefix.toLowerCase() + "%"));
        }
        cq.orderBy(cb.asc(root.get(Tag_.id)));
        return em.createQuery(cq);
    }

    public static TypedQuery<Article> getArticleDisplayList(EntityManager em, String tag, User owner) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Article> cq = cb.createQuery(Article.class);
        final Root<Article> root = cq.from(Article.class);
        if (tag != null) {
            final Join<Article, Tag> tagRoot = root.join(Article_.tags, JoinType.INNER);
            cq.where(cb.equal(cb.lower(tagRoot.get(Tag_.name)), tag.toLowerCase()));
        }
        if (owner != null) {
            cq.where(cb.equal(root.get(Article_.owner), owner.getId()));
        }
        cq.orderBy(cb.desc(root.get(Article_.createdAt)));
        return em.createQuery(cq);
    }

    public static TypedQuery<User> getUserByGoogleUid(EntityManager em, String uid) {
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        final CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        final Root<User> userRoot = criteriaQuery.from(User.class);
        Predicate predicateUser = criteriaBuilder.equal(
            userRoot.get(User_.googleUniqueId),
            uid);
        criteriaQuery.where(predicateUser);
        return em.createQuery(criteriaQuery);
    }

    public static String getConfigValue(EntityManager em, ConfigKey key) {
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        final CriteriaQuery<ConfigValue> criteriaQuery = criteriaBuilder.createQuery(ConfigValue.class);
        final Root<ConfigValue> userRoot = criteriaQuery.from(ConfigValue.class);
        Predicate predicateKey = criteriaBuilder.equal(
            userRoot.get(ConfigValue_.key),
            key);
        criteriaQuery.where(predicateKey);
        return em.createQuery(criteriaQuery).getSingleResult().getValue();
    }

    public static TypedQuery<ConfigValue> getAllConfigValues(EntityManager em) {
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        final CriteriaQuery<ConfigValue> criteriaQuery = criteriaBuilder.createQuery(ConfigValue.class);
        criteriaQuery.from(ConfigValue.class);
        return em.createQuery(criteriaQuery);
    }

    public static Map<ConfigKey, String> getAllConfigValuesAsMap(EntityManager em) {
        Map<ConfigKey, String> result = new HashMap<>();
        for (ConfigValue cv : getAllConfigValues(em).getResultList()) {
            result.put(cv.getKey(), cv.getValue());
        }
        return result;
    }
}