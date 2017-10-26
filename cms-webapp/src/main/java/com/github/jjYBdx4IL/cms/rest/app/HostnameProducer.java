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
package com.github.jjYBdx4IL.cms.rest.app;

import com.github.jjYBdx4IL.cms.Env;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

@Provider
@RequestScoped
// CHECKSTYLE:OFF
public class HostnameProducer {

    public static final Pattern SUBDOMAIN_PATTERN = Pattern.compile("^(.+)\\.[^.]+\\.[^.]+$");
    public static final Pattern SUBDOMAIN_DEVEL_PATTERN = Pattern.compile("^(.+)\\.localhost$");
    
    // inject like:
    // @Inject @Named("hostname")
    // String hostname;

    @Produces
    @Named("hostname")
    public String getHostname(@Context HttpServletRequest req) {
        String host = req.getHeader("host").toLowerCase();
        int idxOf = host.indexOf(':');
        return idxOf == -1 ? host : host.substring(0, host.indexOf(':'));
    }
    
    @Context
    UriInfo uriInfo;
    @Produces
    @Named("subdomain")
    public String getSubDomain() {
        String host = uriInfo.getBaseUri().getHost().toLowerCase();
        Matcher m = SUBDOMAIN_PATTERN.matcher(host);
        if (Env.isDevel()) {
            m = SUBDOMAIN_DEVEL_PATTERN.matcher(host);
        }
        return m.find() ? m.group(1) : "";
    }
    
}
