package com.github.jjYBdx4IL.cms.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.io.IOException;
import java.util.List;

public class SolrConfig {

    public static final String SOLR_CONNECTION = "http://127.0.0.1:8983/solr/WebSearchCollection";
    public static final int MAX_RESULTS_PER_REQUEST = 10;

    public static SolrClient getClient() {
        HttpSolrClient solr = new HttpSolrClient.Builder(SOLR_CONNECTION).build();
        solr.setParser(new XMLResponseParser());
        return solr;
    }

    public static List<WebPageBean> queryWebPages(String queryString, int pageIndex)
        throws SolrServerException, IOException {

        try (SolrClient client = getClient()) {
            return queryWebPages(client, queryString, pageIndex);
        }
    }

    public static List<WebPageBean> queryWebPages(SolrClient client, String queryString, int pageIndex)
        throws SolrServerException, IOException {

        SolrQuery query = new SolrQuery();
        query.set("q", queryString);
        query.set("rows", MAX_RESULTS_PER_REQUEST);
        query.set("start", pageIndex * MAX_RESULTS_PER_REQUEST);
        query.set("type", SolrBeanType.WEBPAGE.name());
        QueryResponse response = client.query(query);
        return response.getBeans(WebPageBean.class);
    }

    public static void submitForProcessing(String site) throws IOException {
        try (SolrClient client = getClient()) {
            submitForProcessing(client, site);
        }
    }

    public static void submitForProcessing(SolrClient client, String site) {
        
    }
}
