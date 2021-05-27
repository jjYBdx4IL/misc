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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

//CHECKSTYLE:OFF
public class StringUtil {

    public static String f(String pattern, Object... args) {
        return String.format(Locale.ROOT, pattern, args);
    }
    
    /**
     * String formatter for BASH '-quotes interpolation.
     */
    public static String sq(String input, Object... args) {
        StringBuilder sb = new StringBuilder(input.length() + args.length * 3);
        int i = 0;
        int cnt = 0;
        while (i < input.length()) {
            int j = input.indexOf("%", i);
            if (j == -1 || j == input.length() - 1) {
                sb.append(input.substring(i));
                i = input.length();
                continue;
            }
            char mod = input.charAt(j+1);
            if (mod == '%') {
                sb.append(input.substring(i, j+1));
                i = j+2;
                continue;
            }
            final Object o;
            String append = null;
            if (mod == 's' || mod == 'd' || mod == 'q') {
                if (cnt == args.length) {
                    throw new IllegalArgumentException("too many placeholders, not enough arguments");
                }
                o = args[cnt++];
            }
            else {
                throw new IllegalArgumentException("unsupported modifier: " + mod);
            }
            sb.append(input.substring(i, j));
            i = j+2;
            if (mod == 's') {
                if (o instanceof String) {
                    append = (String) o;
                }
            }
            else if (mod == 'd') {
                if (o instanceof Integer) {
                    append = Integer.toString((Integer)o);
                }
                else if (o instanceof Long) {
                    append = Long.toString((Long)o);
                }
            }
            else if (mod == 'q') {
                if (o instanceof String) {
                    sb.append("'");
                    sb.append(((String)o).replaceAll("'", "'\\\\''"));
                    append = "'";
                }
            }
            if (append == null) {
                throw new IllegalArgumentException("argument #" + (cnt-1) + " has wrong type: " + o.getClass());
            }
            sb.append(append);
        }
        if (cnt != args.length) {
            throw new IllegalArgumentException("more arguments than placeholders");
        }
        return sb.toString();
    }
    
    public static boolean haveEqualSets(Collection<String> a, Collection<String> b) {
        if (a == null || a.isEmpty()) {
            return b == null || b.isEmpty();
        }
        if (b == null || b.isEmpty()) {
            return a == null || a.isEmpty();
        }
        
        Set<String> c = new HashSet<>(b.size());
        Iterator<String> it = b.iterator();
        while(it.hasNext()) {
            c.add(it.next());
        }
        it = a.iterator();
        while(it.hasNext()) {
            if (!c.remove(it.next())) {
                return false;
            }
        }
        return c.isEmpty();
    }

    private StringUtil() {
    }
}
