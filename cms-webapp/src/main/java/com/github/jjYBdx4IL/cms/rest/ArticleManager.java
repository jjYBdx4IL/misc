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

import static j2html.TagCreator.a;
import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.form;
import static j2html.TagCreator.i;
import static j2html.TagCreator.input;
import static j2html.TagCreator.label;
import static j2html.TagCreator.text;
import static j2html.TagCreator.textarea;

import com.github.jjYBdx4IL.cms.jaxb.dto.ArticleDTO;
import com.github.jjYBdx4IL.cms.jaxb.dto.ExportDump;
import com.github.jjYBdx4IL.cms.jpa.QueryFactory;
import com.github.jjYBdx4IL.cms.jpa.dto.Article;
import com.github.jjYBdx4IL.cms.jpa.dto.ConfigValue;
import com.github.jjYBdx4IL.cms.jpa.dto.Tag;
import com.github.jjYBdx4IL.cms.jpa.dto.User;
import com.github.jjYBdx4IL.cms.rest.app.HtmlBuilder;
import com.github.jjYBdx4IL.cms.rest.app.Role;
import com.github.jjYBdx4IL.cms.rest.app.SessionData;
import j2html.tags.ContainerTag;
import j2html.tags.Text;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;

//CHECKSTYLE:OFF
@Path("articleManager")
@RolesAllowed(Role.ADMIN)
public class ArticleManager {

    public static final String MARKDOWN_HELP_LINK = "https://github.com/showdownjs/showdown/wiki/Showdown's-Markdown-syntax";

    private static final Logger LOG = LoggerFactory.getLogger(ArticleManager.class);

