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

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;

//CHECKSTYLE:OFF
/**
 * This data object controls sitemap fetching.
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(indexes = {
    @Index(name = "DOMAIN_URL_IDX", unique = true, columnList = "url"),
    @Index(name = "DOMAIN_SCHEDUPD_IDX", columnList = "scheduledUpdate")
})
public class Domain implements Serializable {

    public Domain() {
    }

    @Id
    @GeneratedValue
    private Long id;
    @Basic(optional = false)
    private String url;
    @Basic
    private Date scheduledUpdate;
    @Basic(optional = false)
    private Date lastProcessed;

    /**
     * errors retrieving sitemaps, basically all errors before fetching
     * individual pages. used to determine removal of the domain.
     */
    @Basic
    private int consecutiveErrorCount;
    @Version
    private int version;

    public Long getId() {
        return id;
    }

    public int getVersion() {
        return version;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getLastProcessed() {
        return lastProcessed;
    }

    public void setLastProcessed(Date lastProcessed) {
        this.lastProcessed = lastProcessed;
    }
}
