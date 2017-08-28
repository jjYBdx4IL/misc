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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Github jjYBdx4IL Projects
 */
public class WordUtils {

    public static String camelCase(String s) {
        Pattern p = Pattern.compile("[^a-zA-Z0-9]*([a-zA-Z0-9]+)");
        Matcher m = p.matcher(s);
        StringBuilder sb = new StringBuilder();
        int lastIndex = 0;
        boolean firstMatch = true;
        while (m.find()) {
            if (m.start() > lastIndex) {
                sb.append(s.substring(lastIndex, m.start()));
            }
            String match = m.group(0).replaceFirst("^[^a-zA-Z0-9]+", "");
            if (firstMatch) {
                sb.append(match.substring(0, 1).toLowerCase());
                sb.append(match.substring(1));
                firstMatch = false;
            } else {
                sb.append(match.substring(0, 1).toUpperCase());
                sb.append(match.substring(1));
            }
            lastIndex = m.end();
        }
        return sb.toString();
    }

}