    @Context
    UriInfo uriInfo;
    @PersistenceContext
    EntityManager em;
    @Inject
    SessionData session;
    @Inject
    HtmlBuilder htmlBuilder;
    @Inject
    QueryFactory qf;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response get() {
        LOG.trace("get()");

        List<Article> articles = qf.getArticleDisplayList(null, session.getUid(), false).getResultList();

        UriBuilder urlTpl = uriInfo.getAbsolutePathBuilder().path(ArticleManager.class, "edit");

        ContainerTag articleListRow = div(
            each(articles,
                article -> div().withClass("col-12 article " + (article.isPublished() ? "published" : "notpublished"))
                    .with(
                        a(
                            div(article.getTitle()).withClass("articleTitle")
                        ).withHref(urlTpl.build(article.getId()).toString()))
                    .condWith(article.isPublished(),
                        a("open_in_new").withHref(htmlBuilder.constructArticleLink(article))
                            .withClass("material-icons open-article").withTarget("_extern")
                    )
                    .with(new Text(
                        article.isPublished()
                            ? String.format(" (published: %s)", HtmlBuilder.fmtDate(article.getFirstPublishedAt()))
                            : " (not published)"))

            )
        ).withClass("row");

        htmlBuilder.setPageTitle("Article Manager")
            .addPageTitleSubItem("add_box", "Create new", ArticleManager.class, "create")
            .mainAdd(
                div(articleListRow).withClass("container articleManager")
            );

        return Response.ok().entity(htmlBuilder.toString()).build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("create")
    public Response create() {
        LOG.trace("create()");

        ContainerTag formRow = articleEditForm(null);

        htmlBuilder.setPageTitle("Create New Article")
            .enableJsEditorSupport()
            .addPageTitleSubItem("help", "Syntax help", MARKDOWN_HELP_LINK)
            .setJsValue("tagSearchApiEndpoint",
                uriInfo.getBaseUriBuilder().path(ArticleManager.class).path("tagSearch").build().toString()
            ).mainAdd(div(formRow).withClass("container articleManager"));

        return Response.ok().entity(htmlBuilder.toString()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    @Path("create")
    public Response createSave(
        @FormParam("title") String title,
        @FormParam("pathId") String pathId,
        @FormParam("content") String content,
        @FormParam("processed") String processed,
        @FormParam("tags") String tagsValue,
        @FormParam("published") String _published) {
        LOG.trace("createSave()");

        boolean published = "on".equalsIgnoreCase(_published);

        if (title == null || title.isEmpty()) { // no sanitization of the title needed, j2html will do that on output
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("title required").build();
        }
        if (pathId == null || pathId.isEmpty() || !Article.PATHID_PATTERN.matcher(pathId).find()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("pathId empty or invalid").build();
        }
        if (content == null || content.isEmpty()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("content required").build();
        }
        if (processed == null || processed.isEmpty()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("processed required").build();
        }

        Set<Tag> tags = parseTags(tagsValue);
        if (tags == null) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("bad tag name").build();
        }

        content = content.replace("\r\n", "\n");

        Date now = new Date();
        Article article = new Article();
        article.setTitle(title);
        article.setPathId(pathId);
        article.setContent(content);
        article.setProcessed(sanitizeHtml(processed));
        article.setOwner(qf.getUserByUid(session.getUid()));
        article.setCreatedAt(now);
        article.setLastModified(now);
        article.setTags(tags);
        article.setPublished(published);
        article.setFirstPublishedAt(published ? now : null);
        em.persist(article);

        return Response.temporaryRedirect(uriInfo.getBaseUriBuilder().path(ArticleManager.class).build())
            .status(HttpServletResponse.SC_FOUND)
            .build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("edit/{articleId}")
    @Transactional
    public Response edit(@PathParam("articleId") long articleId) {
        LOG.trace("edit()");

        Article article = em.find(Article.class, articleId);

        if (article == null) {
            return Response.status(HttpServletResponse.SC_NOT_FOUND).entity("not found").build();
        }

        if (article.getOwner().getUid() == null
            || !article.getOwner().getUid().equals(session.getUid())) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("access denied").build();
        }

        ContainerTag formRow = articleEditForm(article);

        htmlBuilder.setPageTitle("Edit Article")
            .enableJsEditorSupport()
            .addPageTitleSubItem("help", "Syntax help", MARKDOWN_HELP_LINK)
            .setJsValue("tagSearchApiEndpoint",
                uriInfo.getBaseUriBuilder().path(ArticleManager.class).path("tagSearch").build().toString()
            ).mainAdd(div(formRow).withClass("container articleManager"));

        return Response.ok().entity(htmlBuilder.toString())
            .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    @Path("edit/{articleId}")
    public Response editSave(
        @PathParam("articleId") long articleId,
        @FormParam("title") String title,
        @FormParam("pathId") String pathId,
        @FormParam("content") String content,
        @FormParam("processed") String processed,
        @FormParam("tags") String tagsValue,
        @FormParam("published") String _published) {
        LOG.trace("editSave()");

        boolean published = "on".equalsIgnoreCase(_published);

        if (title == null || title.isEmpty()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("title required").build();
        }
        if (pathId == null || pathId.isEmpty() || !Article.PATHID_PATTERN.matcher(pathId).find()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("pathId empty or invalid").build();
        }
        if (content == null || content.isEmpty()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("content required").build();
        }
        if (processed == null || processed.isEmpty()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("processed required").build();
        }

        Set<Tag> tags = parseTags(tagsValue);
        if (tags == null) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("bad tag name").build();
        }

        Article article = em.find(Article.class, articleId);

        if (!article.getOwner().getUid().equals(session.getUid())) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("access denied").build();
        }

        content = content.replace("\r\n", "\n");

        Date now = new Date();
        article.setTitle(title);
        // article.setPathId(pathId); // the pathId should be constant and never
        // modified
        String oldContent = article.getContent().replace("\r\n", "\n");
        article.setContent(content);
        article.setProcessed(sanitizeHtml(processed));
        if (!content.equals(oldContent)) {
            article.setLastModified(now);
        }
        article.setTags(tags);
        article.setPublished(published);
        if (published && article.getFirstPublishedAt() == null) {
            article.setFirstPublishedAt(now);
        }
        em.persist(article);

        return Response.temporaryRedirect(uriInfo.getBaseUriBuilder().path(ArticleManager.class).build())
            .status(HttpServletResponse.SC_FOUND)
            .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("tagSearch")
    public Response tagSearch(@QueryParam("term") String term) {
        LOG.trace("tagSearch()");

        if (term == null) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("term param required").build();
        }

        List<Tag> tags = qf.getTags(term).getResultList();

        List<String> tagNames = new ArrayList<>();
        tags.forEach(tag -> tagNames.add(tag.getName()));

        return Response.ok().entity(tagNames).build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.TEXT_XML })
    @Path("export")
    public Response exportDump() throws JAXBException {
        LOG.trace("export()");

        List<Article> articles = qf.getArticleDisplayList(null, session.getUid(), false).getResultList();
        List<ConfigValue> configValues = qf.getAllConfigValues().getResultList();

        return Response.ok().header("Content-Disposition",
            "attachment; filename=\"export.xml\"").entity(ExportDump.create(articles, configValues)).build();
    }

