package com.xkr.domain.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "xkr_database_backup")
public class XkrDatabaseBackup extends BaseEntity{
    /**
     * 数据库备份id
     */
    @Id
    private Long id;

    /**
     * 数据库备份名
     */
    @Column(name = "backup_name")
    private String backupName;

    /**
     * 管理员id
     */
    @Column(name = "admin_account_id")
    private Long adminAccountId;

    /**
     * 状态
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
     * 扩展字段,包括本地备份命令的存储
     */
    private String ext;

    /**
     * 获取数据库备份id
     *
     * @return id - 数据库备份id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置数据库备份id
     *
     * @param id 数据库备份id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取数据库备份名
     *
     * @return backup_name - 数据库备份名
     */
    public String getBackupName() {
        return backupName;
    }

    /**
     * 设置数据库备份名
     *
     * @param backupName 数据库备份名
     */
    public void setBackupName(String backupName) {
        this.backupName = backupName;
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
     * 获取状态
     *
     * @return status - 状态
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置状态
     *
     * @param status 状态
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
     * 获取扩展字段,包括本地备份命令的存储
     *
     * @return ext - 扩展字段,包括本地备份命令的存储
     */
    public String getExt() {
        return ext;
    }

    /**
     * 设置扩展字段,包括本地备份命令的存储
     *
     * @param ext 扩展字段,包括本地备份命令的存储
     */
    public void setExt(String ext) {
        this.ext = ext;
    }
}