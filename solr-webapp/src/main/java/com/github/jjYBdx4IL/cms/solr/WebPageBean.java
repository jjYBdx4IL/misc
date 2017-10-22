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
package com.github.jjYBdx4IL.cms.solr;

import com.github.jjYBdx4IL.utils.solr.beans.FieldConfig;
import com.github.jjYBdx4IL.utils.solr.beans.FieldType;
import org.apache.solr.client.solrj.beans.Field;

public class WebPageBean {

    private String url;
    private String title;
    private String content;
    private String keywords;
    private String contentType;
    private String parsedBy;
    private String language;
    private String description;
    private String category;

    public WebPageBean() {
    }

    public WebPageBean(String url, String title, String content, String keywords, String contentType,
        String parsedBy, String language, String description, String category) {

        this.url = url;
        this.title = title;
        this.content = content;
        this.keywords = keywords;
        this.contentType = contentType;
        this.parsedBy = parsedBy;
        this.language = language;
        this.description = description;
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    @Field("id")
    @FieldConfig(type = FieldType.string, indexed = true, required = true, stored = true)
    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    @Field("content")
    @FieldConfig(indexed = true, type = FieldType.text_general, stored = true)
    public void setContent(String content) {
        this.content = content;
    }

    public String getKeywords() {
        return keywords;
    }

    @Field("keywords")
    @FieldConfig(indexed = true, type = FieldType.text_general, stored = true)
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getTitle() {
        return title;
    }

    @Field("title")
    @FieldConfig(indexed = true, type = FieldType.text_general, stored = true)
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentType() {
        return contentType;
    }

    @Field("contentType")
    @FieldConfig(indexed = false, type = FieldType.string, stored = true)
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getParsedBy() {
        return parsedBy;
    }

    @Field("parsedBy")
    @FieldConfig(indexed = false, type = FieldType.string, stored = true)
    public void setParsedBy(String parsedBy) {
        this.parsedBy = parsedBy;
    }

    public String getLanguage() {
        return language;
    }

    @Field("language")
    @FieldConfig(indexed = true, type = FieldType.text_general, stored = true)
    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    @Field("description")
    @FieldConfig(indexed = true, type = FieldType.text_general, stored = true)
    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    @Field("category")
    @FieldConfig(indexed = true, type = FieldType.string, stored = true)
    public void setCategory(String category) {
        this.category = category;
    }
}
