package com.github.jjYBdx4IL.cms.jaxb.dto;

import com.github.jjYBdx4IL.cms.jpa.dto.Article;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
public class ArticleDTO implements Serializable {

    private String title;
    private String pathId; 
    private String content;
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
        dto.setCreatedAt(article.getCreatedAt());
        dto.setLastModified(article.getLastModified());
        article.getTags().forEach(tag -> dto.getTags().add(tag.getName()));
        return dto;
    }
    
}
