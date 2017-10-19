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
package com.github.jjYBdx4IL.cms.solr;

import com.github.jjYBdx4IL.cms.jpa.QueryFactory;
import com.github.jjYBdx4IL.cms.jpa.dto.Domain;
import com.github.jjYBdx4IL.cms.jpa.dto.WebPageMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

public class IndexingDbService {

    private static final Logger LOG = LoggerFactory.getLogger(IndexingDbService.class);

    public static final long DOMAIN_PROC_DELAY_SUCCESS_MS = 24 * 3600L * 1000L;
    public static final long DOMAIN_PROC_DELAY_ERROR_MS_INC = 1 * 3600L * 1000L;
    public static final long DOMAIN_PROC_DELAY_ERROR_MS_MAX = 72 * 3600L * 1000L;

    private TypedQuery<WebPageMeta> pageMetaQuery;

    @PersistenceContext
    EntityManager em;
    @Inject
    QueryFactory qf;
    @Resource
    private SessionContext sessionContext;

    public Domain getNextDomain4Processing() {
        List<Domain> domains = qf.getDomain4Processing().getResultList();
        return domains.isEmpty() ? null : domains.get(0);
    }

    @Transactional
    public void updateDomain(Domain domain, boolean success) {
        try {
            Domain domain2 = em.find(Domain.class, domain.getId());
            if (success) {
                domain2.setConsecutiveErrorCount(0);
                domain2.setScheduledUpdate(new Date(System.currentTimeMillis() + DOMAIN_PROC_DELAY_SUCCESS_MS));
            } else {
                domain2.setConsecutiveErrorCount(domain2.getConsecutiveErrorCount() + 1);
                domain2.setScheduledUpdate(new Date(System.currentTimeMillis()
                    + DOMAIN_PROC_DELAY_ERROR_MS_INC * domain2.getConsecutiveErrorCount()));
            }
            domain2.setLastProcessed(new Date());
            em.persist(domain2);
        } catch (SecurityException | IllegalStateException ex) {
            throw new RuntimeException(ex);
        }
    }

    public WebPageMeta getWebPageMeta(String url) {
        if (pageMetaQuery == null) {
            pageMetaQuery = qf.getWebPageMetaQuery();
        }
        List<WebPageMeta> results = pageMetaQuery.setParameter("url", url).getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Transactional
    public void updateWebPageMeta(WebPageMeta pageMeta) {
        try {
            if (pageMeta.getId() == null) {
                em.persist(pageMeta);
            } else {
                WebPageMeta pageMeta2 = em.find(WebPageMeta.class, pageMeta.getId());
                pageMeta.copyValuesTo(pageMeta2);
                em.persist(pageMeta2);
            }
        } catch (SecurityException | IllegalStateException ex) {
            throw new RuntimeException(ex);
        }
    }
}
