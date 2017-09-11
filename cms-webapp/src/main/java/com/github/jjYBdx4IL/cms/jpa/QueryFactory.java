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
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

/**
 *
 * @author jjYBdx4IL
 */
@RequestScoped
public class QueryFactory {

    @PersistenceContext
    EntityManager em;
    
    public QueryFactory() {
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
        cq.where(cb.equal(root.get(Article_.pathId), pathId));
        
        List<Article> articles = em.createQuery(cq).getResultList();
        if (articles.isEmpty()) {
            return null;
        }
        return articles.get(0);
    }

    public TypedQuery<Article> getArticleDisplayList(String tag, String uid) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Article> cq = cb.createQuery(Article.class);
        final Root<Article> root = cq.from(Article.class);
        
        if (tag != null) {
            final Subquery<Long> sq = cq.subquery(Long.class);
            final Root<Article> fromArticle = sq.from(Article.class);
            final Join<Article, Tag> articleTag = fromArticle.join(Article_.tags);
            sq.select(fromArticle.get(Article_.id))
                .where(cb.equal(articleTag.get(Tag_.id), tag.toLowerCase()));

            cq.where(cb.in(root.get(Article_.id)).value(sq));
        }
        
        if (uid != null) {
            final Join<Article, User> userRoot = root.join(Article_.owner, JoinType.LEFT);
            cq.where(cb.equal(userRoot.get(User_.uid), uid));
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
     * @param key the config key
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
        return results.isEmpty() ? defaultValue : results.get(0).getValue();
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
