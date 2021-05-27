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
package com.github.jjYBdx4IL.utils.io;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TplUtils {

    public static interface PlaceholderProcessor {
        String handlePlaceholder(String arg);
    }
    
    /**
     * Simple template processing method that tries to be simple, versatile and somewhat optimized.
     * 
     * <p>Example:
     * <pre>{@code
     * TplUtils.processPlaceholders("a${1}b}", "\\$\\{(.+?)}", new TplUtils.PlaceholderProcessor() {
     *       
     *       {@literal @}Override
     *       public String handlePlaceholder(String arg) {
     *           return arg.equals("1") ? "one" : null;
     *       }
     *   })
     * }</pre>
     * 
     * @param input the "template"
     * @param placeholderRegex a regex containing a single group ("()") that will be fed to pp.handlePlaceholder().
     * @param pp the PlaceholderProcessor
     * @return the processed template with all placeholders replaced
     */
    public static String processPlaceholders(String input, Pattern placeholderRegex, PlaceholderProcessor pp) {
        StringBuilder sb = new StringBuilder(input.length());
        int readWaterMark = 0;
        Matcher m = placeholderRegex.matcher(input);
        if (m.groupCount() != 1) {
            throw new IllegalArgumentException("placeholderRegex has group count != 1");
        }
        while (m.find()) {
            String arg = m.group(1);
            sb.append(input.substring(readWaterMark, m.start()));
            readWaterMark = m.end();
            sb.append(pp.handlePlaceholder(arg));
        }
        sb.append(input.substring(readWaterMark));
        return sb.toString();
    }
    
    public static String processPlaceholders(String input, String placeholderRegex, PlaceholderProcessor pp) {
        return processPlaceholders(input, Pattern.compile(placeholderRegex), pp);
    }

}
