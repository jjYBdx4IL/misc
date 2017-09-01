package com.github.jjYBdx4IL.cms.jpa.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

@SuppressWarnings("serial")
@Entity
@Table(indexes={
    @Index(name="OWNER_CREATEDAT_INDEX", unique=false, columnList="OWNER,CREATEDAT")
})
public class Article implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    @Basic
    @Column(name="OWNER")
    private User owner;
    @OneToMany(cascade = {CascadeType.ALL})
    private Collection<Tag> tags = new ArrayList<>();
    @Basic
    private String title;
    @Basic
    private String content;
    @Basic
    @Column(name="CREATEDAT")
    private Date createdAt;
    @Basic
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

    /**
     * @return the values
     */
    public Collection<Tag> getTags() {
        return tags;
    }

    /**
     * @param values the values to set
     */
    public void setValues(Collection<Tag> tags) {
        this.tags = tags;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }

}
