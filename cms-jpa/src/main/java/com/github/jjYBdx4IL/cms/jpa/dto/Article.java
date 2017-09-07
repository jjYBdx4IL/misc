package com.github.jjYBdx4IL.cms.jpa.dto;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@SuppressWarnings("serial")
@Entity
@Table(indexes = {
    @Index(name = "CREATEDAT_INDEX", unique = false, columnList = "createdAt")
})
@Indexed
@XmlAccessorType(XmlAccessType.NONE)
public class Article implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Basic(optional = false)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner")
    @XmlElement
    private User owner;

    @ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @XmlElement
    private Collection<Tag> tags = new ArrayList<>();

    @Basic(optional = false)
    @Column(columnDefinition = "TEXT")
    @Field(store=Store.NO)
    @XmlElement
    private String title;

    @Basic(optional = false)
    @Column(columnDefinition = "TEXT")
    @Field(store=Store.NO)
    @XmlElement
    private String content;

    @Basic(optional = false)
    @Column(name = "createdAt")
    @XmlElement
    private Date createdAt;

    @Basic(optional = false)
    @XmlElement
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
