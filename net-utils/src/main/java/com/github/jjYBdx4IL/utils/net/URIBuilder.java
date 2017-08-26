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
package com.github.jjYBdx4IL.utils.net;

//CHECKSTYLE:OFF
import com.github.fge.uritemplate.URITemplate;
import com.github.fge.uritemplate.URITemplateException;
import com.github.fge.uritemplate.vars.VariableMap;
import com.github.fge.uritemplate.vars.VariableMapBuilder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class URIBuilder {

    private final String result;

    public URIBuilder(String scheme, String host, String path, String... queryParts)
            throws URISyntaxException {
        this(scheme, host, 0, path, queryParts);
    }

    public URIBuilder(String scheme, String host, int port, String path, String... queryParts)
            throws URISyntaxException {

        if (scheme == null || scheme.length() == 0) {
            throw new URISyntaxException("", "scheme must not be empty or null");
        }
        if (host == null || host.length() == 0) {
            throw new URISyntaxException("", "host must not be empty or null");
        }
        if (queryParts.length % 2 != 0) {
            throw new URISyntaxException("", "need an even number of queryParts");
        }
        String query = null;
        if (queryParts.length > 0) {
            final VariableMapBuilder builder = VariableMap.newBuilder();
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < queryParts.length; i += 2) {
                if (queryParts[i] == null || queryParts[i].length() == 0) {
                    throw new URISyntaxException("", "queryPart key may not be null or empty");
                }
                map.put(queryParts[i], queryParts[i + 1] == null ? "" : queryParts[i + 1]);
            }

            builder.addMapValue("map", map);
            final VariableMap vars = builder.freeze();
            try {
                query = new URITemplate("{?map*}").toString(vars);
            } catch (URITemplateException ex) {
                throw new URISyntaxException("", ex.getMessage());
            }
        }
        if (port > 0) {
            result = new URI(scheme, null, host, port, path, null, null).toASCIIString() + (query != null ? query : "");
        } else {
            result = new URI(scheme, host, path, null, null).toASCIIString() + (query != null ? query : "");
        }
    }

    @Override
    public String toString() {
        return result;
    }

    public URL toURL() throws MalformedURLException {
        return new URL(result);
    }
}
