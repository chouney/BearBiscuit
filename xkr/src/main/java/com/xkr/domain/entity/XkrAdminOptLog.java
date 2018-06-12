package com.xkr.domain.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "xkr_admin_opt_log")
public class XkrAdminOptLog {
    /**
     * 评论id
     */
    @Id
    private Long id;

    /**
     * 管理员id
     */
    @Column(name = "admin_account_id")
    private Long adminAccountId;

    /**
     * 操作模块
     */
    @Column(name = "opt_module")
    private Byte optModule;

    /**
     * 操作内容,长度限制80字内
     */
    @Column(name = "opt_detail")
    private String optDetail;

    /**
     * 客户端ip
     */
    @Column(name = "client_ip")
    private String clientIp;

    /**
     * 日志状态
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
     * 扩展字段
     */
    private String ext;

    /**
     * 获取评论id
     *
     * @return id - 评论id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置评论id
     *
     * @param id 评论id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取管理员id
     *
     * @return admin_account_id - 管理员id
     */
    public Long getAdminAccountId() {
        return adminAccountId;
    }

    /**
     * 设置管理员id
     *
     * @param adminAccountId 管理员id
     */
    public void setAdminAccountId(Long adminAccountId) {
        this.adminAccountId = adminAccountId;
    }

    /**
     * 获取操作模块
     *
     * @return opt_module - 操作模块
     */
    public Byte getOptModule() {
        return optModule;
    }

    /**
     * 设置操作模块
     *
     * @param optModule 操作模块
     */
    public void setOptModule(Byte optModule) {
        this.optModule = optModule;
    }

    /**
     * 获取操作内容,长度限制80字内
     *
     * @return opt_detail - 操作内容,长度限制80字内
     */
    public String getOptDetail() {
        return optDetail;
    }

    /**
     * 设置操作内容,长度限制80字内
     *
     * @param optDetail 操作内容,长度限制80字内
     */
    public void setOptDetail(String optDetail) {
        this.optDetail = optDetail;
    }

    /**
     * 获取客户端ip
     *
     * @return client_ip - 客户端ip
     */
    public String getClientIp() {
        return clientIp;
    }

    /**
     * 设置客户端ip
     *
     * @param clientIp 客户端ip
     */
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    /**
     * 获取日志状态
     *
     * @return status - 日志状态
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置日志状态
     *
     * @param status 日志状态
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
     * 获取扩展字段
     *
     * @return ext - 扩展字段
     */
    public String getExt() {
        return ext;
    }

    /**
     * 设置扩展字段
     *
     * @param ext 扩展字段
     */
    public void setExt(String ext) {
        this.ext = ext;
    }
}