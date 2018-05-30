package com.xkr.web.model.vo.resource;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/29
 */
public class ResourceFolderVO implements Serializable{

    private static final long serialVersionUID = -5901066632009129425L;

    private String name;

    private String fileType;

    private List<ResourceFolderVO> subFolders;

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

    public List<ResourceFolderVO> getSubFolders() {
        return subFolders;
    }

    public void setSubFolders(List<ResourceFolderVO> subFolders) {
        this.subFolders = subFolders;
    }
}
