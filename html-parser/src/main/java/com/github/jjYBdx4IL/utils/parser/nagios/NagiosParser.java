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
package com.github.jjYBdx4IL.utils.parser.nagios;

import com.github.jjYBdx4IL.utils.time.TimeUtils;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// CHECKSTYLE:OFF
/**
 * Nagios status cgi parser.
 * 
 * <p>
 * Parses output from
 * http://you.nagios.host.com/nagios3/cgi-bin/status.cgi?host=all&amp;style=detail.
 * </p>
 *
 * @author Github jjYBdx4IL Projects
 */
public class NagiosParser {

    private static final int MIN_COLS = 7;
    private static final String HOST = "Host";
    private static final String SERVICE = "Service";
    private static final String STATUS = "Status";
    private static final String LAST_CHECK = "Last Check";
    private static final String DURATION = "Duration";
    private static final String ATTEMPT = "Attempt";
    private static final String STATUS_INFORMATION = "Status Information";
    private static final String SPACE = "&nbsp;";
    private static final String ACK_SELECTOR = "td[class$=ACK]";
    private static final String DOWNTIME_SELECTOR =
        "img[alt^=This service is currently in a period of scheduled downtime]";
    private static final Pattern ATTEMPTS_PATTERN = Pattern.compile("(\\d+)/(\\d+)");
    private final SimpleDateFormat lastCheckParser = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    private String html;

    public NagiosParser() {
        lastCheckParser.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    // CHECKSTYLE IGNORE .* FOR NEXT 1 LINE
    public List<CheckStatus> parse(String statusCgiHtml) throws ParseException {
        html = statusCgiHtml;
        return parse();
    }

    // CHECKSTYLE IGNORE .* FOR NEXT 1 LINE
    @SuppressWarnings("deprecation")
    public List<CheckStatus> parse(URL statusCgiUrl) throws IOException, ParseException {
        URLConnection conn = statusCgiUrl.openConnection();
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        html = IOUtils.toString(conn.getInputStream());
        return parse();
    }

    // CHECKSTYLE IGNORE .* FOR NEXT 1 LINE
    public List<CheckStatus> parse() throws ParseException {
        Document doc = Jsoup.parse(html);
        // logger.info(doc.toString());
        // CHECKSTYLE IGNORE MagicNumber FOR NEXT 1 LINE
        Map<String, Integer> colMap = new HashMap<>(10);

        Integer i = 0;
        for (Element e : doc.select("html body table th.status")) {
            String colName = e.html();
            if (colName.indexOf(SPACE) > -1) {
                colName = colName.substring(0, colName.indexOf(SPACE));
            }
            colMap.put(colName, i);
            i++;
        }
        // CHECKSTYLE IGNORE MagicNumber FOR NEXT 1 LINE
        List<CheckStatus> res = new ArrayList<>(10);
        String defaultHost = null;
        for (Element tr : doc.select("html > body > table > tbody > tr")) {
            // for(Element p : e.parents()) {
            // logger.debug(p.tagName());
            // }
            // logger.debug("row: " + tr);
            Elements tds = tr.select(":root > td");
            if (tds.size() < MIN_COLS) {
                continue;
            }

            // logger.info("found td's: " + tds);
            String htmlBuf;

            final String host;
            final String service;
            final Status status;
            final Date lastCheck;
            final long durationMillis;
            final Matcher attemptMatcher;
            final int attempt;
            final int maxAttempts;
            final String statusInfo;
            final boolean ack;
            final boolean downtime;

            htmlBuf = tds.get(colMap.get(HOST)).text();
            if (htmlBuf.isEmpty()) {
                if (defaultHost == null) {
                    throw new ParseException("check status without host value");
                }
                host = defaultHost;
            } else {
                host = htmlBuf;
            }

            service = tds.get(colMap.get(SERVICE)).text();
            if (service.isEmpty()) {
                throw new ParseException("failed to parse service field (empty)");
            }

            htmlBuf = tds.get(colMap.get(STATUS)).html();
            if (htmlBuf.indexOf(SPACE) > -1) {
                htmlBuf = htmlBuf.substring(0, htmlBuf.indexOf(SPACE));
            }
            try {
                status = Status.byString(htmlBuf);
            } catch (InvalidStatusException ex) {
                throw new ParseException(String.format("failed to parse status field: '%s'", htmlBuf), ex);
            }

            htmlBuf = tds.get(colMap.get(LAST_CHECK)).html();
            if (htmlBuf.equals("N/A")) {
                lastCheck = null;
            } else {
                try {
                    lastCheck = lastCheckParser.parse(htmlBuf);
                } catch (java.text.ParseException ex) {
                    throw new ParseException(String.format("failed to parse last-check field: '%s'", htmlBuf), ex);
                }
            }

            durationMillis = TimeUtils.durationToMillis(tds.get(colMap.get(DURATION)).html());

            htmlBuf = tds.get(colMap.get(ATTEMPT)).html();
            attemptMatcher = ATTEMPTS_PATTERN.matcher(htmlBuf);
            if (!attemptMatcher.find()) {
                throw new ParseException(String.format("failed to parse attempt field: '%s'", htmlBuf));
            }
            try {
                attempt = Integer.parseInt(attemptMatcher.group(1));
                maxAttempts = Integer.parseInt(attemptMatcher.group(2));
            } catch (NumberFormatException ex) {
                throw new ParseException(String.format("failed to parse attempt field: '%s'", htmlBuf), ex);
            }

            statusInfo = tds.get(colMap.get(STATUS_INFORMATION)).text();

            ack = !tds.get(colMap.get(SERVICE)).select(ACK_SELECTOR).isEmpty();

            downtime = !tds.get(colMap.get(SERVICE)).select(DOWNTIME_SELECTOR).isEmpty();

            CheckStatus checkStatus = new CheckStatus(host, service, status, lastCheck, durationMillis, attempt,
                maxAttempts, statusInfo, ack, downtime);
            res.add(checkStatus);
            defaultHost = checkStatus.getHost();
        }
        return res;
    }
}
