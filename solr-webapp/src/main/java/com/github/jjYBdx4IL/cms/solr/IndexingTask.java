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

import com.github.jjYBdx4IL.cms.Env;
import com.github.jjYBdx4IL.cms.jpa.QueryFactory;
import com.github.jjYBdx4IL.cms.jpa.dto.WebPageMeta;
import com.github.jjYBdx4IL.cms.tika.MetaReply;
import com.github.jjYBdx4IL.cms.tika.TikaClient;
import com.github.jjYBdx4IL.utils.io.CompressionUtils;
import com.github.jjYBdx4IL.utils.io.IoUtils;
import com.github.jjYBdx4IL.utils.jsoup.JsoupTools;
import com.github.jjYBdx4IL.utils.net.DownloadUtils;
import com.github.jjYBdx4IL.utils.time.TimeUsage;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;
import crawlercommons.sitemaps.AbstractSiteMap;
import crawlercommons.sitemaps.SiteMap;
import crawlercommons.sitemaps.SiteMapIndex;
import crawlercommons.sitemaps.SiteMapParser;
import crawlercommons.sitemaps.SiteMapURL;
import crawlercommons.sitemaps.SiteMapURL.ChangeFrequency;
import crawlercommons.sitemaps.UnknownFormatException;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.cache.HeaderConstants;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.DataFormatException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Startup
@Singleton
public class IndexingTask implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(IndexingTask.class);

    public static final String INDEXER_NAME = "GeeGee";
    public static final String INDEXER_REFERER = "https://geegee.online/";
    public static final String COLLECTION = "WebSearchCollection";
    public static final String SOLR_COLLECTION_URL = "http://127.0.0.1:8983/solr/" + COLLECTION;
    public static final int MAX_PAGES_PER_WEBSITE = 100;
    public static final int MAX_SITEMAPS_PER_WEBSITE = 10;
    public static final SiteMapURL.ChangeFrequency MAX_CHANGE_FREQUENCY = ChangeFrequency.MONTHLY;
    public static final long SPAM_DELAY_MS = Env.isDevel() ? 0L : 900L * 1000L;
    public static final long MAX_DOC_SIZE = 10 * 1024 * 1024L;

    private Thread taskThread = null;
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);
    private SolrClient solrClient;
    private final UrlLockService lockService;
    private final RequestConfig requestConfig = RequestConfig.custom()
        .setConnectTimeout(30000)
        .setConnectionRequestTimeout(30000)
        .setSocketTimeout(30000)
        .setAuthenticationEnabled(false)
        .setContentCompressionEnabled(true)
        .setCircularRedirectsAllowed(false)
        .build();
    private CloseableHttpClient httpClient = null;

    public static final AtomicLong ping = new AtomicLong(0);
    private final TimeUsage timeUsage = new TimeUsage();

    @Resource
    ManagedThreadFactory threadFactory;
    @Inject
    IndexingDbService dbService;

    public IndexingTask() {
        lockService = new UrlLockService();
    }

    @PostConstruct
    public void postConstruct() {
        SolrConfig.init(); // fail startup if init goes wrong

        List<Header> defaultHeaders = new ArrayList<>();
        defaultHeaders.add(new BasicHeader(HttpHeaders.REFERER, INDEXER_REFERER));
        httpClient = HttpClients.custom()
            .setDefaultRequestConfig(requestConfig)
            .setDefaultHeaders(defaultHeaders)
            .disableCookieManagement()
            .build();

        taskThread = threadFactory.newThread(this);
        taskThread.setName("IdxTask");
        taskThread.start();
    }

    @PreDestroy
    public void preDestroy() {
        shutdownLatch.countDown();
        try {
            taskThread.join();
        } catch (InterruptedException ex) {
            LOG.warn("interrupted while waiting for " + taskThread + " to shut down", ex);
        }
        try {
            httpClient.close();
        } catch (Exception ex) {
        }
    }

    private void syncItJob() {
        try (SolrClient solr = SolrConfig.getClient()) {

            boolean done = false;
            int page = 0;
            while (!done) {
                SolrQuery query = new SolrQuery();
                query.set("q", "id:*");
                query.set("rows", 100);
                query.set("start", page * 100);
                QueryResponse response = solr.query(query);
                if (response.getResults().isEmpty()) {
                    done = true;
                    break;
                }
                List<String> notFoundInDb = dbService
                    .addLastAddedToSearchIndexDates(response.getBeans(WebPageBean.class));
                for (String urlId : notFoundInDb) {
                    solr.deleteById(urlId);
                }
                SolrConfig.commit(solr);
                page++;
            }
        } catch (SolrServerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        LOG.info("started");

        // syncItJob();

        try (SolrClient client = SolrConfig.getClient()) {
            solrClient = client;
            while (!shutdownLatch.await(0, TimeUnit.MILLISECONDS)) {
                WebPageMeta meta = dbService.getNextUrl4Processing();
                if (meta == null) {
                    shutdownLatch.await(3000, TimeUnit.MILLISECONDS);
                    continue;
                }

                try (UrlLockService.Lock lock = lockService.acquire(meta)) {
                    if (lock == null) {
                        LOG.info("url has process lock: " + meta.getUrl());
                        dbService.rescheduleLocked(meta);
                        continue;
                    }
                    processUrl(meta);
                }

                ping.set(System.currentTimeMillis());
                timeUsage.ivalDump();
            }
        } catch (Exception ex) {
            ping.set(0);
            LOG.warn("", ex);
        }
        LOG.info("stopped");
    }

    private void processUrl(WebPageMeta meta) {

        try {
            if (!hasAccess(meta)) {
                LOG.info("disallowed by robots.txt: " + meta.getUrl());
                dbService.block(meta);
                return;
            }
        } catch (IOException ex) {
            dbService.reschedule(meta, 30, TimeUnit.MINUTES);
            return;
        }

        if (!needsUpdate(meta)) {
            LOG.info("update not required: " + meta.getUrl());
            dbService.reschedule(meta, 30, TimeUnit.MINUTES);
            return;
        }

        LOG.info("processing: " + meta.getUrl());
        final Date now = new Date();

        boolean updated = false;
        try {
            try {
                HttpHead httpHead = new HttpHead(meta.getUrl());
                httpHead.setConfig(requestConfig);
                setPageFetchHeaders(httpHead, meta);

                boolean needUpdate = true;
                try (TimeUsage httpTimeUsage = timeUsage.startSub("http")) {
                    try (CloseableHttpResponse response = httpClient.execute(httpHead)) {
                        httpTimeUsage.stop();
                        needUpdate = checkResponseHeader(response, meta);
                    } catch (BadHttpStatusCodeException ex) {
                    }
                }

                if (needUpdate) {
                    HttpGet httpGet = new HttpGet(meta.getUrl());
                    httpGet.setConfig(requestConfig);
                    setPageFetchHeaders(httpGet, meta);

                    try (TimeUsage httpTimeUsage = timeUsage.startSub("http")) {
                        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                            httpTimeUsage.stop();
                            updated = handleResponse(response, meta);
                        }
                    }
                }
            } catch (FileTooLargeException | IndexingNotAllowedException
                | UnsupportedContentTypeException | IllegalArgumentException ex) {
                LOG.info("", ex);
                dbService.block(meta);
                return;
            }
        } catch (IOException ex) {
            LOG.info("", ex);
            dbService.error(meta);
            return;
        }

        if (updated) {
            LOG.info("updated: " + meta.getUrl());
            dbService.updated(meta);
        } else {
            LOG.info("not modified: " + meta.getUrl());
            dbService.notModified(meta);
        }
    }

    /**
     * 
     * @param response
     * @param meta
     * @return true if the resource needs to be updated
     * @throws IOException
     */
    private boolean checkResponseHeader(CloseableHttpResponse response, WebPageMeta meta) throws IOException {
        checkNotNull(response);
        checkNotNull(meta);

        LOG.info("" + response.getStatusLine());

        int status = response.getStatusLine().getStatusCode();

        if (LOG.isDebugEnabled()) {
            for (Header h : response.getAllHeaders()) {
                LOG.debug("" + h);
            }
        }

        if (status == HttpStatus.SC_NOT_MODIFIED) {
            return false;
        }

        if (status == HttpStatus.SC_NOT_FOUND) {
            throw new FileNotFoundException(meta.getUrl());
        }

        if (status >= 300 || status < 200) {
            LOG.info("error status: " + status + " for " + meta.getUrl());
            throw new BadHttpStatusCodeException(String.format("received status %d for %s", status, meta.getUrl()));
        }

        Header contentTypeHeader = response.getFirstHeader("Content-Type");
        String contentType = contentTypeHeader == null ? "" : contentTypeHeader.getValue().toLowerCase().trim();
        if (!contentType.contains("text/html")) {
            LOG.info("content type not supported yet: " + contentType);
            throw new UnsupportedContentTypeException(meta.getUrl());
        }

        Header contentLengthHeader = response.getFirstHeader("Content-Length");
        Long contentLength = contentLengthHeader == null ? null : Long.parseLong(contentLengthHeader.getValue());
        if (contentLength != null && contentLength > MAX_DOC_SIZE) {
            LOG.info("content too large: " + contentLength.intValue() + " bytes");
            throw new FileTooLargeException(meta.getUrl());
        }

        if (status == HttpStatus.SC_NO_CONTENT) {
            return false;
        }

        return true;
    }

    /**
     * 
     * @param response
     * @param meta
     * @return true if updated, false if unchanged
     * @throws IOException
     */
    private boolean handleResponse(CloseableHttpResponse response, WebPageMeta meta) throws IOException {
        if (!checkResponseHeader(response, meta)) {
            return false;
        }

        if (response.getEntity() == null || response.getEntity().getContent() == null) {
            return false;
        }

        Header etagHeader = response.getFirstHeader(HeaderConstants.ETAG);
        String etagValue = etagHeader == null ? null : etagHeader.getValue();
        meta.setEtag(etagValue != null && !etagValue.isEmpty() ? etagValue : null);

        Header expiresHeader = response.getFirstHeader(HeaderConstants.EXPIRES);
        meta.setExpires(expiresHeader == null ? null : DateUtils.parseDate(expiresHeader.getValue()));
        Header lastmodHeader = response.getFirstHeader(HeaderConstants.LAST_MODIFIED);
        meta.setLastModified(lastmodHeader == null ? null : DateUtils.parseDate(lastmodHeader.getValue()));

        byte[] data;
        try (TimeUsage httpTimeUsage = timeUsage.startSub("http");
            InputStream is = response.getEntity().getContent()) {
            data = IoUtils.toByteArray(is, MAX_DOC_SIZE);
        }
        if (data == null) {
            throw new FileTooLargeException(meta.getUrl());
        }
        LOG.info(String.format("retrieved %d bytes", data.length));

        MetaReply reply;
        try (TimeUsage tikaTimeUsage = timeUsage.startSub("tika")) {
            reply = TikaClient.parse(data);
        }

        if (reply.getRobots().contains("noindex")) {
            throw new IndexingNotAllowedException(meta.getUrl());
        }

        if (reply.getContentType() == null || !reply.getContentType().toLowerCase().contains("text/html")) {
            throw new UnsupportedContentTypeException(meta.getUrl());
        }

        Charset charset = Charset.forName("UTF-8");
        try {
            charset = Charset.forName(reply.getContentEncoding());
        } catch (Exception ex) {
            LOG.info(reply.getContentEncoding(), ex);
        }
        // String pageContent = new String(data, charset);
        // List<String> extractedUrls = URLUtils.hyperlinkUrls(pageContent);
        List<String> extractedUrls = JsoupTools.extractLinks(data, charset.name(), meta.getUrl());
        LOG.info(String.format("extracted %d urls", extractedUrls.size()));
        try (TimeUsage addUrlTimeUsage = timeUsage.startSub("addUrls")) {
            dbService.addUrls(extractedUrls);
        }

        WebPageBean pageBean = new WebPageBean(meta.getUrl(), reply.getTitle(), reply.getParsedContent(),
            reply.getKeywords(), reply.getContentType(), reply.getParsedBy(), reply.getLanguage(),
            reply.getDescription());

        try (TimeUsage solrTimeUsage = timeUsage.startSub("solr")) {
            solrClient.addBean(pageBean);
            SolrConfig.commit(solrClient);
        } catch (Exception ex) {
            LOG.error("", ex);
            // these exceptions are not related to external servers, so don't
            // reflect them in the processing db,
            // instead shut down the indexer
            throw new RuntimeException(ex);
        }

        meta.setLastAddedToSearchIndex(new Date());
        dbService.addedToSearchIndex(meta);

        return true;
    }

    private void setPageFetchHeaders(HttpRequestBase httpReq, WebPageMeta pageMeta) {
        httpReq.setHeader("User-Agent", INDEXER_NAME);

        if (pageMeta == null) {
            return;
        }

        if (httpReq instanceof HttpHead) {
            if (pageMeta.getEtag() != null) {
                httpReq.setHeader(HeaderConstants.IF_NONE_MATCH, pageMeta.getEtag());
            }
            if (pageMeta.getLastModified() != null) {
                httpReq.setHeader(HeaderConstants.IF_MODIFIED_SINCE, DateUtils.formatDate(pageMeta.getLastModified()));
            }
        }
    }

    /**
     * Use sitemap and db information to determine if some link needs to be
     * updated.
     */
    private boolean needsUpdate(WebPageMeta pageMeta) {
        // page not indexed yet?
        if (pageMeta == null) {
            return true;
        }

        final Date now = new Date();

        if (pageMeta.getLastProcessed().after(new Date(now.getTime() - SPAM_DELAY_MS))) {
            return false;
        }

        // not expired yet?
        if (pageMeta.getExpires() != null && pageMeta.getExpires().after(now)) {
            return false;
        }

        return true;
    }

    @SuppressWarnings("unused")
    private List<SiteMapURL> fetchSiteLinks(String siteUrl, BaseRobotRules rules)
        throws UnknownFormatException, IOException, InterruptedException {

        List<SiteMapURL> links = new ArrayList<>();
        List<SiteMap> siteMaps = new ArrayList<>();
        SiteMapParser sitemapParser = new SiteMapParser();
        for (String sitemapUrl : rules.getSitemaps()) {
            sitemapUrl = IndexingUtils.sanitizeUrl(sitemapUrl);
            if (sitemapUrl == null) {
                continue;
            }
            AbstractSiteMap siteMapCandidate = sitemapParser.parseSiteMap(new URL(sitemapUrl));
            if (siteMapCandidate instanceof SiteMapIndex) {
                SiteMapIndex siteMapIndex = (SiteMapIndex) siteMapCandidate;
                for (AbstractSiteMap aSiteMap : siteMapIndex.getSitemaps()) {
                    if (aSiteMap instanceof SiteMap) {
                        if (siteMaps.size() < MAX_SITEMAPS_PER_WEBSITE) {
                            siteMaps.add((SiteMap) aSiteMap);
                        }
                    } else {
                        LOG.warn("ignoring site map index inside site map index: " + aSiteMap.getUrl());
                    }
                }
            } else {
                if (!addLinks((SiteMap) siteMapCandidate, links)) {
                    LOG.info(String.format("%d links found in sitemap for %s", links.size(), siteUrl));
                    return links;
                }
            }
            if (shutdownLatch.await(1, TimeUnit.SECONDS)) {
                return links;
            }
        }
        LOG.info(siteMaps.size() + " site maps found for " + siteUrl);
        for (SiteMap siteMap : siteMaps) {
            AbstractSiteMap siteMapCandidate = sitemapParser.parseSiteMap(siteMap.getUrl());
            if (siteMapCandidate instanceof SiteMapIndex) {
                // ignoring 2nd-level sitemap index
                continue;
            }
            if (!addLinks((SiteMap) siteMapCandidate, links)) {
                LOG.info(String.format("%d links found in sitemap for %s", links.size(), siteUrl));
                return links;
            }
            if (shutdownLatch.await(1, TimeUnit.SECONDS)) {
                return links;
            }
        }

        LOG.info(String.format("%d links found in sitemap for %s", links.size(), siteUrl));
        return links;
    }

    private boolean addLinks(SiteMap siteMap, List<SiteMapURL> links) {
        LOG.info("" + siteMap.getUrl());
        for (SiteMapURL url : siteMap.getSiteMapUrls()) {
            String urlString = IndexingUtils.sanitizeUrl(url.getUrl().toExternalForm());
            if (urlString == null) {
                continue;
            }
            url.setUrl(urlString);
            links.add(url);
            if (links.size() >= MAX_PAGES_PER_WEBSITE) {
                return false;
            }
        }
        return true;
    }

    public boolean hasAccess(WebPageMeta meta) throws IOException {
        BaseRobotRules rules;
        try (TimeUsage robotsTimeUsage = timeUsage.startSub("robotsTxt")) {
            rules = getRobots(meta);
        }
        if (rules == null) {
            return false;
        }
        return rules.isAllowed(meta.getUrl());
    }

    private static final int MAX_ROBOTSTXT_FILESIZE = 65536;
    @SuppressWarnings("unused")
    private final Cache<String, byte[]> robotsTxtCache = CacheBuilder.<String, byte[]>newBuilder()
        .expireAfterAccess(50, TimeUnit.MINUTES).expireAfterWrite(12, TimeUnit.HOURS).build();
    private final Cache<String, String> invalidRobotsTxtCache = CacheBuilder.newBuilder()
        .expireAfterWrite(3, TimeUnit.DAYS).build();

    public BaseRobotRules getRobots(WebPageMeta meta) throws IOException {
        URL url = new URL(meta.getUrl());
        String urlString = new URL(url.getProtocol().toLowerCase(), url.getHost().toLowerCase(), url.getPort(),
            "/robots.txt").toExternalForm();

        if (invalidRobotsTxtCache.getIfPresent(urlString) != null) {
            return null;
        }

        byte[] robotsTxt = robotsTxtCache.getIfPresent(urlString);
        if (robotsTxt == null) {
            try {
                LOG.info("retrieving: " + urlString);
                robotsTxt = DownloadUtils.get(urlString, MAX_ROBOTSTXT_FILESIZE);
            } catch (FileNotFoundException ex) {
                robotsTxt = new byte[0];
            }
            if (robotsTxt == null) {
                invalidRobotsTxtCache.put(urlString, "");
                return null;
            }
            robotsTxtCache.put(urlString, CompressionUtils.compress(robotsTxt));
        } else if (robotsTxt.length > 0) {
            try {
                robotsTxt = CompressionUtils.decompress(robotsTxt);
            } catch (DataFormatException e) {
                throw new RuntimeException(e);
            }
        }

        SimpleRobotRulesParser robotParser = new SimpleRobotRulesParser();
        BaseRobotRules rules = robotParser.parseContent(
            urlString, robotsTxt, "text/plain", INDEXER_NAME);
        return rules;
    }

    @SuppressWarnings("serial")
    public static class FileTooLargeException extends IOException {
        public FileTooLargeException(String reason) {
            super(reason);
        }
    }

    @SuppressWarnings("serial")
    public static class IndexingNotAllowedException extends IOException {
        public IndexingNotAllowedException(String reason) {
            super(reason);
        }
    }

    @SuppressWarnings("serial")
    public static class UnsupportedContentTypeException extends IOException {
        public UnsupportedContentTypeException(String reason) {
            super(reason);
        }
    }

    @SuppressWarnings("serial")
    public static class BadHttpStatusCodeException extends IOException {
        public BadHttpStatusCodeException(String reason) {
            super(reason);
        }
    }

}
