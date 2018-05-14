package com.xkr.domain.dto.file;

import java.util.Date;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/9
 */
public class FolderItemDTO {

    private String name;
    private boolean isFolder;
    private long size;
    private Date date;

    public FolderItemDTO() {
    }

    public FolderItemDTO(String name, boolean isFolder, long size, Date date) {
        this.name = name;
        this.isFolder = isFolder;
        this.size = size;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
