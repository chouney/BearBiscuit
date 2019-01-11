package com.xkr.domain.dto.resource;

import com.xkr.domain.dto.ResponseDTO;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/11
 */
public class ResourceRecycleDTO extends ResponseDTO implements Serializable{

    private static final long serialVersionUID = -3095059614687439166L;
    /**
     * 资源id
     */
    private Long resourceId;

    /**
     * 资源标题
     */
    private String resourceTitle;

    /**
     * 分类id
     */
    private String className;

    /**
     * 分类id
     */
    private String userName;

    /**
     * 操作员id
     */
    private String optName;

    /**
     * 更新时间
     */
    private Date updateTime;


    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceTitle() {
        return resourceTitle;
    }

    public void setResourceTitle(String resourceTitle) {
        this.resourceTitle = resourceTitle;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOptName() {
        return optName;
    }

    public void setOptName(String optName) {
        this.optName = optName;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
