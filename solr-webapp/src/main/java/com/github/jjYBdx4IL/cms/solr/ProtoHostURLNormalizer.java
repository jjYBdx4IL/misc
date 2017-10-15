package com.github.jjYBdx4IL.cms.solr;

import crawlercommons.filters.basic.BasicURLNormalizer;

import java.net.MalformedURLException;
import java.net.URL;

public class ProtoHostURLNormalizer extends BasicURLNormalizer {

    @Override
    public String filter(String urlString) {
        try {
            URL url = new URL(urlString);
            String protocol = url.getProtocol().toLowerCase();
            if (protocol == null || !protocol.equals("http") && !protocol.equals("https")) {
                protocol = "https";
            }
            String hostname = url.getHost();
            return super.filter(protocol + "://" + hostname);
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
