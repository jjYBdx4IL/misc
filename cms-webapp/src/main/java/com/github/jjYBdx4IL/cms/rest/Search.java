package com.github.jjYBdx4IL.cms.rest;

import static j2html.TagCreator.div;
import static j2html.TagCreator.form;
import static j2html.TagCreator.input;

import com.github.jjYBdx4IL.cms.jpa.dto.Article;
import com.github.jjYBdx4IL.cms.jpa.dto.Article_;
import com.github.jjYBdx4IL.cms.jpa.tx.TxRo;
import com.github.jjYBdx4IL.cms.rest.app.HtmlBuilder;

import org.hibernate.search.exception.EmptyQueryException;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import j2html.tags.ContainerTag;

@Path("search")
public class Search {

    private static final Logger LOG = LoggerFactory.getLogger(Search.class);

    @Context
    UriInfo uriInfo;
    @Inject
    public EntityManager em;
    @Inject
    private HtmlBuilder htmlBuilder;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @TxRo
    public Response search(@QueryParam("q") String searchTerm) {
        LOG.trace("search()");

        htmlBuilder.setPageTitle("Search");
        htmlBuilder.enableNoIndex();
        ContainerTag container = div().withClass("container");

        container.with(
            form().withMethod("GET").with(
                input().withName("q").withPlaceholder("Enter search term").isRequired()
                    .withCondValue(searchTerm != null, searchTerm)
                    .attr("autofocus")
                    .withClass("col-12")
                    .attr("title", "Wildcards like * and ? are supported. "
                        + "If nothing is found, a fuzzy search is performed.")
            ).withClass("row searchForm")
        );

        if (searchTerm != null) {
            List<Article> articles = doFullTextSearch(searchTerm);
            if (articles.isEmpty()) {
                container.with(
                    div(
                        div("nothing found").withClass("col-12 searchWithoutResults")
                    ).withClass("row")
                );
            } else {
                container.with(htmlBuilder.createArticleListRow(articles));
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

        return articles;
    }
}
