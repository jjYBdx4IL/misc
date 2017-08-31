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

    public static TypedQuery<KeyValuePair> getByKey(EntityManager em, String key) {
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        final CriteriaQuery<KeyValuePair> criteriaQuery = criteriaBuilder.createQuery(KeyValuePair.class);
        final Root<KeyValuePair> userRoot = criteriaQuery.from(KeyValuePair.class);
        Predicate predicateEmail = criteriaBuilder.equal(
            userRoot.get(KeyValuePair_.key),
            key);
        criteriaQuery.where(predicateEmail);
        return em.createQuery(criteriaQuery);
    }

    public static TypedQuery<KeyValuePair> getAll(EntityManager em) {
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        final CriteriaQuery<KeyValuePair> criteriaQuery = criteriaBuilder.createQuery(KeyValuePair.class);
        criteriaQuery.from(KeyValuePair.class);
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
