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
package com.github.jjYBdx4IL.parser.maven.enforcer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionComparator {

    private static final Pattern COMPONENT_PATTERN = Pattern.compile("([0-9]+)", Pattern.CASE_INSENSITIVE);

    /**
     * Compares simple version numbers like "1.2.3" and "1.2".
     * 
     * @param one
     *            the first version string
     * @param two
     *            the second version string
     * @return true if the first version string is larger than the second
     */
    public static boolean largerThan(String one, String two) {
        // split the version strings into number and non-number parts:
        String[] oneParts = split(one);
        String[] twoParts = split(two);

        for (int i = 0; i < Math.min(oneParts.length, twoParts.length); i++) {
            // even index numbers are assumed to contain numbers:
            int onePart = Integer.parseInt(oneParts[i]);
            int twoPart = Integer.parseInt(twoParts[i]);
            if (onePart > twoPart) {
                return true;
            }
            if (twoPart > onePart) {
                return false;
            }
        }

        return one.length() > two.length();
    }

    private static String[] split(String version) {
        List<String> parts = new ArrayList<>();
        Matcher matcher = COMPONENT_PATTERN.matcher(version);
        while (matcher.find()) {
            parts.add(matcher.group(1));
        }
        return parts.toArray(new String[parts.size()]);
    }
}
