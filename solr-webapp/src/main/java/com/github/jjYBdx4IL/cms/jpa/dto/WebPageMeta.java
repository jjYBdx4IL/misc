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

import java.io.Serializable;
import java.util.Date;

import javax.ejb.BeforeCompletion;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;

//CHECKSTYLE:OFF
/**
 * This data object control the update of individual, indexed web pages.
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(indexes = {
    @Index(name = "WEBPAGE_URL_IDX", unique = true, columnList = "url"),
    @Index(name = "WEBPAGE_SCHEDUPD_IDX", columnList = "scheduledUpdate")
})
public class WebPageMeta implements Serializable {

    public WebPageMeta() {
    }

    @Id
    @GeneratedValue
    private Long id;
    @Basic
    private String url;
    @Basic(optional = false)
    private Date scheduledUpdate;
    @Basic(optional = false)
    private int consecutiveErrorCount;
    @Basic
    private Date blocked;
    @Basic
    private String etag;
    @Basic
    private Date lastModified;
    @Basic
    private Date expires;
    @Basic(optional = false)
    private Date lastProcessed;
    @Version
    private int version;

    public Long getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getScheduledUpdate() {
        return scheduledUpdate;
    }

    public void setScheduledUpdate(Date scheduledUpdate) {
        this.scheduledUpdate = scheduledUpdate == null ? null : (Date) scheduledUpdate.clone();
    }

    public int getConsecutiveErrorCount() {
        return consecutiveErrorCount;
    }

    public void setConsecutiveErrorCount(int consecutiveErrorCount) {
        this.consecutiveErrorCount = consecutiveErrorCount;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified == null ? null : (Date) lastModified.clone();
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires == null ? null : (Date) expires.clone();
    }

    public Date getLastProcessed() {
        return lastProcessed;
    }

    public void setLastProcessed(Date lastProcessed) {
        this.lastProcessed = lastProcessed == null ? null : (Date) lastProcessed.clone();
    }
    
    public void copyValuesTo(WebPageMeta dest) {
        dest.setConsecutiveErrorCount(getConsecutiveErrorCount());
        dest.setEtag(getEtag());
        dest.setExpires(getExpires());
        dest.setLastModified(getLastModified());
        dest.setScheduledUpdate(getScheduledUpdate());
        dest.setUrl(getUrl());
        dest.setLastProcessed(getLastProcessed());
        dest.setBlocked(getBlocked());
    }

    public Date getBlocked() {
        return blocked;
    }

    public void setBlocked(Date blocked) {
        this.blocked = blocked == null ? null : (Date) blocked.clone();
    }
    
    @PrePersist
    public void fixDates() {
        if (getLastModified() == null && getExpires() == null) {
            return;
        }
        final Date now = new Date();
        if (getLastModified() != null) {
            setLastModified(getLastModified().after(now) ? null : getLastModified());
        }
        if (getExpires() != null) {
            setExpires(getExpires().before(now) ? null : getExpires());
        }
    }
}
