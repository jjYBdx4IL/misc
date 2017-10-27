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
package com.github.jjYBdx4IL.utils.markdown;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.commons.text.StringEscapeUtils;

//CHECKSTYLE:OFF
/**
 * Markdown to html convertor that allows editing of source parts.
 * 
 * <p>
 * This class allows to edit markdown source of rendered html in-place:
 * <ul>
 * <li>md to html conversion, embedding of markdown source positions in html tag
 * attributes
 * <li>source positions can be used to retrieve markdown source parts for
 * rendered html
 * <li>source positions can be used to update source parts of rendered html
 * </ul>
 */
public class MyMD {

    public static final String EOL = "\n";

    public static String html(String input) {
        checkNotNull(input);
        StringBuilder sb = new StringBuilder(input.length());
        StringBuilder buf = new StringBuilder();
        int startLine = -1;
        State state = State.DEFAULT;
        String[] lines = input.split("\r?\n");
        for (int lineNum = 0; lineNum <= lines.length; lineNum++) {
            String l = lineNum < lines.length ? lines[lineNum] : "";
            switch (state) {
                case DEFAULT:
                    if (l.equals("```")) {
                        state = State.PRE;
                        startLine = lineNum;
                        buf.setLength(0);
                    } else if (l.startsWith("    ")) {
                        state = State.IPRE;
                        startLine = lineNum;
                        buf.setLength(0);
                        buf.append(l.substring(4));
                    } else if (l.startsWith("* ")) {
                        state = State.UL;
                        startLine = lineNum;
                        buf.setLength(0);
                        buf.append("<li>");
                        buf.append(StringEscapeUtils.escapeHtml4(l.substring(2).trim()));
                        buf.append("</li>");
                    } else if (l.startsWith("> ")) {
                        sb.append(String.format("<blockquote srcLines=\"%d-%d\">", lineNum, lineNum));
                        sb.append(format(l.substring(2).trim()));
                        sb.append("</blockquote>");
                    } else if (l.startsWith("# ")) {
                        sb.append(String.format("<h1 srcLines=\"%d-%d\">", lineNum, lineNum));
                        sb.append(format(l.substring(2).trim()));
                        sb.append("</h1>");
                    } else if (l.startsWith("## ")) {
                        sb.append(String.format("<h2 srcLines=\"%d-%d\">", lineNum, lineNum));
                        sb.append(format(l.substring(3).trim()));
                        sb.append("</h2>");
                    } else if (l.startsWith("### ")) {
                        sb.append(String.format("<h3 srcLines=\"%d-%d\">", lineNum, lineNum));
                        sb.append(format(l.substring(4).trim()));
                        sb.append("</h3>");
                    } else if (l.startsWith("#### ")) {
                        sb.append(String.format("<h4 srcLines=\"%d-%d\">", lineNum, lineNum));
                        sb.append(format(l.substring(5).trim()));
                        sb.append("</h4>");
                    } else if (l.startsWith("##### ")) {
                        sb.append(String.format("<h5 srcLines=\"%d-%d\">", lineNum, lineNum));
                        sb.append(format(l.substring(6).trim()));
                        sb.append("</h5>");
                    } else if (l.startsWith("###### ")) {
                        sb.append(String.format("<h6 srcLines=\"%d-%d\">", lineNum, lineNum));
                        sb.append(format(l.substring(7).trim()));
                        sb.append("</h6>");
                    } else if (!l.isEmpty()) {
                        sb.append(String.format("<p srcLines=\"%d-%d\">", lineNum, lineNum));
                        sb.append(format(l.trim()));
                        sb.append("</p>");
                    }
                    break;
                case PRE:
                    if (l.equals("```")) {
                        state = State.DEFAULT;
                        sb.append(String.format("<pre srcLines=\"%d-%d\">%s</pre>", startLine, lineNum,
                            StringEscapeUtils.escapeHtml4(buf.toString())));
                    } else {
                        if (buf.length() > 0) {
                            buf.append(EOL);
                        }
                        buf.append(l);
                    }
                    break;
                case IPRE:
                    if (l.startsWith("    ")) {
                        buf.append(EOL);
                        buf.append(l.substring(4));
                    } else {
                        state = State.DEFAULT;
                        lineNum--;
                        sb.append(String.format("<pre srcLines=\"%d-%d\">%s</pre>", startLine, lineNum,
                            StringEscapeUtils.escapeHtml4(buf.toString())));
                    }
                    break;
                case UL:
                    if (l.startsWith("* ")) {
                        buf.append("<li>");
                        buf.append(StringEscapeUtils.escapeHtml4(l.substring(2).trim()));
                        buf.append("</li>");
                    } else {
                        state = State.DEFAULT;
                        lineNum--;
                        sb.append(String.format("<ul srcLines=\"%d-%d\">%s</ul>", startLine, lineNum, buf.toString()));
                    }
                    break;
                default:
            }
        }
        return sb.toString();
    }

    private static String format(String input) {
        input = StringEscapeUtils.escapeHtml4(input);
        input = input.replaceAll("\\[([^]]+)\\]\\(([^\")]+)\\)", "<a href=\"$2\">$1</a>");
        input = input.replaceAll("\\*\\*\\*(.*?)\\*\\*\\*", "<b><i>$1</i></b>");
        input = input.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");
        input = input.replaceAll("\\*(.*?)\\*", "<i>$1</i>");
        input = input.replaceAll("_(.*?)_", "<u>$1</u>");
        input = input.replaceAll("`(.*?)`", "<code>$1</code>");
        return input;
    }

    private static enum State {
        DEFAULT, PRE, IPRE, UL;
    }

}
