package com.github.jjYBdx4IL.cms.rest.app;

import static j2html.TagCreator.div;

import j2html.tags.ContainerTag;
import j2html.tags.DomContent;

public class Grid {

    public static ContainerTag container() {
        return div().withClass("container");
    }

    public static ContainerTag container(ContainerTag... rows) {
        return div().withClass("container").with(rows);
    }

    public static ContainerTag row() {
        return div().withClass("row");
    }

    public static ContainerTag row(ContainerTag... cells) {
        return div().withClass("row").with(cells);
    }

    public static ContainerTag cell(int width) {
        return div().withClass("col-" + width);
    }

    public static ContainerTag cell(int width, String text) {
        return div().withClass("col-" + width).withText(text);
    }

    public static ContainerTag cell(int width, DomContent... dc) {
        return div().withClass("col-" + width).with(dc);
    }

    public static ContainerTag cell(String classes) {
        return div().withClass(classes);
    }

    public static ContainerTag cell(String classes, String text) {
        return div().withClass(classes).withText(text);
    }

    public static ContainerTag cell(String classes, DomContent... dc) {
        return div().withClass(classes).with(dc);
    }

}
