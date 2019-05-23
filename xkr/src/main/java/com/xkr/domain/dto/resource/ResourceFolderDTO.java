package com.xkr.domain.dto.resource;

import com.xkr.domain.dto.file.FolderItemDTO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/29
 */
public class ResourceFolderDTO implements Serializable{

    private static final long serialVersionUID = 3903968445097045087L;
    private String name;

    private long size;
    private Date date;
    private String fileType;

    private List<ResourceFolderDTO> subFolders;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public List<ResourceFolderDTO> getSubFolders() {
        return subFolders;
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

    public void setSubFolders(List<ResourceFolderDTO> subFolders) {
        this.subFolders = subFolders;
    }
}
