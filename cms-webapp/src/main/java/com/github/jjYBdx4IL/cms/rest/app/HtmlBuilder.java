package com.github.jjYBdx4IL.cms.rest.app;

import static j2html.TagCreator.a;
import static j2html.TagCreator.body;
import static j2html.TagCreator.div;
import static j2html.TagCreator.document;
import static j2html.TagCreator.each;
import static j2html.TagCreator.footer;
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

import com.github.jjYBdx4IL.cms.rest.ArticleManager;
import com.github.jjYBdx4IL.cms.rest.Home;
import com.github.jjYBdx4IL.cms.rest.LoginSelect;
import com.github.jjYBdx4IL.cms.rest.Logout;

import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import j2html.tags.UnescapedText;

public class HtmlBuilder {

    private UriInfo uriInfo = null;
    private String title = null;
    private String pageTitle = "";
    private String lang = "en";
    private String description = null;
    private String author = null;
    private String signInLink = null;
    private String signOutLink = null;
    private String homeLink = null;
    private SessionData session = null;
    private final List<String> cssUrls = new ArrayList<>();
    private final List<String> scriptUrls = new ArrayList<>();
    private final List<DomContent> mainContent = new ArrayList<>();
    private final ContainerTag _footer = footer();
    private final List<ContainerTag> pageTitleRowSubItems = new ArrayList<>();
    private final Map<String, String> jsValues = new HashMap<>();

    private HtmlBuilder() {
    }

    public static HtmlBuilder createDefault() {
        HtmlBuilder htmlBuilder = new HtmlBuilder();

        return htmlBuilder;
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

    public String getTitle() {
        return title;
    }

    public HtmlBuilder setTitle(String title) {
        this.title = title;
        return this;
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
        addCssUrl(baseUri + "assets/style.css");
        addCssUrl(baseUri + "assets/simplegrid.css");
        // spinning icons: https://www.w3schools.com/w3css/w3css_icons.asp
        // <!-- https://material.io/icons/ -->
        addCssUrl("https://fonts.googleapis.com/icon?family=Material+Icons");
        addCssUrl("https://ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/themes/smoothness/jquery-ui.css");
        addScriptUrl("http://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js");
        addScriptUrl("https://ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.js");
        addScriptUrl(baseUri + "assets/header.js");
        addScriptUrl(baseUri + "assets/tag-autocomplete.js");

        setTitle("Page Title");

        if (session.isAuthenticated()) {
            setSignOutLink(uriInfo.getBaseUriBuilder().path(Logout.class).build().toString());
        } else {
            setSignInLink(uriInfo.getBaseUriBuilder().path(LoginSelect.class).build().toString());
        }

        String signoutTooltipText = "Sign out." +
            (session.isAuthenticated() ? "\nCurrently signed in as:\n" + session.getUser().getEmail() : "");

        ContainerTag menu = null;
        if (session.isAuthenticated()) {
            menu = Grid.container(
                Grid.row(
                    iconTextLink("col-6", "view_list", "Article Manager", ArticleManager.class),
                    Grid.cell(6, "Menu item 2"),
                    Grid.cell(12, "Menu item 3")
                    )
                ).withClass("container menu");
        }

        ContainerTag _main = constructMainSection();

        return document(
            html(
                head(
                    title != null ? title(title) : null,
                    description != null ? meta().attr("description", description) : null,
                    author != null ? meta().attr("author", author) : null,
                    meta().attr("http-equiv", "Content-Type").attr("content", "text/html;charset=UTF-8"),
                    meta().attr("name", "viewport").attr("content", "width=device-width, initial-scale=1"),
                    createJsValuesScript(),
                    each(
                        cssUrls,
                        stylesheet -> link().withRel("stylesheet").withType("text/css").withHref(stylesheet)),
                    each(
                        scriptUrls, script -> script().withType("text/javascript").withSrc(script))
                    ),
                body(
                    header(
                        div(
                            div(
                                h3(a(title).withHref(homeLink)).withClass("col-8-sm"),
                                div().condWith(signInLink != null,
                                    a("sign in").withHref(signInLink).withClass("signin")
                                ).condWith(session.isAuthenticated(),
                                    i("menu").withClass("menuIcon material-icons").attr("title", "Menu")
                                ).condWith(signOutLink != null,
                                    a("exit_to_app").withHref(signOutLink).withClass("signout material-icons")
                                        .attr("title", signoutTooltipText)
                                ).withClass("col-4-sm right")
                                ).withClass("row")
                            ).withClass("container titlebar")
                        ).condWith(menu != null, menu),
                    _main, _footer
                    )
                ).attr("lang", lang)
            );
    }

    public HtmlBuilder mainAdd(DomContent... dc) {
        for (DomContent _dc : dc) {
            mainContent.add(_dc);
        }
        return this;
    }

    public String getHomeLink() {
        return homeLink;
    }

    public void setHomeLink(String homeLink) {
        this.homeLink = homeLink;
    }

    public SessionData getSession() {
        return session;
    }

    public HtmlBuilder setSession(SessionData session) {
        this.session = session;
        return this;
    }

    public HtmlBuilder setUriInfo(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
        setHomeLink(uriInfo.getBaseUriBuilder().path(Home.class).build().toString());
        return this;
    }

    public ContainerTag iconTextLink(String cssClass, String materialIconId, String text, Class<?> resource,
        String method) {
        return iconTextLink(
            cssClass, materialIconId, text,
            uriInfo.getBaseUriBuilder().path(resource).path(method).toString()
            );
    }

    public ContainerTag iconTextLink(String cssClass, String materialIconId, String text, Class<?> resource) {
        return iconTextLink(cssClass, materialIconId, text, uriInfo.getBaseUriBuilder().path(resource).toString());
    }

    public ContainerTag iconTextLink(String cssClass, String materialIconId, String text, String href) {
        return a(
            i(materialIconId).withClass("material-icons"), span(text)
            ).withHref(href).withClass((cssClass != null ? cssClass + " " : "") + "iconTextLink");
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
}
