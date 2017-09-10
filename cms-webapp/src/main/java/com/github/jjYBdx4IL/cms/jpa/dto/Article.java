package com.github.jjYBdx4IL.cms.jpa.dto;

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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

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
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner")
    private User owner;

    @ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    private Collection<Tag> tags = new ArrayList<>();

    @Basic(optional = false)
    @Column(columnDefinition = "TEXT")
    @Field(store = Store.NO)
    private String title;

    @Basic(optional = false)
    @Column(columnDefinition = "TEXT")
    @Field(store = Store.NO)
    private String content;

    @Basic(optional = false)
    @Column(name = "createdAt")
    private Date createdAt;

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

}