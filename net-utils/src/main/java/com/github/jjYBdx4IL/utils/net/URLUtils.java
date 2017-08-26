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
package com.github.jjYBdx4IL.utils.net;

//CHECKSTYLE:OFF
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class URLUtils {
// NOTES:   1) \w includes 0-9, a-z, A-Z, _
//          2) The leading '-' is the '-' character. It must go first in character class expression

    private static final String VALID_CHARS = "-\\w+&@#/%=~()|";
    private static final String VALID_NON_TERMINAL = "?!:,.;";

// Notes on the expression:
//  1) Any number of leading '(' (left parenthesis) accepted.  Will be dealt with.
//  2) s? ==> the s is optional so either [http, https] accepted as scheme
//  3) All valid chars accepted and then one or more
//  4) Case insensitive so that the scheme can be hTtPs (for example) if desired
    private static final Pattern URI_FINDER_PATTERN = Pattern.compile("\\(*https?://[" + VALID_CHARS + VALID_NON_TERMINAL + "]*[" + VALID_CHARS + "]", Pattern.CASE_INSENSITIVE);

    /**
     * Finds all "URL"s in the given _rawText, wraps them in HTML link tags and returns the result (with the
     * rest of the text html encoded).
     * 
     * <p>
     * We employ the procedure described at:
     * <a
     * href="http://www.codinghorror.com/blog/2008/10/the-problem-with-urls.html">http://www.codinghorror.com/blog/2008/10/the-problem-with-urls.html</a>
     * which is a <b>must-read</b>.</p>
     * 
     * <p>
     * Basically, we allow any number of left parenthesis (which will get stripped away) followed by http://
     * or https://. Then any number of permitted URL characters (based on <a
     * href="http://www.ietf.org/rfc/rfc1738.txt">http://www.ietf.org/rfc/rfc1738.txt</a>) followed by a
     * single character of that set (basically, those minus typical punctuation). We remove all sets of
     * matching left &amp; right parentheses which surround the URL.</p>
     * 
     * <p>
     * This method *must* be called from a tag/component which will NOT end up escaping the output. For
     * example:</p>
     * 
     * <pre>{@code
     * <h:outputText ... escape="false" value="#{core:hyperlinkText(textThatMayHaveURLs, '_blank')}"/>
     * }</pre>
     * 
     * <p>
     * Reason: we are adding <code>&lt;a href="..."&gt;</code> tags to the output *and* encoding the rest of
     * the string. So, encoding the outupt will result in double-encoding data which was already encoded - and
     * encoding the <code>a href</code> (which will render it useless).</p>
     * 
     * <p>
     * <a href="http://stackoverflow.com/a/9602832/1050755">Source @ stackoverflow.com</a></p>
     *
     * @param _rawText - if <code>null</code>, returns <code>""</code> (empty string).
     * @param _target - if not <code>null</code> or <code>""</code>, adds a target attributed to the generated
     * link, using _target as the attribute value.
     * @return the modified input text
     */
    public static final String hyperlinkText(final String _rawText, final String _target) {

        String returnValue = null;

        if (!StringUtils.isBlank(_rawText)) {

            final Matcher matcher = URI_FINDER_PATTERN.matcher(_rawText);

            if (matcher.find()) {

                final int originalLength = _rawText.length();

                final String targetText = (StringUtils.isBlank(_target)) ? "" : " target=\"" + _target.trim() + "\"";
                final int targetLength = targetText.length();

                // Counted 15 characters aside from the target + 2 of the URL (max if the whole string is URL)
                // Rough guess, but should keep us from expanding the Builder too many times.
                final StringBuilder returnBuffer = new StringBuilder(originalLength * 2 + targetLength + 15);

                int currentStart;
                int currentEnd;
                int lastEnd = 0;

                String currentURL;

                do {
                    currentStart = matcher.start();
                    currentEnd = matcher.end();
                    currentURL = matcher.group();

                    // Adjust for URLs wrapped in ()'s ... move start/end markers
                    //      and substring the _rawText for new URL value.
                    while (currentURL.startsWith("(") && currentURL.endsWith(")")) {
                        currentStart = currentStart + 1;
                        currentEnd = currentEnd - 1;

                        currentURL = _rawText.substring(currentStart, currentEnd);
                    }

                    while (currentURL.startsWith("(")) {
                        currentStart = currentStart + 1;

                        currentURL = _rawText.substring(currentStart, currentEnd);
                    }

                    // Text since last match
                    returnBuffer.append(StringEscapeUtils.escapeHtml(_rawText.substring(lastEnd, currentStart)));

                    // Wrap matched URL
                    returnBuffer.append("<a href=\"" + currentURL + "\"" + targetText + ">" + currentURL + "</a>");

                    lastEnd = currentEnd;

                } while (matcher.find());

                if (lastEnd < originalLength) {
                    returnBuffer.append(StringEscapeUtils.escapeHtml(_rawText.substring(lastEnd)));
                }

                returnValue = returnBuffer.toString();
            }
        }

        if (returnValue == null) {
            returnValue = StringEscapeUtils.escapeHtml(_rawText);
        }

        return returnValue;

    }

    /**
     * Based on {@link #hyperlinkText(java.lang.String, java.lang.String)} and adjusted to return a list of
     * all detected URLs (http and https).
     *
     * @param _rawText the text to parse
     * @return list of detected URLs
     */
    public static final List<String> hyperlinkUrls(final String _rawText) {
        List<String> returnValue = new ArrayList<>();

        if (StringUtils.isBlank(_rawText)) {
            return returnValue;
        }

        final Matcher matcher = URI_FINDER_PATTERN.matcher(_rawText);

        if (matcher.find()) {
            int currentStart;
            int currentEnd;

            String currentURL;

            do {
                currentStart = matcher.start();
                currentEnd = matcher.end();
                currentURL = matcher.group();

                // Adjust for URLs wrapped in ()'s ... move start/end markers
                //      and substring the _rawText for new URL value.
                while (currentURL.startsWith("(") && currentURL.endsWith(")")) {
                    currentStart = currentStart + 1;
                    currentEnd = currentEnd - 1;

                    currentURL = _rawText.substring(currentStart, currentEnd);
                }

                while (currentURL.startsWith("(")) {
                    currentStart = currentStart + 1;

                    currentURL = _rawText.substring(currentStart, currentEnd);
                }

                returnValue.add(currentURL);
            } while (matcher.find());
        }

        return returnValue;
    }

    /**
     * Beware, silently overwrites duplicate parameters and there is no guarantee in which order it is done.
     *
     * Convenience wrapper for {@link URLEncodedUtils#parse(URI,String)}.
     *
     * Kept here for backwards compatbility. There are likely better methods available in apache http packages.
     *
     * @param url the url string to decode
     * @param charset the charset to use
     * @return the map of url parameters
     * @throws URISyntaxException thrown if the url is not properly formatted
     */
    public static Map<String, String> readParamsIntoMap(String url, String charset) throws URISyntaxException {
        Map<String, String> params = new HashMap<>();

        @SuppressWarnings("deprecation")
        List<NameValuePair> result = URLEncodedUtils.parse(new URI(url), charset);

        for (NameValuePair nvp : result) {
            params.put(nvp.getName(), nvp.getValue());
        }

        return params;
    }

    /**
     * Kept here for backwards compatbility. There are likely better methods available in apache http packages.
     * 
     * @param url the url string to decode
     * @return the query params map
     * @throws UnsupportedEncodingException if UTF-8 is not supported on your platform
     */
    public static Map<String, List<String>> getQueryParams(String url) throws UnsupportedEncodingException {
        Map<String, List<String>> params = new HashMap<>();
        String[] urlParts = url.split("\\?");
        if (urlParts.length < 2) {
            return params;
        }

        String query = urlParts[1];
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            String key = URLDecoder.decode(pair[0], "UTF-8");
            String value = "";
            if (pair.length > 1) {
                value = URLDecoder.decode(pair[1], "UTF-8");
            }

            // skip ?& and &&
            if ("".equals(key) && pair.length == 1) {
                continue;
            }

            List<String> values = params.get(key);
            if (values == null) {
                values = new ArrayList<>();
                params.put(key, values);
            }
            values.add(value);
        }

        return params;
    }
    
    private URLUtils() {
    }

}
