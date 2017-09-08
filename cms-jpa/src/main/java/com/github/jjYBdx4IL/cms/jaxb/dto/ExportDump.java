package com.github.jjYBdx4IL.cms.jaxb.dto;

import com.github.jjYBdx4IL.cms.jpa.dto.Article;
import com.github.jjYBdx4IL.cms.jpa.dto.ConfigKey;
import com.github.jjYBdx4IL.cms.jpa.dto.ConfigValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("serial")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ExportDump implements Serializable {

    private List<ArticleDTO> article = new ArrayList<>();
    private Map<ConfigKey, String> configValues = new HashMap<>();

    public List<ArticleDTO> getArticles() {
        return article;
    }

    public void setArticles(List<ArticleDTO> articles) {
        this.article = articles;
    }

    public Map<ConfigKey, String> getConfigValue() {
        return configValues;
    }

    public void setConfigValue(Map<ConfigKey, String> configValue) {
        this.configValues = configValue;
    }
    
    /**
     * Create an {@link ExportDump} from JPA objects.
     * 
     * @param articles the jpa articles
     * @return the export dump
     */
    public static ExportDump create(List<Article> articles, List<ConfigValue> configValues) {
        ExportDump dump = new ExportDump();
        articles.forEach(article -> dump.getArticles().add(ArticleDTO.create(article)));
        configValues.forEach(cv -> dump.getConfigValue().put(cv.getKey(), cv.getValue()));
        return dump;
    }

}
