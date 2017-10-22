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
import static j2html.TagCreator.style;
import static j2html.TagCreator.title;

import com.github.jjYBdx4IL.cms.Env;
import com.github.jjYBdx4IL.cms.jpa.AppCache;
import com.github.jjYBdx4IL.cms.jpa.QueryFactory;
import com.github.jjYBdx4IL.cms.jpa.dto.ConfigKey;
import com.github.jjYBdx4IL.cms.rest.Administration;
import com.github.jjYBdx4IL.cms.rest.Feedback;
import com.github.jjYBdx4IL.cms.rest.LoginSelect;
import com.github.jjYBdx4IL.cms.rest.Logout;
import com.github.jjYBdx4IL.cms.rest.Settings;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import j2html.tags.UnescapedText;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
    @Inject
    AppCache appCache;

    private String pageTitle = ""; // shown on top of html -> body -> main
    private String headTitlePrefix = ""; // prefix for html -> head -> title
    private String lang = "en";
    private String description = null;
    private String author = null;
    private String signInLink = null;
    private String signOutLink = null;
    private boolean noIndex = false;
    private final List<String> cssUrls = new ArrayList<>();
    private final List<String> scriptUrls = new ArrayList<>();
    private final List<DomContent> mainContent = new ArrayList<>();
    private final List<DomContent> headContent = new ArrayList<>();
    private final ContainerTag _footer = footer();
    private final List<ContainerTag> pageTitleRowSubItems = new ArrayList<>();
    private final Map<String, String> jsValues = new HashMap<>();
    private StringBuilder metaKeywords = null;
    private final List<String> headFragments = new ArrayList<>();

    public HtmlBuilder() {
    }

    public HtmlBuilder addHeadContent(DomContent dom) {
        headContent.add(dom);
        return this;
    }

    public HtmlBuilder addHeadFragment(String fragment) {
        if (!fragment.isEmpty()) {
            headFragments.add(fragment);
        }
        return this;
    }

    public HtmlBuilder addHeadFragment(URL resource) {
        try (InputStream is = resource.openStream()) {
            return addHeadFragment(IOUtils.toString(resource, "UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        setJsValue("privacyPolicyUri", "https://gruust.stream/byTag/site-privacy-policy");
        if (Boolean.parseBoolean(appCache.get(ConfigKey.ENABLE_ADBLOCK_BLOCKER))) {
            setJsValue("enableAdblockBlocker", "true");
        }
        if (Env.isDevel()) {
            setJsValue("isDevel", "true");
        }
        String cookieConsentMessage = appCache.get(ConfigKey.COOKIE_CONSENT_MESSAGE);
        if (!cookieConsentMessage.isEmpty()) {
            setJsValue("cookieConsentMessage", cookieConsentMessage);
            addCssUrl("//cdnjs.cloudflare.com/ajax/libs/cookieconsent2/3.0.4/cookieconsent.min.css");
        }
        
        if (Env.isDevel()) {
            addCssUrl(baseUri + "assets/simplegrid.css");
            addCssUrl(baseUri + "assets/style.css");
        } else {
            addCssUrl(baseUri + "assets/site.min.css");
        }
        addCssUrl("//fonts.googleapis.com/icon?family=Material+Icons");
        addCssUrl("//ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/themes/smoothness/jquery-ui.min.css");
        addHeadContent(style(new UnescapedText(
            "@import url('https://fonts.googleapis.com/css?family=Montserrat');" + 
            "font-family:'Montserrat',Georgia,\"Times New Roman\",Times,serif;"
        )));

        if (session.isAuthenticated()) {
            setSignOutLink(uriInfo.getBaseUriBuilder().path(Logout.class).build().toString());
        } else {
            setSignInLink(uriInfo.getBaseUriBuilder().path(LoginSelect.class).build().toString());
        }

        String signoutTooltipText = "Sign out." +
            (session.isAuthenticated() ? "\nCurrently signed in as:\n" + qf.getUserByUid(session.getUid()).getEmail()
                : "");

        String aboutLink = "https://gruust.stream/byTag/site-geegee-about";
        String privacyPolicyLink = "https://gruust.stream/byTag/site-privacy-policy";
        String impressumLink = "https://gruust.stream/byTag/impressum";

        /* build the menu */
        ContainerTag menuRow = div().withClass("row");
        ContainerTag menu = div(menuRow).withClass("container menu");

        if (session.isAuthenticated()) {
            menuRow.with(iconTextLink("col-6", "settings", "Settings", Settings.class));
            menuRow.with(iconTextLink("col-6", "settings_applications", "Administration", Administration.class));
        }

        menuRow.with(iconTextLink("col-6", "info", "Feedback", Feedback.class));
        menuRow.with(iconTextLink("col-6", "info", "About", aboutLink));
        menuRow.with(iconTextLink("col-6", "info", "Privacy Policy", privacyPolicyLink));
        menuRow.with(iconTextLink("col-6", "info", "Impressum", impressumLink));

        ContainerTag _main = constructMainSection();

        String title = appCache.get(ConfigKey.WEBSITE_TITLE);
        addHeadFragment(appCache.get(ConfigKey.HTML_HEAD_FRAGMENT));
        String footFragment = appCache.get(ConfigKey.HTML_FOOT_FRAGMENT);

        _footer.condWith(footFragment != null && !footFragment.isEmpty(),
            new UnescapedText(footFragment));

        _footer.with(div(
            div(
                div(
                    a("About").withHref(aboutLink),
                    span(" - "),
                    a("Privacy Policy").withHref(privacyPolicyLink),
                    span(" - "),
                    a("Impressum").withHref(impressumLink)
                ).withClass("col-12 impressum")
            ).withClass("row")
        ).withClass("container")
        );

        String doc = document(
            html(
                head(
                    title(getHeadTitlePrefix()  + title),
                    description != null ? meta().attr("description", description) : null,
                    author != null ? meta().attr("author", author) : null,
                    noIndex ? meta().attr("name", "robots").attr("content", "noindex,noarchive")
                        : meta().attr("name", "robots").attr("content", "noarchive"),
                    meta().attr("http-equiv", "Content-Type").attr("content", "text/html;charset=UTF-8"),
                    meta().attr("name", "viewport").attr("content", "width=device-width, initial-scale=1"),
                    meta().attr("name", "keywords")
                        .attr("content", metaKeywords != null ? metaKeywords.toString() : ""),
                    createJsValuesScript(),
                    script().withSrc("//cdnjs.cloudflare.com/ajax/libs/require.js/2.3.5/require.min.js")
                        .attr("async").attr("defer").attr("data-main", baseUri + "assets/app")
                        .withType("text/javascript"),
                    each(headFragments,
                        headFragment -> new UnescapedText(headFragment)
                    ),
                    each(headContent, dom -> dom),
                    each(cssUrls,
                        stylesheet -> link().withRel("stylesheet").withType("text/css").withHref(stylesheet)),
                    each(scriptUrls, script -> script().withType("text/javascript").withSrc(script).attr("async")
                        .attr("defer"))
                ),
                body(
                    header(
                        div(
                            div(
                                h1(a(title).withHref(baseUri)).withClass("col-6-sm"),
                                div().with(
                                    i("menu").withClass("menuIcon material-icons").attr("title", "Menu")
                                ).condWith(signOutLink != null,
                                    a("exit_to_app").withHref(signOutLink).withClass("signout material-icons")
                                        .attr("title", signoutTooltipText)
                                ).condWith(signInLink != null,
                                    a("vpn_key").withHref(signInLink).withClass("material-icons")
                                        .attr("title", "Sign in")
                                ).withClass("col-6-sm right")
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
        jsValues.forEach((k, v) -> sb.append("var ").append(k).append(" = '")
            .append(StringEscapeUtils.escapeEcmaScript(v)).append("';")
        );
        return script().withType("text/javascript")
            .with(new UnescapedText(StringEscapeUtils.escapeHtml4(sb.toString())));
    }

    public static String fmtDate(Date date) {
        LocalDateTime ldt = LocalDateTime.ofEpochSecond(date.getTime() / 1000L, 0, ZoneOffset.UTC);
        return ldt.format(DateTimeFormatter.ISO_DATE);
    }

    public HtmlBuilder enableJsEditorSupport() {
        setJsValue("enableJsEditorSupport", "true");
        return this;
    }

    public HtmlBuilder enableShareButtons() {
        setJsValue("enableShareButtons", "true");
        return this;
    }

    public HtmlBuilder enableJsAminSupport() {
        setJsValue("enableJsAdminSupport", "true");
        return this;
    }

    public String getHeadTitlePrefix() {
        return headTitlePrefix;
    }

    public void setHeadTitlePrefix(String headTitlePrefix) {
        this.headTitlePrefix = headTitlePrefix + " - ";
    }

}
