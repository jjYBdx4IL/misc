package com.github.jjYBdx4IL.cms.jpa.dto;

import java.io.Serializable;
import java.util.regex.Pattern;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
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

    public Tag(String name) {
        this.name = name;
    }

    @Id
    @Basic(optional = false)
    @Column(length = 32, unique = true)
    private String id;
    
    @Basic(optional = false)
    @Column(length = 32)
    private String name;

    @PrePersist
    @PreUpdate
    private void prepare() {
        this.id = name == null ? null : name.toLowerCase();
    }

    @Version
    private int version;
    
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the value
     */
    public String getName() {
        return name;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setName(String name) {
        this.name = name;
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
