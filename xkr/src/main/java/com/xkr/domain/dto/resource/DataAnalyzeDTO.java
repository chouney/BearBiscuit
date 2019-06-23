package com.xkr.domain.dto.resource;

import com.xkr.domain.dto.BaseDTO;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/25
 */
public class DataAnalyzeDTO extends BaseDTO implements Serializable {

    private static final long serialVersionUID = -1182559768974672181L;

    private Long id;

    private String title;

    private Integer calCount;

    private Integer calType;

    private Date createTime;

    private Date updateTime;

    private String ext;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCalCount() {
        return calCount;
    }

    public void setCalCount(Integer calCount) {
        this.calCount = calCount;
    }

    public Integer getCalType() {
        return calType;
    }

    public void setCalType(Integer calType) {
        this.calType = calType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
