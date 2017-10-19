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
package com.github.jjYBdx4IL.cms.tika;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MetaReply {
    @SerializedName("Content-Type")
    private String contentType;
    @SerializedName("Content-Encoding")
    private String contentEncoding;
    private String keywords;
    @SerializedName("X-Parsed-By")
    private List<String> parsedBy;
    private String title;
    private String language;
    private String robots;
    
    private String parsedContent;

    public String getLastParsedBy() {
        if (parsedBy == null || parsedBy.isEmpty()) {
            return "";
        }
        return parsedBy.get(parsedBy.size()-1);
    }
    
    public String getRobots() {
        return (robots == null ? "" : robots).toLowerCase();
    }

    public void setRobots(String robots) {
        this.robots = robots;
    }

    public String getContentType() {
        return contentType == null ? "" : contentType;
    }

    public void setContenttype(String contenttype) {
        this.contentType = contenttype;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public String getKeywords() {
        return keywords == null ? "" : keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public List<String> getParsedBy() {
        return parsedBy;
    }

    public void setParsedBy(List<String> parsedBy) {
        this.parsedBy = parsedBy;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getParsedContent() {
        return parsedContent == null ? "" : parsedContent;
    }

    public void setParsedContent(String parsedContent) {
        this.parsedContent = parsedContent;
    }
}
