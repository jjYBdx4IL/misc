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

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.activation.MimetypesFileTypeMap;

//CHECKSTYLE:OFF
public class MimeType {

    /**
     * You can use the internal map directly. It is initialized with an extended mime types
     * list, which is considered "programmatic" by {@link MimetypesFileTypeMap} and therefore
     * takes precedence over any unbundled resources. That, in turn, leads to more predictable
     * behavior.
     */
    public static final MimetypesFileTypeMap MAP = createMap();
    
    /**
     * 
     * @return a vastly improved mimetype map
     */
    public static MimetypesFileTypeMap createMap() {
        try (InputStream is = MimeType.class.getResourceAsStream("mimetypes.txt")) {
            return new MimetypesFileTypeMap(is);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Determine mime type based on file name extension.
     * 
     * @param fileName a file name including an extension
     * @return mime type for case-insensitive file name extension
     */
    public static String get(String fileName) {
        return get(fileName, null);
    }
    
    /**
     * Determine mime type based on file name extension.
     * 
     * @param fileName a file name including an extension
     * @param charset appended if mime type starts with "text/" or contains "javascript"
     * @return mime type for case-insensitive file name extension
     */
    public static String get(String fileName, String charset) {
        String mimeType = MAP.getContentType(fileName.toLowerCase(Locale.ROOT));
        if (charset != null && (mimeType.startsWith("text/") || mimeType.contains("javascript"))) {
            mimeType += ";charset=" + charset.toLowerCase();
        }
        return mimeType;
    }
}
