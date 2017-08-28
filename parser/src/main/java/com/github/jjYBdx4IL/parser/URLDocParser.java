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
package com.github.jjYBdx4IL.parser;

import org.apache.commons.io.IOUtils;

//CHECKSTYLE:OFF
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Generic class for parsing URL resources. 
 *
 * @author Github jjYBdx4IL Projects
 * @param <RESULT> a parse result type extending {@link ParseResult}
 */
public abstract class URLDocParser<RESULT extends ParseResult> {
    
    protected static final int DEFAULT_PATTERN_COMPILE_OPTIONS = Pattern.CASE_INSENSITIVE | Pattern.DOTALL;

    private final URL srcUrl;
    private final URL browseUrl;
    private String sourceDoc = null;
    private final Map<String, RESULT> results = new HashMap<>();

    public URLDocParser(String srcUrl, String browseUrl) {
        try {
            this.srcUrl = new URL(srcUrl);
            this.browseUrl = new URL(browseUrl);
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("deprecation")
    public URLDocParser<RESULT> fetch() throws IOException {
        InputStream is = null;
        try {
            is = srcUrl.openStream();
            setSourceDoc(IOUtils.toString(is));
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex2) {
                }
            }
        }
        return this;
    }

    public URLDocParser<RESULT> setSourceDoc(String sourceDoc) {
        results.clear();
        this.sourceDoc = sourceDoc;
        return this;
    }

    protected String getSourceDoc() {
        return this.sourceDoc;
    }

    protected void addResult(RESULT result) throws ParseException {
        if(results.containsKey(result.getId())) {
            throw new ParseException();
        }
        results.put(result.getId(), result);
    }

    public RESULT getResult(String id) {
        return results.get(id);
    }

    public String getSourceURL() {
        return srcUrl.toExternalForm();
    }

    public String getBrowseURL() {
        return browseUrl.toExternalForm();
    }

    public abstract URLDocParser<RESULT> parse() throws ParseException;

}
