package com.xkr.domain.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "xkr_message")
public class XkrMessage extends BaseEntity implements Serializable{

    private static final long serialVersionUID = 8853423226373183851L;
    /**
     * 消息id
     */
    @Id
    private Long id;

    /**
     * 消息来源id类型,1用户id,2管理员id
     */
    @Column(name = "from_type_code")
    private Byte fromTypeCode;

    /**
     * 来源消息id
     */
    @Column(name = "from_id")
    private Long fromId;

    /**
     * 消息目标id类型,1用户id,2管理员id
     */
    @Column(name = "to_type_code")
    private Byte toTypeCode;

    /**
     * 目标消息id
     */
    @Column(name = "to_id")
    private Long toId;

    /**
     * 消息内容,长度限制80字内
     */
    private String content;

    /**
     * 消息状态,1为未读,2为已读,3为删除
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
     * 获取消息id
     *
     * @return id - 消息id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置消息id
     *
     * @param id 消息id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取消息来源id类型
     *
     * @return from_type_code - 消息来源id类型
     */
    public Byte getFromTypeCode() {
        return fromTypeCode;
    }

    /**
     * 设置消息来源id类型
     *
     * @param fromTypeCode 消息来源id类型
     */
    public void setFromTypeCode(Byte fromTypeCode) {
        this.fromTypeCode = fromTypeCode;
    }

    /**
     * 获取来源消息id
     *
     * @return from_id - 来源消息id
     */
    public Long getFromId() {
        return fromId;
    }

    /**
     * 设置来源消息id
     *
     * @param fromId 来源消息id
     */
    public void setFromId(Long fromId) {
        this.fromId = fromId;
    }

    /**
     * 获取消息目标id类型
     *
     * @return to_type_code - 消息目标id类型
     */
    public Byte getToTypeCode() {
        return toTypeCode;
    }

    /**
     * 设置消息目标id类型
     *
     * @param toTypeCode 消息目标id类型
     */
    public void setToTypeCode(Byte toTypeCode) {
        this.toTypeCode = toTypeCode;
    }

    /**
     * 获取目标消息id
     *
     * @return to_id - 目标消息id
     */
    public Long getToId() {
        return toId;
    }

    /**
     * 设置目标消息id
     *
     * @param toId 目标消息id
     */
    public void setToId(Long toId) {
        this.toId = toId;
    }

    /**
     * 获取消息内容,长度限制80字内
     *
     * @return content - 消息内容,长度限制80字内
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置消息内容,长度限制80字内
     *
     * @param content 消息内容,长度限制80字内
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取消息状态
     *
     * @return status - 消息状态
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置消息状态
     *
     * @param status 消息状态
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