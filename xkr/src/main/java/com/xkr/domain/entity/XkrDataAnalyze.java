package com.xkr.domain.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "xkr_data_analyze")
public class XkrDataAnalyze extends BaseEntity implements Serializable{

    private static final long serialVersionUID = 3279343405430116781L;
    /**
     * 用户id
     */
    @Id
    private Long id;

    /**
     * 用户名,长度限制20字内
     */
    @Column(name = "title")
    private String title;

    /**
     * 用户token
     */
    @Column(name = "cal_count")
    private Integer calCount;

    /**
     * 用户token
     */
    @Column(name = "cal_type")
    private Byte calType;

    /**
     * 用户状态1为正常,2为未激活,3为冻结,4删除
     */
    private Byte status;

    /**
     * 注册时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 扩展字段
     */
    private String ext;

    /**
     * 获取用户id
     *
     * @return id - 用户id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置用户id
     *
     * @param id 用户id
     */
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

    public Byte getCalType() {
        return calType;
    }

    public void setCalType(Byte calType) {
        this.calType = calType;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
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