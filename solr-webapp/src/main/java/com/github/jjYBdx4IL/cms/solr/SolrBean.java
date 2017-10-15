package com.github.jjYBdx4IL.cms.solr;

import org.apache.solr.client.solrj.beans.Field;

public class SolrBean {

    private String id;
    private SolrBeanType type;
    
    public SolrBean(String id, SolrBeanType type) {
        this.id = id;
        this.type = type;
    }

    public SolrBeanType getType() {
        return type;
    }

    @Field("type")
    public void setType(SolrBeanType type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    @Field("id")
    public void setId(String id) {
        this.id = id;
    }
    
}
