package com.github.jjYBdx4IL.cms.jpa.dto;

import java.io.Serializable;
import java.util.regex.Pattern;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

/**
 * 
 * @author Github jjYBdx4IL Projects
 */
@SuppressWarnings("serial")
@Entity
public class Tag implements Serializable {

    public static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9-]+$");
    
    public Tag() {
    }

    @Id
    @GeneratedValue
    private Long id;
    @Basic
    private String name;
    @Basic
    private String description;
    @Version
    private int version;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the value
     */
    public String getName() {
        return name;
    }

    /**
     * @param value the value to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.toLowerCase().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Tag other = (Tag) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equalsIgnoreCase(other.name))
            return false;
        return true;
    }

    
}
