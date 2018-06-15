package com.xkr.web.model.vo.resource;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.xkr.domain.dto.BaseDTO;
import com.xkr.domain.dto.resource.ParentClass;
import com.xkr.util.DateUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class ResourceDetailVO implements Serializable{

    private static final long serialVersionUID = -1546467097463337597L;

    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long resourceId;

    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long userId;

    private String userName;

    private ParentClass pClass;

    private String title;

    private String detail;

    private String fileSize;

    private String downloadCount;

    private Integer cost;

    private String updateTime;

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ParentClass getpClass() {
        return pClass;
    }

    public void setpClass(ParentClass pClass) {
        this.pClass = pClass;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(String downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        if(Objects.isNull(updateTime)){
            return;
        }
        this.updateTime = DateUtil.yyyyMMdd.format(updateTime);
    }


}
