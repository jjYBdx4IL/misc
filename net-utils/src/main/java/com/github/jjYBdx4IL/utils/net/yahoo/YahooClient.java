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
package com.github.jjYBdx4IL.utils.net.yahoo;

import com.github.jjYBdx4IL.utils.net.URIBuilder;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

//CHECKSTYLE:OFF
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class YahooClient {

    private static final Logger LOG = LoggerFactory.getLogger(YahooClient.class);

    public YahooClient() {
    }

    public static URL constructURL(String symbol, YahooIval ival) throws URISyntaxException, MalformedURLException {
        return new URIBuilder("http", "ichart.finance.yahoo.com", "/table.csv",
            "s", symbol,
            "a", "0", // start month - 1
            "b", "1", // start day
            "c", "1700", // start year
            "d", "0", // end month - 1
            "e", "1", // end day
            "f", "2038", // end year
            "g", ival.toString(), // ival
            "ignore", ".csv").toURL();
    }

    public YahooObservations get(String symbol, YahooIval ival) throws IOException, ParseException {
        final URL url;

        try {
            url = constructURL(symbol, ival);
        } catch (MalformedURLException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }

        return get(url);
    }

    public YahooObservations get(final URL url) throws IOException, ParseException {
        YahooObservations observations = null;

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url.toExternalForm());
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                int sc = response.getStatusLine().getStatusCode();
                if (sc != 200) {
                    throw new IOException(String.format("%s (%d)",
                        response.getStatusLine().getReasonPhrase(), sc));
                }
                observations = new YahooObservations();
                observations.setObservations(parseStream(response.getEntity().getContent()));
            }
        }

        return observations;
    }

    protected List<YahooObservation> parseStream(InputStream inputStream) throws IOException, ParseException {
        List<YahooObservation> observations = new ArrayList<>();
        // Date,Open,High,Low,Close,Volume,Adj Close
        // 2013-04-01,795.01,814.83,768.40,774.85,2855500,774.85
        // ...
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "ASCII"))) {
            String line = br.readLine();
            if (line == null) {
                throw new IOException("no header line");
            }
            LOG.trace("line=" + line);
            line = br.readLine();
            while (line != null) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("line=" + line);
                }
                StringTokenizer t = new StringTokenizer(line, ",");
                String date = null, open = null, high = null, low = null, close = null,
                    volume = null, adjClose = null;
                for (YahooCsvColname colname : YahooCsvColname.values()) {
                    String value = t.nextToken();
                    switch (colname) {
                        case DATE:
                            date = value;
                            break;
                        case OPEN:
                            open = value;
                            break;
                        case CLOSE:
                            close = value;
                            break;
                        case HIGH:
                            high = value;
                            break;
                        case LOW:
                            low = value;
                            break;
                        case VOLUME:
                            volume = value;
                            break;
                        case ADJ_CLOSE:
                            adjClose = value;
                            break;
                        default:
                            throw new IOException("unhandled column: " + colname);
                    }
                }
                YahooObservation yd = new YahooObservation(date, open, high, low, close, volume, adjClose);
                observations.add(yd);

                line = br.readLine();
            }

        }
        observations.sort(new YahooObservationDateComparator());
        return observations;
    }

}
