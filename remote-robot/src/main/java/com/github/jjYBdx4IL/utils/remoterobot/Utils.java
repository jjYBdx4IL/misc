/*
 * Copyright © 2019 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.utils.remoterobot;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class Utils {

    private Utils() {
    }

    /**
     * Delay method that does not expect to get interrupted, and throws a
     * RuntimeException if that happens.
     * 
     * @param ms
     *            the delay
     */
    public static void delay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    static void writeTo(ObjectOutputStream oos, Object o) throws IOException {
        oos.writeUnshared(o);
        oos.flush(); // force sending over the write
        oos.reset(); // clear serialization cache
    }

}