    @POST
    @Consumes({ MediaType.APPLICATION_XML, MediaType.TEXT_XML })
    @Transactional
    @Path("import")
    public Response importDump(ExportDump dump) throws JAXBException {
        LOG.trace("import()");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Article> cd = cb.createCriteriaDelete(Article.class);
        cd.from(Article.class);
        em.createQuery(cd).executeUpdate();

        Map<String, Tag> tagMap = new HashMap<>();
        qf.getTags(null).getResultList().forEach(tag -> tagMap.put(tag.getId(), tag));

        User user = qf.getUserByUid(session.getUid());

        for (ArticleDTO dto : dump.getArticles()) {
            Article article = new Article();
            article.setTitle(dto.getTitle());
            article.setPathId(dto.getPathId());
            article.setContent(dto.getContent());
            article.setProcessed(dto.getProcessed());
            article.setCreatedAt(dto.getCreatedAt());
            article.setLastModified(dto.getLastModified());
            article.setOwner(user);
            article.setPublished(dto.isPublished());
            article.setFirstPublishedAt(dto.getFirstPublishedAt());
            List<Tag> tags = new ArrayList<>();
            for (String tagName : dto.getTags()) {
                String tagId = tagName.toLowerCase();
                if (tagMap.containsKey(tagId)) {
                    tags.add(tagMap.get(tagId));
                } else {
                    Tag newTag = new Tag(tagName);
                    tags.add(newTag);
                    tagMap.put(tagId, newTag);
                }
            }
            article.setTags(tags);
            em.persist(article);
        }

        return Response.noContent().build();
    }

    protected ContainerTag articleEditForm(Article article) {
        return div(
            form().withMethod("post").attr("accept-charset", "utf-8").with(
                input().withName("title").withPlaceholder("title").isRequired()
                    .withValue(article != null ? article.getTitle() : "").withClass("col-12"),
                input().withName("pathId").withPlaceholder("path id").isRequired()
                    .condAttr(article != null, "readonly", "")
                    .withValue(article != null ? article.getPathId() : "").withClass("col-12"),
                textarea().withName("content").isRequired()
                    .withText(article != null ? article.getContent() : "").withClass("col-6"),
                div().withId("mdPreview").withClass("col-5 articleContent"),
                input().withName("processed").withType("hidden"),
                input().withName("tags").withId("tags").withPlaceholder("Tags")
                    .withValue(createTagsString(article)).withClass("col-12"),
                label(input().withName("published").withType("checkbox")
                    .condAttr(article == null || article.isPublished(), "checked", "checked"),
                    text("published")).withClass("col-12"),
                input().withType("submit").withName("submitButton").withValue("save").withClass("col-12")
                ).withClass("editForm")
            ).withClass("row");
    }

    protected Set<Tag> parseTags(String tagsValue) {
        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagsValue.split("[\\s,]+")) {
            if (tagName.isEmpty()) {
                continue;
            }
            if (!Tag.NAME_PATTERN.matcher(tagName).find()) {
                return null;
            }
            List<Tag> _tags = qf.getTag(tagName).getResultList();
            Tag tag;
            if (!_tags.isEmpty()) {
                tag = _tags.get(0);
            } else {
                tag = new Tag();
                tag.setName(tagName);
            }
            tags.add(tag);
        }
        return tags;
    }

    protected String createTagsString(Article article) {
        if (article == null) {
            return "";
        }
        List<String> tags = new ArrayList<>(article.getTags().size());
        article.getTags().forEach(tag -> tags.add(tag.getName()));
        Collections.sort(tags, new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });
        StringBuilder sb = new StringBuilder(tags.size() * 10);
        tags.forEach(tag -> sb.append(sb.length() != 0 ? ", " : "").append(tag));
        return sb.toString();
    }

    protected String sanitizeHtml(String untrustedHTML) {
        PolicyFactory policy = new HtmlPolicyBuilder().allowElements("pre").allowAttributes("class").onElements("pre")
            .toFactory();
        policy = policy.and(Sanitizers.FORMATTING).and(Sanitizers.LINKS).and(Sanitizers.IMAGES).and(Sanitizers.STYLES)
            .and(Sanitizers.TABLES).and(Sanitizers.BLOCKS);
        return policy.sanitize(untrustedHTML);
    }

}
