package com.github.jjYBdx4IL.cms.jpa.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * 
 * @author Github jjYBdx4IL Projects
 */
@SuppressWarnings("serial")
@Entity
@Table(indexes={
    @Index(name="USER_GOOGLE_UID_INDEX", unique=true, columnList="GOOGLE_UID")
})
public class User implements Serializable {

    public User() {
    }

    @Id
    @GeneratedValue
    private Long id;
    @Basic
    @Column(name="GOOGLE_UID")
    private String googleUniqueId;
    @Basic
    private String email;
    @Basic
    private int loginCount;
    @Basic
    private Date lastLoginAt;
    @Basic
    private Date createdAt;
    @Version
    private int version;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    public String getGoogleUniqueId() {
        return googleUniqueId;
    }

    public void setGoogleUniqueId(String googleUniqueId) {
        this.googleUniqueId = googleUniqueId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    public Date getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Date lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }

}
