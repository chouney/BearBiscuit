package com.xkr.domain.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "xkr_class")
public class XkrClass {
    /**
     * 分类id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 父分类id
     */
    @Column(name = "parent_class_id")
    private Long parentClassId;

    /**
     * 分类路径,包含该路径,以-分割
     */
    private String path;

    /**
     * 分类名,长度限制20
     */
    @Column(name = "class_name")
    private String className;

    /**
     * 分类状态
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
     * 获取分类id
     *
     * @return id - 分类id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置分类id
     *
     * @param id 分类id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取父分类id
     *
     * @return parent_class_id - 父分类id
     */
    public Long getParentClassId() {
        return parentClassId;
    }

    /**
     * 设置父分类id
     *
     * @param parentClassId 父分类id
     */
    public void setParentClassId(Long parentClassId) {
        this.parentClassId = parentClassId;
    }

    /**
     * 获取分类路径,包含该路径,以-分割
     *
     * @return path - 分类路径,包含该路径,以-分割
     */
    public String getPath() {
        return path;
    }

    /**
     * 设置分类路径,包含该路径,以-分割
     *
     * @param path 分类路径,包含该路径,以-分割
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取分类名,长度限制20
     *
     * @return class_name - 分类名,长度限制20
     */
    public String getClassName() {
        return className;
    }

    /**
     * 设置分类名,长度限制20
     *
     * @param className 分类名,长度限制20
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * 获取分类状态
     *
     * @return status - 分类状态
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置分类状态
     *
     * @param status 分类状态
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