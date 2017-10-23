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
package com.github.jjYBdx4IL.cms.rest;

import com.github.jjYBdx4IL.cms.jpa.QueryFactory;
import com.github.jjYBdx4IL.cms.rest.app.HtmlBuilder;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;

//CHECKSTYLE:OFF
@Path("sitemap.xml")
@PermitAll
@Transactional
public class SiteMap {

    private static final Logger LOG = LoggerFactory.getLogger(SiteMap.class);

    @Context
    UriInfo uriInfo;
    @Inject
    HtmlBuilder htmlBuilder;
    @Inject
    QueryFactory qf;
    @Inject
    @Named("subdomain")
    String subdomain;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response sitemap() throws MalformedURLException, IllegalArgumentException, UriBuilderException {
        LOG.trace("sitemap()");

        WebSitemapGenerator wsg = new WebSitemapGenerator(uriInfo.getBaseUriBuilder().build().toString());

        qf.getArticleDisplayList(null, null, true, subdomain).getResultList()
            .forEach(article -> {
                try {
                    wsg.addUrl(new WebSitemapUrl.Options(htmlBuilder.constructArticleLink(article))
                        .lastMod(article.getLastModified()).build());
                } catch (MalformedURLException e) {
                    LOG.error("", e);
                }
            });

        List<String> result = wsg.writeAsStrings();

        if (result.size() > 1) {
            LOG.error("limit reached");
        }
        if (result.isEmpty()) {
            return Response.noContent().build();
        }
        return Response.ok().entity(result.get(0)).build();
    }

}
