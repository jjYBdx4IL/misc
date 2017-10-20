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

import com.github.jjYBdx4IL.utils.net.AddressUtils;
import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;
import crawlercommons.filters.basic.BasicURLNormalizer;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

public class IndexingUtils {

    public static final int MAX_URL_LEN = 255;
    public static final BasicURLNormalizer urlNormalizer = new URLNoQueryNormalizer();

    private IndexingUtils() {
    }

    public static String normalizeUrl(String url) {
        return urlNormalizer.filter(url);
    }
    
    public static boolean isValidDomainName(String domainName) {
        try {
            domainName = new URL(domainName).getHost();
            if (domainName == null || domainName.isEmpty()) {
                return false;
            }
            if (!domainName.contains(".")) {
                return false;
            }
            if (!InternetDomainName.isValid(domainName)) {
                return false;
            }
            if (InetAddresses.isInetAddress(domainName.trim())) {
                return false;
            }
            if (!AddressUtils.isSimpleNonLocalAddress(domainName)) {
                return false;
            }
            return true;
        } catch (MalformedURLException | UnknownHostException e) {
            return false;
        }
    }
    
    public static String sanitizeUrl(String url) {
        String urlString = normalizeUrl(url);
        if (urlString == null) {
            return null;
        }
        if (urlString.length() > MAX_URL_LEN) {
            return null;
        }
        if (!isValidDomainName(urlString)) {
            return null;
        }
        return urlString;
    }
}
