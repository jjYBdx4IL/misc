package com.github.jjYBdx4IL.cms.rest;

import static j2html.TagCreator.a;
import static j2html.TagCreator.br;
import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.form;
import static j2html.TagCreator.input;
import static j2html.TagCreator.textarea;

import com.github.jjYBdx4IL.cms.jaxb.dto.ExportDump;
import com.github.jjYBdx4IL.cms.jpa.dto.Article;
import com.github.jjYBdx4IL.cms.jpa.dto.QueryFactory;
import com.github.jjYBdx4IL.cms.jpa.dto.Tag;
import com.github.jjYBdx4IL.cms.jpa.tx.Tx;
import com.github.jjYBdx4IL.cms.jpa.tx.TxRo;
import com.github.jjYBdx4IL.cms.rest.app.HtmlBuilder;
import com.github.jjYBdx4IL.cms.rest.app.SessionData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import j2html.tags.ContainerTag;

@Path("articleManager")
public class ArticleManager {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleManager.class);

    @Context
    UriInfo uriInfo;
    @Inject
    public EntityManager em;
    @Inject
    private SessionData session;
    @Inject
    private HtmlBuilder htmlBuilder;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @TxRo
    public String get() {
        LOG.trace("get()");

        List<Article> articles = QueryFactory.getArticleDisplayList(em, null).getResultList();

        UriBuilder urlTpl = uriInfo.getAbsolutePathBuilder().path(ArticleManager.class, "edit");

        ContainerTag articleListRow = div(
            each(articles,
                article -> a(
                    div(article.getTitle()).withClass("articleTitle")
                ).withHref(urlTpl.build(article.getId()).toString())
                    .withClass("col-12 article")
            )
        ).withClass("row");

        htmlBuilder.setPageTitle("Article Manager")
            .addPageTitleSubItem("add_box", "Create new", ArticleManager.class, "create")
            .mainAdd(
                div(articleListRow).withClass("container articleManager")
            );

        return htmlBuilder.toString();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @TxRo
    @Path("create")
    public String create() {
        LOG.trace("create()");

        ContainerTag formRow = articleEditForm(null);

        htmlBuilder.setPageTitle("Create New Article")
            .setJsValue("tagSearchApiEndpoint",
                uriInfo.getBaseUriBuilder().path(ArticleManager.class).path("tagSearch").build().toString()
            ).mainAdd(div(formRow).withClass("container articleManager"));

        return htmlBuilder.toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Tx
    @Path("create")
    public Response createSave(
        @FormParam("title") String title,
        @FormParam("content") String content,
        @FormParam("tags") String tagsValue) {
        LOG.trace("createSave()");

        if (title == null || title.isEmpty()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("title required").build();
        }
        if (content == null || content.isEmpty()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("content required").build();
        }

        Set<Tag> tags = parseTags(tagsValue);
        if (tags == null) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("bad tag name").build();
        }

        Article article = new Article();
        article.setTitle(title);
        article.setContent(content);
        article.setOwner(session.getUser());
        article.setCreatedAt(new Date());
        article.setLastModified(article.getCreatedAt());
        article.setTags(tags);
        em.persist(article);

        return Response.temporaryRedirect(uriInfo.getBaseUriBuilder().path(ArticleManager.class).build())
            .status(HttpServletResponse.SC_FOUND)
            .build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @TxRo
    @Path("edit/{articleId}")
    public Response edit(@PathParam("articleId") long articleId) {
        LOG.trace("edit()");

        Article article = em.find(Article.class, articleId);

        if (article == null) {
            return Response.status(HttpServletResponse.SC_NOT_FOUND).entity("not found").build();
        }

        if (article.getOwner().getGoogleUniqueId() == null
            || !article.getOwner().getGoogleUniqueId().equals(session.getUser().getGoogleUniqueId())) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("access denied").build();
        }

        ContainerTag formRow = articleEditForm(article);

        htmlBuilder.setPageTitle("Edit Article")
            .setJsValue("tagSearchApiEndpoint",
                uriInfo.getBaseUriBuilder().path(ArticleManager.class).path("tagSearch").build().toString()
            ).mainAdd(div(formRow).withClass("container articleManager"));

        return Response.ok().entity(htmlBuilder.toString())
            .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Tx
    @Path("edit/{articleId}")
    public Response editSave(
        @PathParam("articleId") long articleId,
        @FormParam("title") String title,
        @FormParam("content") String content,
        @FormParam("tags") String tagsValue) {
        LOG.trace("editSave()");

        if (title == null || title.isEmpty()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("title required").build();
        }
        if (content == null || content.isEmpty()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("content required").build();
        }

        Set<Tag> tags = parseTags(tagsValue);
        if (tags == null) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("bad tag name").build();
        }

        Article article = em.find(Article.class, articleId);

        if (!session.getUser().hasWriteAccessTo(article)) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("access denied").build();
        }

        article.setTitle(title);
        article.setContent(content);
        article.setLastModified(new Date());
        article.setTags(tags);
        em.persist(article);

        return Response.temporaryRedirect(uriInfo.getBaseUriBuilder().path(ArticleManager.class).build())
            .status(HttpServletResponse.SC_FOUND)
            .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @TxRo
    @Path("tagSearch")
    public Response tagSearch(@QueryParam("term") String term) {
        LOG.trace("tagSearch()");

        if (term == null) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("term param required").build();
        }

        List<Tag> tags = QueryFactory.getTags(em, term).getResultList();

        List<String> tagNames = new ArrayList<>();
        tags.forEach(tag -> tagNames.add(tag.getName()));

        return Response.ok().entity(tagNames).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    @TxRo
    @Path("export")
    public Response export() throws JAXBException {
        LOG.trace("export()");

        JAXBContext jaxbContext = JAXBContext.newInstance(ExportDump.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<Article> articles = QueryFactory.getArticleDisplayList(em, null).getResultList();
        jaxbMarshaller.marshal(new ExportDump(articles), baos);

        return Response.ok().entity(baos.toString()).build();
    }

    protected ContainerTag articleEditForm(Article article) {
        return div(
            form().withMethod("post").with(
                input().withName("title").withPlaceholder("title").isRequired()
                    .withValue(article != null ? article.getTitle() : ""),
                br(),
                textarea().withName("content").isRequired()
                    .withText(article != null ? article.getContent() : ""),
                br(),
                input().withName("tags").withId("tags").withPlaceholder("Tags")
                    .withValue(createTagsString(article)),
                br(),
                input().withType("submit").withName("submitButton").withValue("save")
            ).withClass("col-12 editForm")
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
            List<Tag> _tags = QueryFactory.getTag(em, tagName).getResultList();
            Tag tag;
            if (!_tags.isEmpty()) {
                tag = _tags.get(0);
            } else {
                tag = new Tag();
                tag.setName(tagName);
                tag.setDescription("");
            }
            tags.add(tag);
        }
        return tags;
    }

    protected String createTagsString(Article article) {
        if (article == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        article.getTags().forEach(tag -> sb.append(sb.length() != 0 ? ", " : "").append(tag.getName()));
        return sb.toString();
    }
}
