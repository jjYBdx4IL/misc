package com.github.jjYBdx4IL.cms.jpa.dto;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
@Entity
@Cacheable
public class ConfigValue implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Basic
    @Enumerated(EnumType.STRING) 
    private ConfigKey key;

    @Basic
    private String value;

    public ConfigValue() {
    }

    public ConfigValue(ConfigKey key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * @return the data1
     */
    public ConfigKey getKey() {
        return key;
    }

    /**
     * @param data1
     *            the data1 to set
     */
    public void setKey(ConfigKey key) {
        this.key = key;
    }

    /**
     * @return the data2
     */
    public String getValue() {
        return value;
    }

    /**
     * @param data2
     *            the data2 to set
     */
    public void setValue(String value) {
        this.value = value;
    }

}
