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

import crawlercommons.filters.basic.BasicURLNormalizer;

import java.net.IDN;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class URLNoQueryNormalizer extends BasicURLNormalizer {

    @Override
    public String filter(String urlString) {
        if (urlString == null) {
            return null;
        }

        try {
            if (!urlString.toLowerCase().startsWith("http://") && !urlString.toLowerCase().startsWith("https://")) {
                urlString = "https://" + urlString;
            }
            URL url = new URL(urlString);
            String protocol = url.getProtocol().toLowerCase();
            if (protocol == null || !protocol.equals("http") && !protocol.equals("https")) {
                protocol = "https";
            }
            String hostname = url.getHost();
            if (hostname == null) {
                return null;
            }
            String path = url.getPath();
            if (path == null) {
                path = "";
            }
            String result = super.filter(protocol + "://" + IDN.toASCII(hostname) + path);
            // feeding the url string to the URL constructor is not enough to
            // make sure it is compatible with httpclient
            URI.create(result);
            return result;
        } catch (MalformedURLException | IllegalArgumentException ex) {
            LOG.warn("", ex);
            return null;
        }
    }
}
