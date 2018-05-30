package com.xkr.domain.dto.resource;

import com.xkr.domain.dto.file.FolderItemDTO;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/29
 */
public class ResourceFolderDTO implements Serializable{

    private static final long serialVersionUID = 3903968445097045087L;
    private String name;

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

    public void setSubFolders(List<ResourceFolderDTO> subFolders) {
        this.subFolders = subFolders;
    }
}
