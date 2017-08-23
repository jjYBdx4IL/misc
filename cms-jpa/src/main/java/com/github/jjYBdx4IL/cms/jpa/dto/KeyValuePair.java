package com.github.jjYBdx4IL.cms.jpa.dto;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
@Entity
public class KeyValuePair implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * @return the version
     */
    public long getVersion() {
        return version;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the data1
     */
    public String getKey() {
        return key;
    }

    /**
     * @param data1
     *            the data1 to set
     */
    public void setKey(String key) {
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

    @Id
    @GeneratedValue
    private int id;

    @Basic
    private String key;

    @Basic
    private String value;

    @Version
    private long version;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("KeyValuePair [id=").append(id).append(", key=").append(key).append(", value=").append(value)
            .append(", version=").append(version).append("]");
        return builder.toString();
    }

}
