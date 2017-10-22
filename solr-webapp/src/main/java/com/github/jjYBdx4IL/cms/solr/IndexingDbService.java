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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.github.jjYBdx4IL.cms.jpa.QueryFactory;
import com.github.jjYBdx4IL.cms.jpa.dto.WebPageMeta;
import com.github.jjYBdx4IL.utils.text.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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
    public static final int MAX_ERRORS = 10;
    public static final long DEF_WEBPAGE_REINDEX_IVAL_HOURS = 30 * 24;
    public static final int MAX_RECHECK_DAYS = 90;
    public static final int ERR_WEBPAGE_RETRY_BACKOFF_IVAL_MINUTES = 1 * 24 * 60;
    public static final int BLOCK_RECHECK_DAYS = 300;
    public static final int LOCK_RESCHEDULE_MINUTES = 30;
    public static final int MAX_ADD_URLS_PER_PAGE = 100;
    public static final int FLUSH_AFTER_N_ADDS = 20;

    public static final List<String> TYPES_WHITELIST = new ArrayList<>();
    static {
        TYPES_WHITELIST.add("text/html");
        TYPES_WHITELIST.add("application/x-php");
        TYPES_WHITELIST.add("application/octet-stream"); // default if URL has no file suffix
    }
    
    @PersistenceContext
    EntityManager em;
    @Inject
    QueryFactory qf;
    @Resource
    private SessionContext sessionContext;

    private final Random r = new Random();

    public WebPageMeta getNextUrl4Processing() {
        return qf.getUrl4Processing();
    }

    public WebPageMeta getWebPageMeta(String url) {
        TypedQuery<WebPageMeta> pageMetaQuery = qf.getWebPageMetaQuery();
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

    @Transactional
    public void reschedule(WebPageMeta meta, int duration, TimeUnit timeUnit) {
        checkNotNull(meta);
        checkNotNull(timeUnit);
        checkState(duration >= 0);
        duration = duration / 4 + r.nextInt(duration - duration / 4);
        WebPageMeta pageMeta = em.find(WebPageMeta.class, meta.getId());
        pageMeta.setScheduledUpdate(new Date(System.currentTimeMillis() + timeUnit.toMillis(duration)));
        pageMeta.setLastProcessed(new Date());
        em.persist(pageMeta);
    }

    @Transactional
    public void rescheduleLocked(WebPageMeta meta) {
        checkNotNull(meta);
        int duration = LOCK_RESCHEDULE_MINUTES;
        duration = duration / 4 + r.nextInt(duration - duration / 4);
        WebPageMeta pageMeta = em.find(WebPageMeta.class, meta.getId());
        pageMeta.setScheduledUpdate(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(duration)));
        pageMeta.setLastProcessed(new Date());
        em.persist(pageMeta);
    }

    @Transactional
    public void remove(WebPageMeta meta) {
        checkNotNull(meta);
        throw new UnsupportedOperationException(); // need to clean up solr too
        // WebPageMeta pageMeta = em.find(WebPageMeta.class, meta.getId());
        // em.remove(pageMeta);
    }

    @Transactional
    public void updated(WebPageMeta meta) {
        checkNotNull(meta);
        WebPageMeta pageMeta = em.find(WebPageMeta.class, meta.getId());
        pageMeta.setConsecutiveErrorCount(0);
        pageMeta.setEtag(meta.getEtag());
        pageMeta.setLastModified(meta.getLastModified());
        pageMeta.setExpires(meta.getExpires());
        pageMeta.setLastProcessed(new Date());

        fixExpires(pageMeta, meta.getScheduledUpdate());
        updateScheduledUpdate(pageMeta);

        em.persist(pageMeta);
    }

    // remove db and solr entry at some point?
    @Transactional
    public void error(WebPageMeta meta) {
        checkNotNull(meta);
        WebPageMeta pageMeta = em.find(WebPageMeta.class, meta.getId());

        int errors = Math.min(MAX_ERRORS, pageMeta.getConsecutiveErrorCount() + 1);
        if (errors == MAX_ERRORS) {
            LOG.info("maximum number of errors reached, removing: " + meta.getUrl());
            em.remove(pageMeta);
            return;
        }
        pageMeta.setConsecutiveErrorCount(errors);

        int duration = ERR_WEBPAGE_RETRY_BACKOFF_IVAL_MINUTES * errors;
        duration = duration / 2 + r.nextInt(duration - duration / 2);
        pageMeta.setScheduledUpdate(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(duration)));
        pageMeta.setLastProcessed(new Date());

        em.persist(pageMeta);
    }

    @Transactional
    public void notModified(WebPageMeta meta) {
        checkNotNull(meta);
        WebPageMeta pageMeta = em.find(WebPageMeta.class, meta.getId());
        pageMeta.setConsecutiveErrorCount(0);
        pageMeta.setEtag(meta.getEtag());
        pageMeta.setLastModified(meta.getLastModified());
        pageMeta.setExpires(meta.getExpires());
        pageMeta.setLastProcessed(new Date());

        fixExpires(pageMeta, meta.getScheduledUpdate());
        updateScheduledUpdate(pageMeta);

        em.persist(pageMeta);
    }

    // fix expires first, delay it according to current backlog
    private void fixExpires(WebPageMeta meta, Date backlog) {
        if (meta.getExpires() == null) {
            return;
        }
        
        Date now = new Date();
        long backlogMs = Math.abs(now.getTime() - backlog.getTime());
        Date minExpires = new Date((long) (now.getTime() + backlogMs * 1.2));
        if (meta.getExpires().before(minExpires)) {
            meta.setExpires(minExpires);
        }
    }
    
    private void updateScheduledUpdate(WebPageMeta meta) {
        final Date now = new Date();
        Date nextUpdate = new Date(now.getTime() + TimeUnit.HOURS.toMillis(DEF_WEBPAGE_REINDEX_IVAL_HOURS));
        Date maxUpdate = new Date(now.getTime() + TimeUnit.DAYS.toMillis(MAX_RECHECK_DAYS));
        
        if (meta.getExpires() != null && meta.getExpires().before(nextUpdate)) {
            nextUpdate = meta.getExpires();
        }
        if (nextUpdate.after(maxUpdate)) {
            nextUpdate = maxUpdate;
        }
        meta.setScheduledUpdate(nextUpdate);
    }

    // remove from solr?
    @Transactional
    public void block(WebPageMeta meta) {
        checkNotNull(meta);
        
        LOG.info("blocking: " + meta.getUrl());
        
        final Date now = new Date();
        WebPageMeta pageMeta = em.find(WebPageMeta.class, meta.getId());
        pageMeta.setConsecutiveErrorCount(0);
        pageMeta.setEtag(meta.getEtag());
        pageMeta.setLastModified(meta.getLastModified());
        pageMeta.setExpires(meta.getExpires());
        pageMeta.setLastProcessed(now);

        pageMeta.setScheduledUpdate(new Date(now.getTime() + TimeUnit.DAYS.toMillis(BLOCK_RECHECK_DAYS)));

        em.persist(pageMeta);
    }

    @Transactional
    public void addUrls(List<String> extractedUrls) {
        int nAdded = 0;
        int nBadUrlFormat = 0;
        int nBadGuessedFileType = 0;
        int nDuplicates = 0;
        int nSkipped = 0;
        TypedQuery<Long> countQuery = qf.getCountWebPageMetaQuery();
        for (String url : extractedUrls) {
            if (nAdded == MAX_ADD_URLS_PER_PAGE) {
                nSkipped++;
                continue;
            }
            
            String newUrl = IndexingUtils.sanitizeUrl(url);
            if (newUrl == null) {
                LOG.debug("dropping url: " + url);
                nBadUrlFormat++;
                continue;
            }
            if (!isTypeWhitelisted(newUrl)) {
                nBadGuessedFileType++;
                continue;
            }
            if (countQuery.setParameter("url", newUrl).getSingleResult().longValue() > 0) {
                nDuplicates++;
                continue;
            }
            
            WebPageMeta meta = new WebPageMeta();
            meta.setUrl(newUrl);
            meta.setScheduledUpdate(new Date());
            meta.setLastProcessed(new Date(0));
            em.persist(meta);
            nAdded++;
            if (nAdded % FLUSH_AFTER_N_ADDS == 0) {
                em.flush();
            }
        }
        LOG.info(String.format("%d urls: %d added, %d bad url, %d bad file type, %d dupes, %d skipped",
            extractedUrls.size(), nAdded, nBadUrlFormat, nBadGuessedFileType, nDuplicates, nSkipped));
    }
    
    public static boolean isTypeWhitelisted(String url) {
        checkNotNull(url);
        String type = MimeType.get(url);
        if (type == null) {
            return true;
        }
        type = type.toLowerCase();
        for (String whitelistedType : TYPES_WHITELIST) {
            if (type.contains(whitelistedType)) {
                return true;
            }
        }
        LOG.debug(String.format("dropping %s url %s", type, url));
        return false;
    }

}
