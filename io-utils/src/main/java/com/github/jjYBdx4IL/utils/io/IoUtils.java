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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

//CHECKSTYLE:OFF
public class IoUtils {

    private IoUtils() {
    }

    /**
     * Read some input stream into a byte array, requiring a maximum length.
     * 
     * @param is the input stream
     * @param maxBytes max number of bytes to read
     * @return null if the input stream was longer than maxBytes
     * @throws IOException on I/O error
     */
    public static byte[] toByteArray(InputStream is, long maxBytes) throws IOException {
        checkNotNull(is);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        do {
            n = is.read(buf);
            if (n > 0) {
                baos.write(buf, 0, n);
            }
        } while (baos.size() < maxBytes && n > 0);
        if (baos.size() > maxBytes) {
            return null;
        }
        return baos.toByteArray();
    }
}
