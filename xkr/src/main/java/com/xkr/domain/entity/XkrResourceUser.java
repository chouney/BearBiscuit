package com.xkr.domain.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "xkr_resource_user")
public class XkrResourceUser {
    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 资源id
     */
    @Column(name = "resource_id")
    private Long resourceId;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 用户资源状态,已支付
     */
    private Byte status;

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
     * 扩展字段，存储download_count，resource_url等
     */
    private String ext;

    /**
     * 获取id
     *
     * @return id - id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置id
     *
     * @param id id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取资源id
     *
     * @return resource_id - 资源id
     */
    public Long getResourceId() {
        return resourceId;
    }

    /**
     * 设置资源id
     *
     * @param resourceId 资源id
     */
    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    /**
     * 获取用户id
     *
     * @return user_id - 用户id
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置用户id
     *
     * @param userId 用户id
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取用户资源状态,已支付
     *
     * @return status - 用户资源状态,已支付
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置用户资源状态,已支付
     *
     * @param status 用户资源状态,已支付
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取更新时间
     *
     * @return update_time - 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取扩展字段，存储download_count，resource_url等
     *
     * @return ext - 扩展字段，存储download_count，resource_url等
     */
    public String getExt() {
        return ext;
    }

    /**
     * 设置扩展字段，存储download_count，resource_url等
     *
     * @param ext 扩展字段，存储download_count，resource_url等
     */
    public void setExt(String ext) {
        this.ext = ext;
    }
}