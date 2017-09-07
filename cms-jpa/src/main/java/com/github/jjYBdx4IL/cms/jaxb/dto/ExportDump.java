package com.github.jjYBdx4IL.cms.jaxb.dto;

import com.github.jjYBdx4IL.cms.jpa.dto.Article;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("serial")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ExportDump implements Serializable {

    @XmlElement
    private List<Article> articles = new ArrayList<>();

    public ExportDump() {
    }

    public ExportDump(List<Article> articles) {
        this.articles = articles;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }
}
