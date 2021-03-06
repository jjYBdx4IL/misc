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

import com.github.jjYBdx4IL.cms.jpa.dto.Article;
import com.github.jjYBdx4IL.cms.jpa.dto.Article_;
import com.github.jjYBdx4IL.cms.rest.app.HtmlBuilder;
import j2html.tags.ContainerTag;
import org.hibernate.search.exception.EmptyQueryException;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

//CHECKSTYLE:OFF
@Path("search")
@PermitAll
public class Search {

    private static final Logger LOG = LoggerFactory.getLogger(Search.class);

    @Context
    UriInfo uriInfo;
    @PersistenceContext
    private EntityManager em;
    @Inject
    private HtmlBuilder htmlBuilder;
    @Inject @Named("subdomain")
    String subdomain;

    private boolean revertedToFuzzyMatching = false;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response search(@QueryParam("q") String searchTerm) {
        LOG.trace("search()");

        htmlBuilder.setPageTitle("Search");
        htmlBuilder.enableNoIndex();
        htmlBuilder.enableShareButtons();
        ContainerTag container = div().withClass("container");

        container.with(
            form().withMethod("GET").attr("accept-charset", "utf-8").with(
                input().withName("q").withPlaceholder("Enter search term").isRequired()
                    .withCondValue(searchTerm != null, searchTerm)
                    .attr("autofocus")
                    .attr("title", "Wildcards like * and ? are supported. "
                        + "If nothing is found, a fuzzy search is performed.")
                    .withClass("col-12")
            ).withClass("row searchForm")
        );

        if (searchTerm != null) {
            List<Article> articles = doFullTextSearch(searchTerm);
            container.with(
                div(
                    div(String.format("%d match(es) found.", articles.size())).withClass("col-12 searchResultComment")
                ).withClass("row")
            );
            if (!articles.isEmpty()) {
                if (revertedToFuzzyMatching) {
                    container.with(
                        div(
                            div("No exact match found. Reverted to fuzzy matching.")
                                .withClass("col-12 searchResultComment")
                        ).withClass("row")
                    );
                }
                container.with(htmlBuilder.createArticleListRow(articles, false, false));
            }
        }

        htmlBuilder.mainAdd(container);
        return Response.ok(htmlBuilder.toString()).build();
    }

    // wildcard search with fallback to fuzzy search
    @SuppressWarnings("unchecked")
    public List<Article> doFullTextSearch(String term) {
        List<Article> articles = null;
        try {
            FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(em);

            QueryBuilder b = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(Article.class).get();

            org.apache.lucene.search.Query luceneQuery = b.keyword().wildcard()
                .onField(Article_.content.getName())
                .andField(Article_.title.getName()).boostedTo(3)
                .matching(term)
                .createQuery();
            Query fullTextQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, Article.class);

            articles = fullTextQuery.getResultList();

            // revert to fuzzy search if nothing has been found
            if (articles.isEmpty()) {
                revertedToFuzzyMatching = true;
                luceneQuery = b.keyword().fuzzy()
                    .onField(Article_.content.getName())
                    .andField(Article_.title.getName()).boostedTo(3)
                    .matching(term)
                    .createQuery();
                fullTextQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, Article.class);
                articles = fullTextQuery.getResultList();
            }
        } catch (EmptyQueryException ex) {
        }

        if (articles == null) {
            articles = new ArrayList<>();
        }

        List<Article> articlesPublished = new ArrayList<>();
        articles.forEach(article -> {
            if (article.isPublished() && article.getSubdomain().equals(subdomain)) {
                articlesPublished.add(article);
            }
        });

        return articlesPublished;
    }

}
