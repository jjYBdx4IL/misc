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
import com.github.jjYBdx4IL.cms.jpa.dto.Domain;
import com.github.jjYBdx4IL.cms.jpa.dto.WebPageMeta;
import com.github.jjYBdx4IL.cms.tika.MetaReply;
import com.github.jjYBdx4IL.cms.tika.TikaClient;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;
import crawlercommons.sitemaps.AbstractSiteMap;
import crawlercommons.sitemaps.SiteMap;
import crawlercommons.sitemaps.SiteMapIndex;
import crawlercommons.sitemaps.SiteMapParser;
import crawlercommons.sitemaps.SiteMapURL;
import crawlercommons.sitemaps.SiteMapURL.ChangeFrequency;
import crawlercommons.sitemaps.UnknownFormatException;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.cache.HeaderConstants;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.solr.client.solrj.SolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.inject.Inject;

@Startup
@Singleton
public class IndexingTask implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(IndexingTask.class);

    public static final String INDEXER_NAME = "Indexer";
    public static final String COLLECTION = "WebSearchCollection";
    public static final String SOLR_COLLECTION_URL = "http://127.0.0.1:8983/solr/" + COLLECTION;
    public static final int MAX_PAGES_PER_WEBSITE = 100;
    public static final long DEF_WEBPAGE_REINDEX_IVAL_MS = 30 * 24 * 3600L * 1000L;
    public static final long ERR_WEBPAGE_REINDEX_IVAL_MS = 1 * 24 * 3600L * 1000L;
    public static final SiteMapURL.ChangeFrequency MAX_CHANGE_FREQUENCY = ChangeFrequency.MONTHLY;
    public static final long SPAM_DELAY_MS = Env.isDevel() ? 0L : 900L * 1000L;
    public static final long MAX_DOC_SIZE = 10 * 1024 * 1024L;

    private Thread taskThread = null;
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);
    private SolrClient solrClient;

    @Resource
    ManagedThreadFactory threadFactory;
    @Inject
    IndexingDbService dbService;

    @PostConstruct
    public void postConstruct() {
        SolrConfig.init(); // fail startup if init goes wrong
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
    }

    @Override
    public void run() {
        LOG.info("started");

        try (SolrClient client = SolrConfig.getClient()) {
            solrClient = client;
            while (!shutdownLatch.await(3000, TimeUnit.MILLISECONDS)) {
                Domain domain = dbService.getNextDomain4Processing();
                if (domain == null) {
                    continue;
                }
                LOG.info("processing domain: " + domain.getUrl());

                try {
                    processWebSite(domain);
                    dbService.updateDomain(domain, true);
                } catch (IOException | UnknownFormatException | URISyntaxException ex) {
                    LOG.info("", ex);
                    dbService.updateDomain(domain, false);
                }
            }
        } catch (Exception ex) {
            LOG.warn("", ex);
        }
        LOG.info("stopped");
    }

    private void processWebSite(Domain domain)
        throws UnknownFormatException, IOException, URISyntaxException, InterruptedException {

        BaseRobotRules rules = getRobots(domain.getUrl());
        List<SiteMapURL> pageLinks = fetchSiteLinks(domain.getUrl(), rules);

        RequestConfig requestConfig = RequestConfig.custom().
            setConnectTimeout(30000).
            setConnectionRequestTimeout(30000).
            setSocketTimeout(30000).
            build();
        
        for (SiteMapURL url : pageLinks) {
            String urlString = IndexingUtils.normalizeUrl(url.getUrl().toExternalForm());
            if (!hasAccess(rules, urlString)) {
                LOG.info("disallowed by robots.txt: " + urlString);
                continue;
            }

            WebPageMeta pageMeta = dbService.getWebPageMeta(urlString);

            if (!needsUpdate(url, pageMeta)) {
                LOG.info("skipping (no processing required yet): " + urlString);
                continue;
            }
            
            LOG.info("processing: " + urlString);
            Date now = new Date();

            // https://stackoverflow.com/questions/33395342/how-does-an-etag-token-works-in-conditional-get-in-http
            try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                HttpGet httpGet = new HttpGet(url.getUrl().toURI());
                httpGet.setConfig(requestConfig);
                setPageFetchHeaders(httpGet, pageMeta);

                if (pageMeta == null) {
                    pageMeta = new WebPageMeta();
                    pageMeta.setUrl(urlString);
                }
                pageMeta.setScheduledUpdate(new Date(now.getTime() + DEF_WEBPAGE_REINDEX_IVAL_MS));
                
                try {
                    try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                        if (handleResponse(response, pageMeta)) {
                            pageMeta.setConsecutiveErrorCount(0);
                        } else {
                            pageMeta.setScheduledUpdate(new Date(now.getTime() + ERR_WEBPAGE_REINDEX_IVAL_MS));
                            pageMeta.setConsecutiveErrorCount(pageMeta.getConsecutiveErrorCount() + 1);
                        }
                    }
                } catch (ConnectTimeoutException ex) {
                    LOG.info("", ex);
                    pageMeta.setScheduledUpdate(new Date(now.getTime() + ERR_WEBPAGE_REINDEX_IVAL_MS));
                    pageMeta.setConsecutiveErrorCount(pageMeta.getConsecutiveErrorCount() + 1);
                }

                pageMeta.setLastProcessed(new Date());
                dbService.updateWebPageMeta(pageMeta);
            }
            if (shutdownLatch.await(1, TimeUnit.SECONDS)) {
                return;
            }
        }
    }

    private boolean handleResponse(CloseableHttpResponse response, WebPageMeta pageMeta) {
        checkNotNull(pageMeta);

        LOG.info("handleResponse: " + pageMeta.getUrl());
        LOG.info("" + response.getStatusLine());

        int status = response.getStatusLine().getStatusCode();
        for (Header h : response.getAllHeaders()) {
            LOG.info("" + h);
        }
        if (status == HttpStatus.SC_NOT_MODIFIED) {
            LOG.info("not modified");
            return true;
        }
        if (status >= 300 || status < 200) {
            LOG.info("error status: " + status + " for " + pageMeta.getUrl());
            return false;
        }
        
        Header etagHeader = response.getFirstHeader(HeaderConstants.ETAG);
        String etagValue = etagHeader == null ? null : etagHeader.getValue();
        pageMeta.setEtag(etagValue != null && !etagValue.isEmpty() ? etagValue : null);
        
        Header expiresHeader = response.getFirstHeader(HeaderConstants.EXPIRES);
        pageMeta.setExpires(expiresHeader == null ? null : DateUtils.parseDate(expiresHeader.getValue()));
        Header lastmodHeader = response.getFirstHeader(HeaderConstants.LAST_MODIFIED);
        pageMeta.setLastModified(lastmodHeader == null ? null : DateUtils.parseDate(lastmodHeader.getValue()));
        
//        Header contentTypeHeader = response.getFirstHeader("Content-Type");
//        String contentType = contentTypeHeader == null ? "" : contentTypeHeader.getValue().toLowerCase().trim();
//        if (!contentType.contains("text/html")) {
//            LOG.info("content type not supported yet: " + contentType);
//            return true;
//        }
        
        Header contentLengthHeader = response.getFirstHeader("Content-Length");
        Long contentLength = contentLengthHeader == null ? null : Long.parseLong(contentLengthHeader.getValue());
        if (contentLength != null && contentLength > MAX_DOC_SIZE) {
            LOG.info("content too large: " + contentLength.intValue()  + " bytes");
            return true;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        try (InputStream is = response.getEntity().getContent()){
            while(baos.size() < MAX_DOC_SIZE && is.available() > 0) {
                int n = is.read(buf);
                baos.write(buf, 0, n);
            }
            if (is.available() > 0) {
                LOG.info("content too large: " + contentLength.intValue()  + " bytes");
                return true;
            }
        } catch (IOException ex) {
            LOG.info("", ex);
            return false;
        }
        
        byte[] data = baos.toByteArray();
        baos = null;
        MetaReply reply = TikaClient.parse(data);
        data = null;

        if (reply.getRobots().contains("noindex")) {
            return true;
        }
        
        WebPageBean pageBean = new WebPageBean(pageMeta.getUrl(), reply.getTitle(), reply.getParsedContent(),
            reply.getKeywords(), reply.getContentType(), reply.getLastParsedBy());

        try {
            solrClient.addBean(pageBean);
            SolrConfig.commit(solrClient);
        } catch (Exception ex) {
            LOG.error("", ex);
            // these exceptions are not related to external servers, so don't
            // reflect them in the processing db,
            // instead shut down the indexer
            throw new RuntimeException(ex);
        }

        return true;
    }

    private void setPageFetchHeaders(HttpGet httpGet, WebPageMeta pageMeta) {
        httpGet.setHeader("User-Agent", INDEXER_NAME);
        
        if (pageMeta == null) {
            return;
        }

        if (pageMeta.getEtag() != null) {
            httpGet.setHeader(HeaderConstants.IF_NONE_MATCH, pageMeta.getEtag());
        }
        if (pageMeta.getLastModified() != null) {
            httpGet.setHeader(HeaderConstants.IF_MODIFIED_SINCE, DateUtils.formatDate(pageMeta.getLastModified()));
        }
    }

    /**
     * Use sitemap and db information to determine if some link needs to be
     * updated.
     */
    private boolean needsUpdate(SiteMapURL url, WebPageMeta pageMeta) {
        checkNotNull(url);

        // page not indexed yet?
        if (pageMeta == null) {
            return true;
        }

        Date now = new Date();

        if (pageMeta.getLastProcessed().after(new Date(now.getTime() - SPAM_DELAY_MS))) {
            return false;
        }
        
        // not expired yet?
        if (pageMeta.getExpires() != null && pageMeta.getExpires().after(now)) {
            return false;
        }

        // not modified since last update?
        if (url.getLastModified() != null && pageMeta.getLastModified() != null
            &&!pageMeta.getLastModified().before(url.getLastModified())) {
            return false;
        }

        // not expired according to update frequency setting?
        if (url.getChangeFrequency() != null) {
            ChangeFrequency freq = url.getChangeFrequency();
            if (freq.compareTo(MAX_CHANGE_FREQUENCY) > 0) {
                freq = MAX_CHANGE_FREQUENCY;
            }
            long delta;
            switch (freq) {
                case HOURLY:
                    delta = 1 * 3600L * 1000L;
                    break;
                case DAILY:
                    delta = 24 * 3600L * 1000L;
                    break;
                case WEEKLY:
                    delta = 7 * 24 * 3600L * 1000L;
                    break;
                case MONTHLY:
                    delta = 30 * 24 * 3600L * 1000L;
                    break;
                case YEARLY:
                case NEVER:
                    delta = 365 * 24 * 3600L * 1000L;
                    break;
                case ALWAYS:
                default:
                    delta = 0L;
            }
            if (pageMeta.getLastModified().getTime() + delta > now.getTime()) {
                return false;
            }
        }

        return true;
    }

    private List<SiteMapURL> fetchSiteLinks(String siteUrl, BaseRobotRules rules)
        throws UnknownFormatException, IOException {

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
                        siteMaps.add((SiteMap) aSiteMap);
                    } else {
                        LOG.warn("ignoring site map index inside site map index: " + aSiteMap.getUrl());
                    }
                }
            } else {
                siteMaps.add((SiteMap) siteMapCandidate);
            }
        }
        LOG.info(siteMaps.size() + " site maps found for " + siteUrl);
        List<SiteMapURL> links = new ArrayList<>();
        for (SiteMap siteMap : siteMaps) {
            LOG.info("" + siteMap.getUrl());
            for (SiteMapURL url : siteMap.getSiteMapUrls()) {
                LOG.info("link: " + url);
                String urlString = IndexingUtils.sanitizeUrl(url.getUrl().toExternalForm());
                if (urlString == null) {
                    continue;
                }
                url.setUrl(urlString);
                links.add(url);
                if (links.size() == MAX_PAGES_PER_WEBSITE) {
                    return links;
                }
            }
        }
        LOG.info(String.format("%d links found in sitemap for %s", links.size(), siteUrl));
        return links;
    }

    public boolean hasAccess(BaseRobotRules rules, String urlString) throws MalformedURLException {
        URL url = new URL(urlString);
        String urlFile = url.getFile();
        return rules.isAllowed(urlFile);
    }

    public BaseRobotRules getRobots(String urlString) throws IOException {
        URL url = new URL(urlString);
        url = new URL(url.getProtocol().toLowerCase(), url.getHost().toLowerCase(), url.getPort(), "");
        String urlNoPath = url.toExternalForm();
        URL robotsTxtUrl = new URL(urlNoPath + "/robots.txt");
        byte[] robotsTxtContent = IOUtils.toByteArray(robotsTxtUrl);
        SimpleRobotRulesParser robotParser = new SimpleRobotRulesParser();
        BaseRobotRules rules = robotParser.parseContent(
            robotsTxtUrl.toExternalForm(), robotsTxtContent, "text/plain", INDEXER_NAME);
        return rules;
    }
}
