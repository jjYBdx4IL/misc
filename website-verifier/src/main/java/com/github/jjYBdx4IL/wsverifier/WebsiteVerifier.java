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
package com.github.jjYBdx4IL.wsverifier;

import com.brucezee.jspider.Page;
import com.brucezee.jspider.Request;
import com.brucezee.jspider.Result;
import com.brucezee.jspider.Spider;
import com.brucezee.jspider.SpiderConfig;
import com.brucezee.jspider.SpiderListener;
import com.brucezee.jspider.common.enums.ResponseType;
import com.brucezee.jspider.processor.PageProcessor;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

//CHECKSTYLE:OFF
public class WebsiteVerifier {

    private static final Logger LOG = LoggerFactory.getLogger(WebsiteVerifier.class);

    // url -> set of referral pages containing the link
    protected final Map<String, Set<String>> sourceUrls = new HashMap<>();
    protected final Set<String> badUrls = new HashSet<>();
    protected String[] exclusions = null;
    private String rootUrl = null;
    protected final Set<String> externalUrls = new HashSet<>();
    private boolean checkExternalUrls = false;
    private final Pattern zipPattern = Pattern.compile("\\.(zip|jar|war)$", Pattern.CASE_INSENSITIVE);

    /**
     * verify method. runs the crawling process.
     * 
     * @param rootUrl
     *            start url, crawling will not follow links that do not start
     *            with this string
     * @param exclusions
     *            regexes matched against the URL after removing the rootUrl
     *            prefix from it
     * @return true iff no problems were found
     */
    public boolean verify(final String rootUrl, String... exclusions) {
        clear();
        this.rootUrl = rootUrl;
        this.exclusions = exclusions;

        // page processor
        PageProcessor pageProcessor = new PageProcessor() {
            @Override
            public Result process(Request request, Page page) {
                String pageUrl = request.getUrl();
                LOG.info("process: " + pageUrl);
                if (LOG.isTraceEnabled()) {
                    LOG.trace("page: " + page);
                }

                Result result = new Result();

                if (zipPattern.matcher(pageUrl).find()) {
                    if (!verifyZip(page)) {
                        throw new RuntimeException();
                    }
                    return result;
                }

                // parse html
                if (!(page.getResult() instanceof String)) {
                    return result;
                }
                
                // find new urls
                for (Element element : page.document().select("a")) {
                    String url = element.absUrl("href");
                    if (!isFollow(url, request)) {
                        continue;
                    }
                    LOG.info("url found: " + url);
                    if (zipPattern.matcher(url).find()) {
                        page.addTargetRequest(url, ResponseType.STREAM);
                    } else {
                        page.addTargetRequest(url);
                    }
                    registerLink(url, pageUrl);
                }
                for (Element element : page.document().select("img")) {
                    String url = element.absUrl("src");
                    if (!isFollow(url, request)) {
                        continue;
                    }
                    LOG.info("img url found: " + url);
                    page.addTargetRequest(url, ResponseType.BYTES);
                    registerLink(url, pageUrl);
                }
                for (Element element : page.document().select("link")) {
                    String url = element.absUrl("href");
                    if (!isFollow(url, request)) {
                        continue;
                    }
                    LOG.info("img url found: " + url);
                    page.addTargetRequest(url, ResponseType.BYTES);
                    registerLink(url, pageUrl);
                }
                for (Element element : page.document().select("script")) {
                    String url = element.absUrl("src");
                    if (!isFollow(url, request)) {
                        continue;
                    }
                    LOG.info("img url found: " + url);
                    page.addTargetRequest(url, ResponseType.BYTES);
                    registerLink(url, pageUrl);
                }

                return result;
            }

        };

        // create, config and start
        SpiderConfig config = new SpiderConfig(this.toString(), 1);
        config.setExitWhenComplete(true);
        config.setDestroyWhenExit(true);
        config.setEmptySleepMillis(1000L);

        Spider spider = Spider.create();
        spider.setPageProcessor(pageProcessor)
            .addStartRequests(rootUrl).setSpiderConfig(config);

        registerLink(rootUrl, "start");

        spider.addSpiderListeners(new SpiderListener() {

            @Override
            public void onSuccess(Request request, Page page, Result result) {
                LOG.info("onSuccess");
            }

            @Override
            public void onError(Request request, Page page) {
                LOG.info("onError");
                badUrls.add(request.getUrl());
            }
        });
        spider.run();

        return isOk();
    }
    
    protected boolean isFollow(String url, Request request) {
        if (!url.startsWith(rootUrl)) {
            return isCheckExternalUrls() && request.getUrl().startsWith(rootUrl);
        }        
        url = url.substring(rootUrl.length());
        for (String m : exclusions) {
            if (url.matches(m)) {
                return false;
            }
        }
        return true;
    }

    protected void clear() {
        badUrls.clear();
        sourceUrls.clear();
    }

    protected void registerLink(String url, String pageUrl) {
        Set<String> pageUrls = sourceUrls.get(url);
        if (pageUrls == null) {
            pageUrls = new HashSet<>();
            sourceUrls.put(url, pageUrls);
        }
        pageUrls.add(pageUrl);
    }

    public Set<String> getPagesContainingUrl(String url) {
        return (Set<String>) Collections.unmodifiableSet(sourceUrls.get(url));
    }

    public Set<String> getBadUrls() {
        return (Set<String>) Collections.unmodifiableSet(badUrls);
    }

    public boolean isOk() {
        return badUrls.isEmpty();
    }

    /**
     * dump result into human-readable report.
     * 
     * @return the report
     */
    public String resultToString() {
        StringBuilder sb = new StringBuilder();

        if (badUrls.isEmpty()) {
            sb.append("no problems found");
        } else {
            sb.append(badUrls.size()).append(" bad link(s) found:").append(System.lineSeparator());
            for (String url : badUrls) {
                sb.append(" => ").append(url).append(" found on page(s):").append(System.lineSeparator());
                for (String pageUrl : sourceUrls.get(url)) {
                    sb.append("      * ").append(pageUrl).append(System.lineSeparator());
                }
            }
        }

        return sb.toString();
    }

    public Set<String> getExternalUrls() {
        return externalUrls;
    }

    public boolean isCheckExternalUrls() {
        return checkExternalUrls;
    }

    public void setCheckExternalUrls(boolean checkExternalUrls) {
        this.checkExternalUrls = checkExternalUrls;
    }
    
    /**
     * Verify zip stream integrity. Requires at least one non-directory entry to be present in the zip stream.
     */
    private boolean verifyZip(Page page) {
        try (InputStream is = page.getStreamResult();
                ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry ze = zis.getNextEntry();
            if(ze == null) {
                return false;
            }
            long nFiles = 0;
            while(ze != null) {
                ze.getCrc();
                ze.getCompressedSize();
                ze.getName();
                nFiles += ze.isDirectory() ? 0 : 1;
                ze = zis.getNextEntry();
            }
            return nFiles > 0;
        } catch (ZipException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }
}
