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

import com.github.jjYBdx4IL.utils.solr.SolrUtils;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class SolrConfig {

    private static final Logger LOG = LoggerFactory.getLogger(SolrConfig.class);

    public static final String SOLR_CONNECTION = "http://127.0.0.1:8983/solr/WebSearchCollection";
    public static final int MAX_RESULTS_PER_REQUEST = 10;
    public static final boolean IS_CLUSTERED = false;

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
        QueryResponse response = client.query(query);
        return response.getBeans(WebPageBean.class);
    }

    public static void init() {
        try (SolrClient client = getClient()) {
            SolrUtils.verifyOrDisableAutoCreateFields(SOLR_CONNECTION);
            if (IS_CLUSTERED) {
                SolrUtils.verifyOrEnableAutoCommit(SOLR_CONNECTION);
            }
            SolrUtils.verifyOrCreateSchema(client, WebPageBean.class);
        } catch (IOException | UnirestException | SolrServerException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void commit(SolrClient client) throws SolrServerException, IOException {
        if (!IS_CLUSTERED) {
            client.commit();
        }
    }

//    public static WebPageBean getWebPageBean(SolrClient client, String url) throws SolrServerException, IOException {
//        String id = WebPageBean.constructId(url);
//        SolrQuery query = new SolrQuery();
//        query.set("q", "id:" + id);
//        query.set("rows", 1);
//        query.set("start", 0);
//        QueryResponse response = client.query(query);
//        List<WebPageBean> results = response.getBeans(WebPageBean.class);
//        return results.isEmpty() ? null : results.get(0);
//    }

}
