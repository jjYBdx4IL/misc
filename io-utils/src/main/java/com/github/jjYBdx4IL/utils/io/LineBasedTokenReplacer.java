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

//CHECKSTYLE:OFF
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

/**
 * Replaces tokens/placeholders line by line and helps avoiding to load the entire data into memory.
 * 
 * @author Github jjYBdx4IL Projects
 */
public class LineBasedTokenReplacer {

    public static final String DEFAULT_TOKEN_PREFIX = "%%";
    public static final String DEFAULT_TOKEN_SUFFIX = "%%";
    public static final String DEFAULT_LINE_BREAK = "\n";

    private final Pattern tokenPattern;
    private final String lineBreak;
    private final Map<String, String> placeholders = new HashMap<>();

    public LineBasedTokenReplacer() {
        this(DEFAULT_TOKEN_PREFIX, DEFAULT_TOKEN_SUFFIX, DEFAULT_LINE_BREAK);
    }

    public LineBasedTokenReplacer(String tokenPrefix, String tokenSuffix, String lineBreak) {
        if (tokenPrefix.length() < 1 || tokenSuffix.length() < 1) {
            throw new IllegalArgumentException();
        }
        String disallowedChar = tokenSuffix.substring(0, 1);
        if (disallowedChar.equals("]")) {
            disallowedChar = "\\" + disallowedChar;
        }
        String _tokenPrefix = tokenPrefix.replace("$", "\\$");
        String _tokenSuffix = tokenSuffix.replace("$", "\\$");
        tokenPattern = Pattern.compile(String.format("%s([^%s]+)%s", _tokenPrefix, disallowedChar, _tokenSuffix));
        this.lineBreak = lineBreak;
    }
    
    public void addTokenValue(String key, String value) {
        placeholders.put(key, value);
    }

    public void execute(InputStream is, OutputStream os, String encoding) throws IOException {
        Charset charset = Charset.forName(encoding);
        LineIterator it = IOUtils.lineIterator(is, charset);
        StringBuilder sb = null;
        int linePos = 0;
        while (it.hasNext()) {
            String line = it.next();
            Matcher m = tokenPattern.matcher(line);
            while (m.find()) {
                if (sb == null) {
                    sb = new StringBuilder(line.length()*2);
                }
                String key = m.group(1);
                String value = placeholders.get(key);
                if (value == null) {
                    throw new IllegalArgumentException(String.format("no value defined for %s", key));
                }
                sb.append(line.substring(linePos, m.start()));
                sb.append(value);
                linePos = m.end();
            }
            if (sb != null) {
                sb.append(line.substring(linePos, line.length()));
                os.write(sb.toString().getBytes(charset));
                sb = null;
                linePos = 0;
            } else {
                os.write(line.getBytes(charset));
            }
            os.write(lineBreak.getBytes(charset));
        }
    }
}
