package com.xkr.domain.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "xkr_resource_comment")
public class XkrResourceComment {
    /**
     * 评论id
     */
    @Id
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
     * 父评论id
     */
    @Column(name = "parent_comment_id")
    private Long parentCommentId;

    /**
     * 根评论id
     */
    @Column(name = "root_comment_id")
    private Long rootCommentId;

    /**
     * 客户端ip
     */
    @Column(name = "client_ip")
    private String clientIp;

    /**
     * 评论状态，1正常,2未审核,3删除
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
     * 评论内容，5000字内
     */
    private String content;

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
     * 获取父评论id
     *
     * @return parent_comment_id - 父评论id
     */
    public Long getParentCommentId() {
        return parentCommentId;
    }

    /**
     * 设置父评论id
     *
     * @param parentCommentId 父评论id
     */
    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    /**
     * 获取根评论id
     *
     * @return root_comment_id - 根评论id
     */
    public Long getRootCommentId() {
        return rootCommentId;
    }

    /**
     * 设置根评论id
     *
     * @param rootCommentId 根评论id
     */
    public void setRootCommentId(Long rootCommentId) {
        this.rootCommentId = rootCommentId;
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
     * 获取评论状态，1正常,2未审核,3删除
     *
     * @return status - 评论状态，1正常,2未审核,3删除
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置评论状态，1正常,2未审核,3删除
     *
     * @param status 评论状态，1正常,2未审核,3删除
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

    /**
     * 获取评论内容，5000字内
     *
     * @return content - 评论内容，5000字内
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置评论内容，5000字内
     *
     * @param content 评论内容，5000字内
     */
    public void setContent(String content) {
        this.content = content;
    }
}