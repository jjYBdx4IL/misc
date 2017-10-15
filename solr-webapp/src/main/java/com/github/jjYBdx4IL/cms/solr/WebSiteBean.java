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
package com.github.jjYBdx4IL.cms.solr;

import com.github.jjYBdx4IL.cms.solr.SolrBean;
import crawlercommons.filters.basic.BasicURLNormalizer;
import org.apache.solr.client.solrj.beans.Field;

import java.util.Date;

public class WebSiteBean extends SolrBean {

    private Date modified;
    private Date retrieved;
    private Date lastChecked;
 
    public WebSiteBean(String url, String content, String keywords, Date modified, Date retrieved, Date lastChecked) {
        super("site-" + new ProtoHostURLNormalizer().filter(url), SolrBeanType.WEBSITE);
        this.content = content;
        this.keywords = keywords;
        this.modified = new Date(modified.getTime());
        this.retrieved = new Date(retrieved.getTime());;
        this.lastChecked = new Date(lastChecked.getTime());;
    }

    /**
     * Alias for {@link #getId()}.
     */
    public String getUrl() {
        return getId();
    }

    public String getContent() {
        return content;
    }

    @Field("content")
    public void setContent(String content) {
        this.content = content;
    }

    public String getKeywords() {
        return keywords;
    }
    
    @Field("keywords")
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Date getModified() {
        return modified;
    }

    @Field("modified")
    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Date getRetrieved() {
        return retrieved;
    }

    @Field("retrieved")
    public void setRetrieved(Date retrieved) {
        this.retrieved = retrieved;
    }

    public Date getLastChecked() {
        return lastChecked;
    }

    @Field("lastChecked")
    public void setLastChecked(Date lastChecked) {
        this.lastChecked = lastChecked;
    }

}
