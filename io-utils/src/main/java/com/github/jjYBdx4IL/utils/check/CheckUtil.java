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
package com.github.jjYBdx4IL.utils.check;

public class CheckUtil {

    /**
     * Check not null.
     *  
     * @throws IllegalArgumentException if o is null
     */
    public static void checkNotNull(Object o) {
        if (o == null) {
            throw new IllegalArgumentException();
        }
    }
    
    /**
     * Check null.
     *  
     * @throws IllegalArgumentException if o is not null
     */
    public static void checkNull(Object o) {
        if (o != null) {
            throw new IllegalArgumentException();
        }
    }
    
    /**
     * Check true.
     *  
     * @throws IllegalArgumentException if b is false
     */
    public static void checkArgument(boolean b) {
        if (!b) {
            throw new IllegalArgumentException();
        }
    }
    
    private CheckUtil() {
    }
}
