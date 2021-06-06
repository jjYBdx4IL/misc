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
package com.github.jjYBdx4IL.utils.lang;

public class ArrayUtils {

    /**
     * Equivalent to: {@link #indexOf(byte[], byte[])} != -1.
     */
    public static boolean contains(byte[] haystack, byte[] needle) {
        return indexOf(haystack, needle) != -1;
    }
    
    /**
     * Search for the first byte array sub match in haystack and return its offset.
     * 
     * @return -1 if no match was found or if needle length is zero 
     */
    public static int indexOf(byte[] haystack, byte[] needle) {
        if (needle.length == 0) {
            return -1;
        }
        for (int i = 0; i < haystack.length + 1 - needle.length; i++) {
            if (isPartEqual(haystack, i, needle)) {
                return i;
            }
        }
        return -1;
    }

    private static boolean isPartEqual(byte[] a, int ai, byte[] b) {
        for (int i = 0; i < b.length; i++) {
            if (a[ai + i] != b[i]) {
                return false;
            }
        }
        return true;
    }
    
    private ArrayUtils() {
    }

}