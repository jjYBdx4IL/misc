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

import java.util.ArrayList;
import java.util.List;

//CHECKSTYLE:OFF
/**
 * This tokenizer was written to handle quotes properly, ie.
 * to recognize <code>-&quot;a b&quot;</code> as a single token
 * <code>-a b</code>.
 *
 */
public class QuoteTokenizer {

    private boolean failOnUnmatchedQuotes = false;

    public QuoteTokenizer() {
    }

    public List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder();
        int idx = 0;
        while (idx < input.length()) {
            int doubleQuoteIdx = input.indexOf("\"", idx);
            int singleQuoteIdx = input.indexOf("'", idx);
            int spaceIdx = input.indexOf(" ", idx);
            int quoteIdx = doubleQuoteIdx;
            if (quoteIdx == -1) {
                quoteIdx = singleQuoteIdx;
            } else if (singleQuoteIdx != -1 && singleQuoteIdx < quoteIdx) {
                quoteIdx = singleQuoteIdx;
            }
            if (spaceIdx != -1 && (quoteIdx == -1 || spaceIdx < quoteIdx)) {
                int startIdx = idx;
                int endIdx = spaceIdx;
                if (endIdx > startIdx) {
                    currentWord.append(input.substring(startIdx, endIdx));
                }
                if (currentWord.length() > 0) {
                    tokens.add(currentWord.toString());
                    currentWord.setLength(0);
                }
                idx = endIdx + 1;
            } else if (quoteIdx != -1) {
                if (quoteIdx > idx) {
                    currentWord.append(input.substring(idx, quoteIdx));
                }
                
                String quoteChar = input.substring(quoteIdx, quoteIdx + 1);
                int secondQuoteIdx = input.indexOf(quoteChar, quoteIdx + 1);
                // no matching quote?
                if (secondQuoteIdx == -1) {
                    if (failOnUnmatchedQuotes) {
                        throw new IllegalArgumentException("unmatched quotes");
                    }
                    int nextSpaceIdx = input.indexOf(" ", quoteIdx + 1);
                    if (nextSpaceIdx == -1) {
                        int startIdx = quoteIdx + 1;
                        int endIdx = input.length();
                        if (endIdx > startIdx) {
                            currentWord.append(input.substring(startIdx, endIdx));
                        }
                        idx = endIdx;
                    } else {
                        int startIdx = quoteIdx + 1;
                        int endIdx = nextSpaceIdx;
                        if (endIdx > startIdx) {
                            currentWord.append(input.substring(startIdx, endIdx));
                        }
                        idx = endIdx;
                    }
                } else {
                    int startIdx = quoteIdx + 1;
                    int endIdx = secondQuoteIdx;
                    if (endIdx > startIdx) {
                        currentWord.append(unescape(input.substring(startIdx, endIdx), quoteChar.charAt(0)));
                    }
                    idx = endIdx + 1;
                }
            } else {
                currentWord.append(input.substring(idx));
                idx = input.length();
            }
        }
        if (currentWord.length() > 0) {
            tokens.add(currentWord.toString());
        }
        return tokens;
    }
    
    /**
     * Override this to unescape stuff inside quotes. Default: no change.
     * 
     */
    public String unescape(String input, char quoteChar) {
        return input;
    }

    public boolean isFailOnUnmatchedQuotes() {
        return failOnUnmatchedQuotes;
    }

    public void setFailOnUnmatchedQuotes(boolean failOnUnmatchedQuotes) {
        this.failOnUnmatchedQuotes = failOnUnmatchedQuotes;
    }
}
