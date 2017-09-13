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

import static j2html.TagCreator.a;
import static j2html.TagCreator.body;
import static j2html.TagCreator.div;
import static j2html.TagCreator.document;
import static j2html.TagCreator.each;
import static j2html.TagCreator.footer;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.h3;
import static j2html.TagCreator.head;
import static j2html.TagCreator.header;
import static j2html.TagCreator.html;
import static j2html.TagCreator.i;
import static j2html.TagCreator.link;
import static j2html.TagCreator.main;
import static j2html.TagCreator.meta;
import static j2html.TagCreator.script;
import static j2html.TagCreator.span;
import static j2html.TagCreator.title;

import com.github.jjYBdx4IL.cms.jpa.QueryFactory;
import com.github.jjYBdx4IL.cms.jpa.dto.Article;
import com.github.jjYBdx4IL.cms.jpa.dto.ConfigKey;
import com.github.jjYBdx4IL.cms.jpa.dto.Tag;
import com.github.jjYBdx4IL.cms.rest.ArticleManager;
import com.github.jjYBdx4IL.cms.rest.Home;
import com.github.jjYBdx4IL.cms.rest.LoginSelect;
import com.github.jjYBdx4IL.cms.rest.Logout;
import com.github.jjYBdx4IL.cms.rest.RssFeed;
import com.github.jjYBdx4IL.cms.rest.Search;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import j2html.tags.UnescapedText;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

