package com.xkr.domain.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "xkr_resource_recycle")
public class XkrResourceRecycle extends BaseEntity{
    /**
     * 资源id
     */
    @Id
    @Column(name = "resource_id")
    private Long resourceId;

    /**
     * 资源标题
     */
    @Column(name = "resource_title")
    private String resourceTitle;

    /**
     * 分类id
     */
    @Column(name = "class_name")
    private String className;

    /**
     * 分类id
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 操作员id
     */
    @Column(name = "opt_name")
    private String optName;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 扩展字段，存储(md5加密后文件名(不带后缀的))
     */
    private String ext;

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

    public String getOptName() {
        return optName;
    }

    public void setOptName(String optName) {
        this.optName = optName;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}