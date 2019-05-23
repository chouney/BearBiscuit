package com.xkr.web.model.vo.resource;

import com.xkr.util.DateUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/29
 */
public class ResourceFolderVO implements Serializable{

    private static final long serialVersionUID = -5901066632009129425L;

    private String name;

    private String fileType;
    private String size;
    private String date;

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

    public String getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = String.valueOf(size);
    }

    public String getDate() {
        return date;
    }

    public void setDate(Date date) {
        if(Objects.isNull(date)){
            return;
        }
        this.date = DateUtil.yyyyMMddHHmmss.format(date);
    }

    public void setSubFolders(List<ResourceFolderVO> subFolders) {
        this.subFolders = subFolders;
    }
}
