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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//CHECKSTYLE:OFF
/**
 * Tries to update simple frontmatters unobtrusively, ie. without reformatting
 * the entire frontmatter and by adding/replacing single lines.
 * 
 * Only supports simple key-value pairs ("key: value").
 * 
 * Line break format is auto detected from existing line breaks.
 * 
 * @author jjYBdx4IL
 */
@SuppressWarnings("serial")
public class UnobtrusiveFrontmatterUpdater {

    private static final Pattern LINE_PATTERN = Pattern.compile("^(.*?)$", Pattern.MULTILINE);
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("^([^\\s:][^:]*):(.*)$");
    
    private final String prefix;
    private final String suffix;

    public UnobtrusiveFrontmatterUpdater(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public UnobtrusiveFrontmatterUpdater() {
        this("---", "---");
    }

    /*
     * New data is added to the beginning of the frontmatter using the order
     * given.
     */
    public String update(String input, String... keyValuePairs) throws FormatException {
        Map<String, String> v = new HashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            v.put(keyValuePairs[i], keyValuePairs[i + 1]);
        }

        String lf = detectLinebreak(input);
        Pattern p = Pattern.compile("^" + prefix + lf + "(.*?" + lf + "|)" + suffix + lf + "(.*)$", Pattern.DOTALL);
        Matcher m = p.matcher(input);
        if (!m.find()) {
            throw new FormatException("failed to find frontmatter");
        }
        String fmContent = m.group(1);
        String rest = m.group(2);

        StringBuilder sb = new StringBuilder();

        // replace lines for matching keys
        m = LINE_PATTERN.matcher(fmContent);
        while (m.find()) {
            Matcher m2 = KEY_VALUE_PATTERN.matcher(m.group(1));
            if (m2.find()) {
                String key = m2.group(1).trim();
                if (v.containsKey(key)) {
                    sb.append(key);
                    sb.append(": ");
                    sb.append(v.get(key) == null ? "" : v.get(key));
                    v.remove(key);
                } else {
                    sb.append(m.group(0));
                }
            } else {
                sb.append(m.group(1));
            }
            sb.append(lf);
        }

        // prepend remaining
        StringBuilder sbPre = new StringBuilder();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            String key = keyValuePairs[i];
            if (v.containsKey(key)) {
                sbPre.append(key);
                sbPre.append(": ");
                sbPre.append(v.get(key) == null ? "" : v.get(key));
                sbPre.append(lf);
            }
        }

        return prefix + lf + sbPre.toString() + sb.toString() + suffix + lf + rest;
    }

    private String detectLinebreak(String input) throws FormatException {
        if (input.startsWith(prefix + "\r\n")) {
            return "\r\n";
        }
        if (input.startsWith(prefix + "\n")) {
            return "\n";
        }
        throw new FormatException("failed to determine linebreak format");
    }

    public class FormatException extends Exception {
        public FormatException(String s) {
            super(s);
        }
    }
}
