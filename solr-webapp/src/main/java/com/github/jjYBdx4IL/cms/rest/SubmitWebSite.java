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

import static j2html.TagCreator.div;
import static j2html.TagCreator.form;
import static j2html.TagCreator.input;

import com.github.jjYBdx4IL.cms.jpa.AppCache;
import com.github.jjYBdx4IL.cms.jpa.QueryFactory;
import com.github.jjYBdx4IL.cms.jpa.dto.Domain;
import com.github.jjYBdx4IL.cms.rest.app.HtmlBuilder;
import com.github.jjYBdx4IL.cms.solr.IndexingUtils;
import com.github.jjYBdx4IL.cms.solr.ProtoHostURLNormalizer;
import j2html.tags.ContainerTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

//CHECKSTYLE:OFF
@Path("submitWebSite")
@PermitAll
@Transactional
public class SubmitWebSite {

    private static final Logger LOG = LoggerFactory.getLogger(SubmitWebSite.class);

    private static final ProtoHostURLNormalizer hostNormalizer = new ProtoHostURLNormalizer();
    
    @Context
    UriInfo uriInfo;
    @Inject
    HtmlBuilder htmlBuilder;
    @Inject
    QueryFactory qf;
    @Inject
    AppCache appCache;
    @PersistenceContext
    EntityManager em;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response submit(@QueryParam("site") String site) {
        htmlBuilder.setPageTitle("Submit Website");

        ContainerTag container = div().withClass("container");

        site = hostNormalizer.filter(site);
        
        if (site != null) {
            if (!IndexingUtils.isValidDomainName(site)) {
                container.with(
                    div(
                        div("Failed to submit website: " + site)
                            .withClass("col-12 error")
                    ).withClass("row")
                );
            } else {
                container.with(
                    div(
                        div("Successfully submitted website: " + site)
                            .withClass("col-12 success")
                    ).withClass("row")
                );
                Domain domain = qf.getDomainByUrl(site);
                if (domain == null) {
                    domain = new Domain();
                    domain.setUrl(site);
                    domain.setScheduledUpdate(new Date());
                    domain.setLastProcessed(new Date(0));
                } else {
                    // do not allow refresh of already scheduled update
                    Date now = new Date();
                    if (domain.getScheduledUpdate() == null || domain.getScheduledUpdate().after(now)) {
                        domain.setScheduledUpdate(now);
                    }
                }
                em.persist(domain);
                site = null;
            }
        }
        
        container.with(
            div(
                div("Indexing websites currently works via site-maps located via sitemap entries in robots.txt.")
                    .withClass("col-12")
            ).withClass("row")
        ).with(
            form().withMethod("GET").attr("accept-charset", "utf-8").with(
                input().withName("site")
                    .withPlaceholder("https://your.website.com (only domain, no pages/query parts, no port)")
                    .isRequired()
                    .attr("autofocus")
                    .withClass("col-12")
                    .withCondValue(site != null, site)
            ).withClass("row submitWebSiteForm")
        );

        htmlBuilder.mainAdd(container);

        return Response.ok(htmlBuilder.toString()).build();
    }
}