//CHECKSTYLE:OFF
@RequestScoped
@Provider
public class HtmlBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(HtmlBuilder.class);

    public static final int META_KEYWORDS_MAX_LEN = 255;

    @Context
    UriInfo uriInfo;
    @Inject
    SessionData session;
    @Inject
    QueryFactory qf;

    private String pageTitle = "";
    private String lang = "en";
    private String description = null;
    private String author = null;
    private String signInLink = null;
    private String signOutLink = null;
    private boolean noIndex = false;
    private final List<String> cssUrls = new ArrayList<>();
    private final List<String> scriptUrls = new ArrayList<>();
    private final List<DomContent> mainContent = new ArrayList<>();
    private final ContainerTag _footer = footer();
    private final List<ContainerTag> pageTitleRowSubItems = new ArrayList<>();
    private final Map<String, String> jsValues = new HashMap<>();
    private StringBuilder metaKeywords = null;

    public HtmlBuilder() {
    }

    public HtmlBuilder addMetaKeywords(Article article) {
        for (Tag tag : article.getTags()) {
            addMetaKeyword(tag.getName());
        }
        return this;
    }

    public HtmlBuilder addMetaKeyword(String keyword) {
        if (metaKeywords == null) {
            metaKeywords = new StringBuilder();
        } else if (metaKeywords.length() >= META_KEYWORDS_MAX_LEN) {
            return this;
        }
        if (metaKeywords.length() > 0) {
            metaKeywords.append(",");
        }
        metaKeywords.append(keyword);
        return this;
    }

    public void enableNoIndex() {
        noIndex = true;
    }

    public ContainerTag getFooter() {
        return _footer;
    }

    public void addCssUrl(String url) {
        cssUrls.add(url);
    }

    public void addScriptUrl(String url) {
        scriptUrls.add(url);
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getSignInLink() {
        return signInLink;
    }

    public void setSignInLink(String signInLink) {
        this.signInLink = signInLink;
    }

    public String getSignOutLink() {
        return signOutLink;
    }

    public void setSignOutLink(String signOutLink) {
        this.signOutLink = signOutLink;
    }

    public HtmlBuilder setJsValue(String varName, String value) {
        jsValues.put(varName, value);
        return this;
    }

    public String toString() {
        String baseUri = uriInfo.getBaseUriBuilder().build().toString();

        setJsValue("assetsUri", baseUri + "assets/");
        setJsValue("privacyPolicyUri", uriInfo.getBaseUriBuilder().path(Home.class).path(Home.class, "byTag")
            .build("site-privacy-policy").toString());
        if (session.isDevel()) {
            addCssUrl(baseUri + "assets/simplegrid.css");
            addCssUrl(baseUri + "assets/style.css");
        } else {
            addCssUrl(baseUri + "assets/site.min.css");
        }
        // spinning icons: https://www.w3schools.com/w3css/w3css_icons.asp
        // <!-- https://material.io/icons/ -->
        addCssUrl("//fonts.googleapis.com/icon?family=Material+Icons");
        addCssUrl("//ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/themes/smoothness/jquery-ui.min.css");
        addScriptUrl("//ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js");
        addScriptUrl("//ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.js");
        addScriptUrl("//cdn.rawgit.com/showdownjs/showdown/1.7.4/dist/showdown.min.js");
        addScriptUrl("//cdnjs.cloudflare.com/ajax/libs/dompurify/1.0.2/purify.min.js");
        if (session.isDevel()) {
            addScriptUrl(baseUri + "assets/scripts.js");
        } else {
            addScriptUrl(baseUri + "assets/site.min.js");
        }

        if (session.isAuthenticated()) {
            setSignOutLink(uriInfo.getBaseUriBuilder().path(Logout.class).build().toString());
        } else {
            setSignInLink(uriInfo.getBaseUriBuilder().path(LoginSelect.class).build().toString());
        }

        String searchLink = uriInfo.getBaseUriBuilder().path(Search.class).build().toString();

        String signoutTooltipText = "Sign out." +
            (session.isAuthenticated() ? "\nCurrently signed in as:\n" + qf.getUserByUid(session.getUid()).getEmail()
                : "");

        ContainerTag menu = null;
        if (session.isAuthenticated()) {
            menu = div(
                div(
                    iconTextLink("col-6", "view_list", "Article Manager", ArticleManager.class),
                    div("Menu item 2").withClass("col-6"),
                    div("Menu item 3").withClass("col-12")
                    ).withClass("row")
                ).withClass("container menu");
        }

        ContainerTag _main = constructMainSection();

        String title = qf.getConfigValue(ConfigKey.WEBSITE_TITLE, "");
        String headFragment = qf.getConfigValue(ConfigKey.HTML_HEAD_FRAGMENT, "");

        String doc = document(
            html(
                head(
                    title(title),
                    description != null ? meta().attr("description", description) : null,
                    author != null ? meta().attr("author", author) : null,
                    noIndex ? meta().attr("name", "robots").attr("content", "noindex") : null,
                    meta().attr("http-equiv", "Content-Type").attr("content", "text/html;charset=UTF-8"),
                    meta().attr("name", "viewport").attr("content", "width=device-width, initial-scale=1"),
                    meta().attr("name", "keywords")
                        .attr("content", metaKeywords != null ? metaKeywords.toString() : ""),
                    createJsValuesScript(),
                    new UnescapedText(headFragment),
                    each(
                        cssUrls,
                        stylesheet -> link().withRel("stylesheet").withType("text/css").withHref(stylesheet)),
                    each(
                        scriptUrls, script -> script().withType("text/javascript").withSrc(script))
                    ),
                link().withRel("alternate").withType("application/rss+xml").withTitle("Blog")
                    .withHref(uriInfo.getBaseUriBuilder().path(RssFeed.class).path(RssFeed.class, "feed").build()
                        .toString()),
                body(
                    header(
                        div(
                            div(
                                h3(a(title).withHref(baseUri)).withClass("col-8-sm"),
                                div().with(
                                    a("search").withHref(searchLink).withClass("material-icons")
                                    ).condWith(session.isAuthenticated(),
                                        i("menu").withClass("menuIcon material-icons").attr("title", "Menu")
                                    ).condWith(signOutLink != null,
                                        a("exit_to_app").withHref(signOutLink).withClass("signout material-icons")
                                            .attr("title", signoutTooltipText)
                                    ).condWith(signInLink != null,
                                        a("vpn_key").withHref(signInLink).withClass("material-icons")
                                            .attr("title", "Sign in")
                                    ).withClass("col-4-sm right")
                                ).withClass("row")
                            ).withClass("container titlebar")
                        ).condWith(menu != null, menu),
                    _main, _footer
                    )
                ).attr("lang", lang)
            );
        if (LOG.isTraceEnabled()) {
            LOG.trace("doc: " + doc);
        }
        return doc;
    }

    public HtmlBuilder mainAdd(DomContent... dc) {
        for (DomContent _dc : dc) {
            mainContent.add(_dc);
        }
        return this;
    }

    public ContainerTag iconTextLink(String cssClass, String materialIconId, String text, Class<?> resource,
        String method) {
        return iconTextLink(
            cssClass, materialIconId, text,
            uriInfo.getBaseUriBuilder().path(resource).path(method).build().toString()
            );
    }

    public ContainerTag iconTextLink(String cssClass, String materialIconId, String text, Class<?> resource) {
        return iconTextLink(cssClass, materialIconId, text,
            uriInfo.getBaseUriBuilder().path(resource).build().toString());
    }

    public ContainerTag iconTextLink(String cssClass, String materialIconId, String text, String href) {
        ContainerTag result = a(
            i(materialIconId).withClass("material-icons")
            ).condWith(text != null, span(text)).withHref(href)
                .withClass((cssClass != null ? cssClass + " " : "") + "iconTextLink");
        String baseUri = uriInfo.getBaseUriBuilder().build().toString();
        if (!href.toLowerCase().startsWith(baseUri.toLowerCase())) {
            result.withTarget("_extern");
        }
        return result;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public HtmlBuilder setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle != null ? pageTitle : "";
        return this;
    }

    public HtmlBuilder addPageTitleSubItem(String materialIconId, String text, Class<?> resource, String method) {
        pageTitleRowSubItems.add(
            iconTextLink(null, materialIconId, text, resource, method)
            );
        return this;
    }

    public HtmlBuilder addPageTitleSubItem(String materialIconId, String text, Class<?> resource) {
        pageTitleRowSubItems.add(
            iconTextLink(null, materialIconId, text, resource)
            );
        return this;
    }

    public HtmlBuilder addPageTitleSubItem(String materialIconId, String text, String href) {
        pageTitleRowSubItems.add(
            iconTextLink(null, materialIconId, text, href)
            );
        return this;
    }

    private ContainerTag constructMainSection() {
        ContainerTag _main = main();
        if (!pageTitle.isEmpty() || !pageTitleRowSubItems.isEmpty()) {
            String colClass = (pageTitle.isEmpty() || pageTitleRowSubItems.isEmpty())
                ? "col-12"
                : "col-6";
            ContainerTag pageTitleRow = div().withClass("row pageTitleRow");
            pageTitleRow.condWith(!pageTitle.isEmpty(), h3(pageTitle).withClass(colClass + " pageTitle"));
            if (!pageTitleRowSubItems.isEmpty()) {
                ContainerTag subItems = div(
                    pageTitleRowSubItems.toArray(new ContainerTag[pageTitleRowSubItems.size()])
                    ).withClass(colClass + " pageTitleSubItems");
                pageTitleRow.with(subItems);
            }
            _main.with(div(pageTitleRow).withClass("container pageTitleContainer"));
        }
        for (DomContent dc : mainContent) {
            _main.with(dc);
        }
        return _main;
    }

    private ContainerTag createJsValuesScript() {
        if (jsValues.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        jsValues.forEach((k, v) -> sb.append("    var ").append(k).append(" = '")
            .append(StringEscapeUtils.escapeEcmaScript(v)).append("';\n")
        );
        return script().withType("text/javascript")
            .with(new UnescapedText(StringEscapeUtils.escapeHtml4(sb.toString())));
    }

    public ContainerTag createArticleListRow(List<Article> articles, boolean showDates, boolean showEditLink) {
        articles.forEach(article -> addMetaKeywords(article));
        UriBuilder uriBuilder = uriInfo.getBaseUriBuilder().path(Home.class, "byTag");
        return div(
            each(articles,
                article -> div(
                    h1(a(article.getTitle()).withHref(constructArticleLink(article))).withClass("articleTitle"),
                    div(createDateInfo(article), createEditLink(article)).withClass("articleMeta"),
                    div(article.getContent()).withClass("articleContent markdown"),
                    span("Tags: ").withClass("tagLineHeader"),
                    each(article.getTags(),
                        tag -> a(tag.getName()).withHref(uriBuilder.build(tag.getName()).toString()).withClass("tag")
                    )
                    ).withClass("col-12 article")
            )
            ).withClass("row");
    }

    private ContainerTag createDateInfo(Article article) {
        StringBuilder sb = new StringBuilder();
        sb.append("Created: ");
        sb.append(fmtDate(article.getCreatedAt()));
        if (!article.getCreatedAt().equals(article.getLastModified())) {
            sb.append(" / Updated: ");
            sb.append(fmtDate(article.getLastModified()));
        }
        return span(sb.toString());
    }

    private String fmtDate(Date date) {
        LocalDateTime ldt = LocalDateTime.ofEpochSecond(date.getTime() / 1000L, 0, ZoneOffset.UTC);
        return ldt.format(DateTimeFormatter.ISO_DATE);
    }

    private ContainerTag createEditLink(Article article) {
        if (!session.isAuthenticated()) {
            return null;
        }
        if (!session.getUid().equals(article.getOwner().getUid())) {
            return null;
        }
        String link = uriInfo.getBaseUriBuilder().path(ArticleManager.class).path(ArticleManager.class, "edit")
            .build(article.getId()).toString();
        return iconTextLink("articleTitleAffix", "edit_mode", null, link).attr("title", "Edit article");
    }

    public String constructArticleLink(Article article) {
        LocalDateTime ldt = LocalDateTime.ofEpochSecond(article.getCreatedAt().getTime() / 1000L, 0, ZoneOffset.UTC);
        return uriInfo.getBaseUriBuilder().path(Home.class, "byPathId")
            .build(ldt.getYear(), String.format("%02d", ldt.getMonthValue()),
                String.format("%02d", ldt.getDayOfMonth()), article.getPathId())
            .toString();
    }

}
