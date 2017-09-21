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
package com.github.jjYBdx4IL.cms.jpa.dto;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

//CHECKSTYLE:OFF
@SuppressWarnings("serial")
@Entity
@Table(indexes = {
    @Index(name = "CREATEDAT_INDEX", unique = false, columnList = "createdAt")
})
@Indexed
public class Article implements Serializable {

    public static final Pattern PATHID_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9-]+$");

    @Id
    @GeneratedValue
    private Long id;

    @Basic(optional = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner")
    private User owner;

    @ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private Collection<Tag> tags = new ArrayList<>();

    @Basic(optional = false)
    @Lob
    @Field(store = Store.NO)
    private String title;

    @Basic(optional = false)
    @Lob
    @Field(store = Store.NO)
    private String content;

    @Basic(optional = false)
    @Lob
    private String processed;

    @Basic(optional = false)
    @Column(name = "createdAt")
    private Date createdAt;

    @Basic(optional = true)
    private Date firstPublishedAt;
    
    @Basic(optional = false)
    private boolean published;
    
    @Basic(optional = false)
    private Date lastModified;

    // we want articles to have IDs that persevere across exports/imports
    @Basic(optional = false)
    @Column(length = 255, unique = true)
    private String pathId;

    @Version
    private int version;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Collection<Tag> getTags() {
        return tags;
    }

    public void setTags(Collection<Tag> tags) {
        this.tags = tags;
    }

    public Long getId() {
        return id;
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

    public int getVersion() {
        return version;
    }

    public String getPathId() {
        return pathId;
    }

    public void setPathId(String pathId) {
        this.pathId = pathId;
    }

    public String getProcessed() {
        return processed;
    }

    public void setProcessed(String processed) {
        this.processed = processed;
    }

    public Date getFirstPublishedAt() {
        return firstPublishedAt;
    }

    public void setFirstPublishedAt(Date firstPublishedAt) {
        this.firstPublishedAt = firstPublishedAt;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Article [id=");
        builder.append(id);
        builder.append(", owner=");
        builder.append(owner);
        builder.append(", tags=");
        builder.append(tags);
        builder.append(", title=");
        builder.append(title);
        builder.append(", content=");
        builder.append(content);
        builder.append(", processed=");
        builder.append(processed);
        builder.append(", createdAt=");
        builder.append(createdAt);
        builder.append(", firstPublishedAt=");
        builder.append(firstPublishedAt);
        builder.append(", published=");
        builder.append(published);
        builder.append(", lastModified=");
        builder.append(lastModified);
        builder.append(", pathId=");
        builder.append(pathId);
        builder.append(", version=");
        builder.append(version);
        builder.append("]");
        return builder.toString();
    }

}
