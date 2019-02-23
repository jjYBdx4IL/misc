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
package com.github.jjYBdx4IL.web.markdown.publisher;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Visitor for markdown parser.
 *
 * @author jjYBdx4IL
 */
class MarkDownVisitor extends AbstractVisitor implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(MarkDownVisitor.class);
    
    private final String httpLoc;
    private final List<SyndEntry> entries = new ArrayList<>();
    private StringBuilder sb = new StringBuilder();
    private String blogTitle = null;
    private String entryTitle = null;

    MarkDownVisitor(String httpLoc) {
        this.httpLoc = httpLoc;
    }

    @Override
    public void visit(Text text) {
        LOG.info(text.getClass().getName());
        Node parent = text.getParent();
        if (parent != null && parent instanceof Heading) {
            Heading heading = (Heading) parent;
            if (heading.getLevel() == 1) {
                LOG.info("Blog title: " + text.getLiteral());
                blogTitle = text.getLiteral();
            }
            if (heading.getLevel() == 2) {
                LOG.info("RSS entry: " + text.getLiteral());
                if (entryTitle != null) {
                    appendBlogEntry();
                }
                entryTitle = text.getLiteral();
            }
        } else {
            sb.append(text.getLiteral());
        }

        visitChildren(text);
    }

    private void appendBlogEntry() {
        SyndEntry entry = new SyndEntryImpl();
        entry.setTitle(entryTitle);
        entry.setLink(httpLoc);
        //entry.setPublishedDate(DATE_PARSER.parse("2004-06-08"));
        entry.setPublishedDate(new Date());
        SyndContent description = new SyndContentImpl();
        description.setType("text/plain");
        description.setValue(sb.toString());
        entry.setDescription(description);
        entries.add(entry);

        sb = new StringBuilder();
    }

    @Override
    public void close() throws IOException {
        if (entryTitle != null) {
            appendBlogEntry();
        }
    }

    /**
     * Get blog title.
     * 
     * @return the blogTitle
     */
    String getBlogTitle() {
        return blogTitle;
    }

    /**
     * Get feed entries.
     * 
     * @return the entries
     */
    List<SyndEntry> getEntries() {
        return entries;
    }

}
