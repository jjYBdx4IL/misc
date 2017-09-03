package com.github.jjYBdx4IL.cms.rest.app;

import static j2html.TagCreator.body;
import static j2html.TagCreator.document;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.link;
import static j2html.TagCreator.meta;
import static j2html.TagCreator.title;

import j2html.tags.Tag;

public class HtmlUtils {
    
    public static String htmlDoc(String title, Tag<?>... bodyTags) {
        return document(
            html(
                head(
                    meta().attr("http-equiv", "Content-Type").attr("content", "text/html;charset=UTF-8"),
                    link().withRel("stylesheet").withType("text/css").withHref("assets/style.css"),
                    title(title)),
                body(
                    bodyTags)));
    }

   
}
