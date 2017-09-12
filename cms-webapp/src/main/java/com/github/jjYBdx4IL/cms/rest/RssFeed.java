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
import com.github.jjYBdx4IL.cms.jpa.dto.Article;
import com.github.jjYBdx4IL.cms.jpa.dto.ConfigKey;
import com.github.jjYBdx4IL.cms.rest.app.HtmlBuilder;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

//CHECKSTYLE:OFF
@Path("feed")
@PermitAll
@Transactional
public class RssFeed {

    public static final int MAX_ENTRIES = 10;
    
    private static final Logger LOG = LoggerFactory.getLogger(RssFeed.class);

    @Context
    UriInfo uriInfo;
    @Inject
    HtmlBuilder htmlBuilder;
    @Inject
    QueryFactory qf;

    @Path("rss.xml")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response feed() {
        LOG.trace("feed()");

        return Response.ok().entity(generateAtomFeed()).build();
    }

    private String generateAtomFeed() {
        List<Article> articles = qf.getArticleDisplayList(null, null).setMaxResults(MAX_ENTRIES).getResultList();

        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");
        feed.setEncoding("UTF-8");

        feed.setTitle(qf.getConfigValue(ConfigKey.WEBSITE_TITLE, ""));
        feed.setLink(uriInfo.getAbsolutePathBuilder().build().toString());
        feed.setStyleSheet("http://www.w3.org/2005/Atom");
        feed.setDescription(feed.getTitle());

        List<SyndEntry> entries = new ArrayList<>();

        for (Article article : articles) {
            SyndEntry entry = new SyndEntryImpl();
            entry.setTitle(article.getTitle());
            entry.setLink(htmlBuilder.constructArticleLink(article));
            entry.setPublishedDate(article.getCreatedAt());
            SyndContent description = new SyndContentImpl();
            description.setType("text/plain");
            description.setValue(article.getContent());
            entry.setDescription(description);
            entries.add(entry);
        }

        feed.setEntries(entries);

        try {
            return new SyndFeedOutput().outputString(feed);
        } catch (FeedException e) {
            throw new RuntimeException(e);
        }
    }

}
