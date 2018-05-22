package com.xkr.domain.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "xkr_about_remark")
public class XkrAboutRemark {
    /**
     * 留言d
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 账号类型,管理员或用户
     */
    @Column(name = "user_type_code")
    private Byte userTypeCode;

    /**
     * 父留言id
     */
    @Column(name = "parent_remark_id")
    private Long parentRemarkId;

    /**
     * 留言状态 1用户正常留言,2管理员回复,3删除
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
     * 扩展字段,包含qq号,手机号
     */
    private String ext;

    /**
     * 留言内容，5000字内
     */
    private String content;

    /**
     * 获取留言d
     *
     * @return id - 留言d
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置留言d
     *
     * @param id 留言d
     */
    public void setId(Long id) {
        this.id = id;
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
     * 获取账号类型,管理员或用户
     *
     * @return user_type_code - 账号类型,管理员或用户
     */
    public Byte getUserTypeCode() {
        return userTypeCode;
    }

    /**
     * 设置账号类型,管理员或用户
     *
     * @param userTypeCode 账号类型,管理员或用户
     */
    public void setUserTypeCode(Byte userTypeCode) {
        this.userTypeCode = userTypeCode;
    }

    /**
     * 获取父留言id
     *
     * @return parent_remark_id - 父留言id
     */
    public Long getParentRemarkId() {
        return parentRemarkId;
    }

    /**
     * 设置父留言id
     *
     * @param parentRemarkId 父留言id
     */
    public void setParentRemarkId(Long parentRemarkId) {
        this.parentRemarkId = parentRemarkId;
    }

    /**
     * 获取留言状态
     *
     * @return status - 留言状态
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置留言状态
     *
     * @param status 留言状态
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
     * 获取扩展字段,包含qq号,手机号
     *
     * @return ext - 扩展字段,包含qq号,手机号
     */
    public String getExt() {
        return ext;
    }

    /**
     * 设置扩展字段,包含qq号,手机号
     *
     * @param ext 扩展字段,包含qq号,手机号
     */
    public void setExt(String ext) {
        this.ext = ext;
    }

    /**
     * 获取留言内容，5000字内
     *
     * @return content - 留言内容，5000字内
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置留言内容，5000字内
     *
     * @param content 留言内容，5000字内
     */
    public void setContent(String content) {
        this.content = content;
    }
}