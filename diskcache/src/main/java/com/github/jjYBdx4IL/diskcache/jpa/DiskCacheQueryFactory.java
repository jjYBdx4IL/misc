/*
 * Copyright © 2016 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.diskcache.jpa;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

//CHECKSTYLE:OFF
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class DiskCacheQueryFactory {

    private final EntityManager em;

    public DiskCacheQueryFactory(EntityManager em) {
        this.em = em;
    }

    public TypedQuery<DiskCacheEntry> getByUrlQuery(String url) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<DiskCacheEntry> criteriaQuery = cb.createQuery(DiskCacheEntry.class);
        final Root<DiskCacheEntry> root = criteriaQuery.from(DiskCacheEntry.class);

        Predicate p1 = cb.equal(root.get(DiskCacheEntry_.url), url);
        Predicate p2 = cb.ge(root.get(DiskCacheEntry_.size), 0);
        criteriaQuery.where(cb.and(p1, p2));
        criteriaQuery.orderBy(cb.desc(root.get(DiskCacheEntry_.createdAt)));
        return em.createQuery(criteriaQuery);
    }

}
