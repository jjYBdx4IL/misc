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
package com.github.jjYBdx4IL.utils.text;

//CHECKSTYLE:OFF
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class Snippets {

    public final static String DEFAULT_SNIPPET_NAME = "default";

    /**
     * Simpler variant of {@link #extract(String, String, String)} that assumes snippet of the form:
     * 
     * <pre>{@code
     * ---START: snippet name ---
     * Snippet content text.
     * ---END---
     * }</pre>
     * 
     * or
     * 
     * <pre>{@code
     * ---START: snippet name ---
     * Snippet content text.
     * ---END: some footer text ---
     * }</pre>
     * 
     * @param input the text containing the snippet(s) to extract
     * @return the map of snippets, ie. snippet name =&gt; snippet text
     */
    public static Map<String, String> extract(String input) {
        return extract(input,
                "^---\\s*START(?:|:\\s*(\\S(?:|.*\\S)))\\s*---\\s*$",
                "^---\\s*END(?:|:\\s*(\\S(?:|.*\\S)))\\s*---\\s*$");
    }

    /**
     * Extracts the text parts between a start and an end tag contained within a larger input text piece.
     * Process the input line by line and the returned snippets have their EOL transformed into the current
     * OS's EOL type.
     *
     * @param input the text containing the snippet(s) to extract
     * @param startRegex a regular expression matching the snippet header. If it contains a match group, the first one
     * will be used as the snippet's name, otherwise the snipper's name will be set to {@link #DEFAULT_SNIPPET_NAME}.
     * @param endRegex a regular expression matching the snippet footer
     * @return the map of snippets, ie. snippet name =&gt; snippet text
     */
    public static Map<String, String> extract(String input, String startRegex, String endRegex) {
        Map<String, String> snippets = new HashMap<>();
        Pattern startPattern = Pattern.compile(startRegex);
        Pattern endPattern = Pattern.compile(endRegex);
        StringBuilder buf = new StringBuilder();
        String currentSnippetName = null;
        for (String line : input.split("\r?\n")) {
            if (currentSnippetName == null) {
                Matcher m = startPattern.matcher(line);
                if (!m.find()) {
                    continue;
                }
                currentSnippetName = m.groupCount() > 0 && m.group(1) != null && m.group().length() > 0
                        ? m.group(1) : DEFAULT_SNIPPET_NAME;
                
            } else {
                Matcher m = endPattern.matcher(line);
                if (m.find()) {
                    snippets.put(currentSnippetName, buf.toString());
                    buf = new StringBuilder();
                    currentSnippetName = null;
                    continue;
                }
                buf.append(line).append(System.lineSeparator());
            }
        }
        return snippets;
    }
}
