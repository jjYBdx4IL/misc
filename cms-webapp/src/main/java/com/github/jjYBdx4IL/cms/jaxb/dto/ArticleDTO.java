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
package com.github.jjYBdx4IL.cms.jaxb.dto;

import com.github.jjYBdx4IL.cms.jpa.dto.Article;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

//CHECKSTYLE:OFF
@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
public class ArticleDTO implements Serializable {

    private String title;
    private String pathId; 
    private String content;
    private String processed;
    private Date createdAt;
    private Date lastModified;
    private List<String> tag = new ArrayList<>();
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getPathId() {
        return pathId;
    }
    
    public void setPathId(String pathId) {
        this.pathId = pathId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getLastModified() {
        return lastModified;
    }
    
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
    
    public List<String> getTags() {
        return tag;
    }
    
    public void setTags(List<String> tags) {
        this.tag = tags;
    }
    
    public String getProcessed() {
        return processed;
    }

    public void setProcessed(String processed) {
        this.processed = processed;
    }
    
    /**
     * Create instance from the corresponding jpa instance.
     * 
     * @param article the jpa article instance
     * @return the dto instance
     */
    public static ArticleDTO create(Article article) {
        ArticleDTO dto = new ArticleDTO();
        dto.setTitle(article.getTitle());
        dto.setPathId(article.getPathId());
        dto.setContent(article.getContent());
        dto.setProcessed(article.getProcessed());
        dto.setCreatedAt(article.getCreatedAt());
        dto.setLastModified(article.getLastModified());
        article.getTags().forEach(tag -> dto.getTags().add(tag.getName()));
        return dto;
    }

}
