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
package com.github.jjYBdx4IL.utils.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public class JsoupTools {

    public static final int MAX_DATASRC_ATTR_LENGTH = 30;
    public static final String OVERLENGTH_INDICATOR = "...";

    private JsoupTools() {
    }
    
    /**
     *
     * @param html the html
     * @param truncateStuff if true, will truncate content of //head/style and src attr of img[src^=data] down
     * to 30 characters
     * @return the formatted html string
     */
    public static String prettyFormatHtml(String html, boolean truncateStuff) {
        Document doc = Jsoup.parse(html);
        if (truncateStuff) {
            Elements dataSrcs = doc.select("img[src^=data:]");
            for (Element dataSrc : dataSrcs) {
                if (dataSrc.attr("src").length() <= MAX_DATASRC_ATTR_LENGTH) {
                    continue;
                }
                dataSrc.attr("src", dataSrc.attr("src")
                        .substring(0, MAX_DATASRC_ATTR_LENGTH - OVERLENGTH_INDICATOR.length())
                        + OVERLENGTH_INDICATOR);
            }
            Elements headStyles = doc.select("head style");
            for (Element content : headStyles) {
                if (content.data().length() <= MAX_DATASRC_ATTR_LENGTH) {
                    continue;
                }
                content.text(content.data()
                        .substring(0, MAX_DATASRC_ATTR_LENGTH - OVERLENGTH_INDICATOR.length())
                        + OVERLENGTH_INDICATOR);
            }
        }
        doc.outputSettings().prettyPrint(true);
        doc.outputSettings().indentAmount(4);
        return doc.toString();
    }

    public static List<String> extractLinks(byte[] htmldata, String charsetName, String baseUri) throws IOException {
        List<String> links = new ArrayList<>();
        Document doc;
        try (InputStream is = new ByteArrayInputStream(htmldata)){
            doc = Jsoup.parse(is, charsetName, baseUri);
        }
        for (Element aHref : doc.select("a[href]")) {
            String link = aHref.absUrl("href");
            if (link == null) {
                continue;
            }
            if (!link.toLowerCase().startsWith("http://") && !link.toLowerCase().startsWith("https://")) {
                continue;
            }
            links.add(link);
        }
        return links;
    }
}
