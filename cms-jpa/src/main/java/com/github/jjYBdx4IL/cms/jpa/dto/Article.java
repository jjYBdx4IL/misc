package com.github.jjYBdx4IL.cms.jpa.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

@SuppressWarnings("serial")
@Entity
@Table(indexes = {
    @Index(name = "CREATEDAT_INDEX", unique = false, columnList = "createdAt")
})
public class Article implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Basic(optional = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner")
    private User owner;

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    private Collection<Tag> tags = new ArrayList<>();

    @Basic(optional = false)
    @Column(columnDefinition = "TEXT")
    private String title;

    @Basic(optional = false)
    @Column(columnDefinition = "TEXT")
    private String content;

    @Basic(optional = false)
    @Column(name = "createdAt")
    private Date createdAt;

    @Basic(optional = false)
    private Date lastModified;

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

}
