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
package com.github.jjYBdx4IL.javaee.h2frontend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

public class InetAddr {

    public static final Pattern IPV4_LOCALHOST_PATTERN = Pattern.compile("^127\\.\\d+\\.\\d+\\.\\d+$");
    private static final Logger LOG = LoggerFactory.getLogger(InetAddr.class);

    private InetAddr() {
    }

    /**
     * Checks whether the remove address of some http request is a localhost
     * address. This function also checks request headers for X-Forwarded-For
     * and Forwarded attributes. It won't trust request where those headers are
     * set.
     * 
     * @param req
     *            the http servlet request from which to get request headers and
     *            remote address
     * @return true if there are no forward headers <b>and</b> the remote
     *         address is a localhost address
     */
    public static boolean isLocalhostAddress(HttpServletRequest req) {
        if (req == null) {
            return false;
        }
        String address = req.getRemoteAddr();
        if (address == null) {
            return false;
        }
        if (!"::1".equals(address) && !IPV4_LOCALHOST_PATTERN.matcher(address).find()) {
            return false;
        }

        Enumeration<String> headers = req.getHeaderNames();
        if (headers == null) {
            LOG.warn("servlet container denies access to headers, assuming remote access");
            return false;
        }
        for (; headers.hasMoreElements();) {
            String header = headers.nextElement();
            if (header.toLowerCase().contains("forward")) {
                return false;
            }
        }
        return true;
    }
}
