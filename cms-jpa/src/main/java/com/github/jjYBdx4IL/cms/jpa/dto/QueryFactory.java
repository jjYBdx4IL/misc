package com.github.jjYBdx4IL.cms.jpa.dto;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author jjYBdx4IL
 */
public class QueryFactory {

    private QueryFactory() {
    }

    public static TypedQuery<Article> getArticleDisplayList(EntityManager em, String tag) {
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        final CriteriaQuery<Article> criteriaQuery = criteriaBuilder.createQuery(Article.class);
        final Root<Article> root = criteriaQuery.from(Article.class);
        if (tag != null) {
            Predicate predicate = criteriaBuilder.equal(
                root.get(Article_.tags),
                tag);
            criteriaQuery.where(predicate);
        }
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get(Article_.createdAt)));
        return em.createQuery(criteriaQuery);
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
