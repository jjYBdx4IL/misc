package com.github.jjYBdx4IL.cms.json.dto;

import com.github.jjYBdx4IL.cms.jpa.dto.MediaFile;

import java.sql.SQLException;
import java.util.Date;

//CHECKSTYLE:OFF
public class MediaFileDTO {

    private Long id;
    private String filename;
    private Long filesize;
    private byte[] preview;
    private Date createdAt;
    private Date lastModified;
    private String contentType;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public Long getFilesize() {
        return filesize;
    }
    public void setFilesize(Long filesize) {
        this.filesize = filesize;
    }
    public byte[] getPreview() {
        return preview;
    }
    public void setPreview(byte[] preview) {
        this.preview = preview;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public Date getLastModified() {
        return lastModified;
    }
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public static MediaFileDTO createFrom(MediaFile mf) throws SQLException {
        MediaFileDTO dto = new MediaFileDTO();
        dto.setId(mf.getId());
        dto.setContentType(mf.getContentType());
        dto.setCreatedAt(mf.getCreatedAt());
        dto.setLastModified(mf.getLastModified());
        dto.setPreview(mf.getPreview().getBytes(0, (int) mf.getPreview().length()));
        dto.setFilename(mf.getFilename());
        dto.setFilesize(mf.getFilesize());
        return dto;
    }
}
