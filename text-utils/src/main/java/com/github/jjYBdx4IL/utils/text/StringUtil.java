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
