package com.xkr.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "xkr_admin_account")
public class XkrAdminAccount extends BaseEntity implements Serializable{

    private static final long serialVersionUID = -3644407875041446978L;

    /**
     * 管理员id
     */
    @Id
    private Long id;

    /**
     * 管理员名称
     */
    @Column(name = "account_name")
    private String accountName;

    /**
     * 管理员token
     */
    @Column(name = "account_token")
    private String accountToken;

    /**
     * 邮箱,长度限制64
     */
    private String email;

    /**
     * 权限id,以;分割
     */
    @Column(name = "permission_ids")
    private String permissionIds;

    /**
     * 账号状态
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
     * 获取管理员id
     *
     * @return id - 管理员id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置管理员id
     *
     * @param id 管理员id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取管理员名称
     *
     * @return account_name - 管理员名称
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * 设置管理员名称
     *
     * @param accountName 管理员名称
     */
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    /**
     * 获取管理员token
     *
     * @return account_token - 管理员token
     */
    public String getAccountToken() {
        return accountToken;
    }

    /**
     * 设置管理员token
     *
     * @param accountToken 管理员token
     */
    public void setAccountToken(String accountToken) {
        this.accountToken = accountToken;
    }

    /**
     * 获取邮箱,长度限制64
     *
     * @return email - 邮箱,长度限制64
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱,长度限制64
     *
     * @param email 邮箱,长度限制64
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(String permissionIds) {
        this.permissionIds = permissionIds;
    }

    /**
     * 获取账号状态
     *
     * @return status - 账号状态
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置账号状态
     *
     * @param status 账号状态
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