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
     * @param configValues config values
     * @return the export dump
     */
    public static ExportDump create(List<Article> articles, List<ConfigValue> configValues) {
        ExportDump dump = new ExportDump();
        articles.forEach(article -> dump.getArticles().add(ArticleDTO.create(article)));
        configValues.forEach(cv -> dump.getConfigValue().put(cv.getKey(), cv.getValue()));
        return dump;
    }

}
