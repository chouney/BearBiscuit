package com.xkr.domain.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "xkr_user")
public class XkrUser implements Serializable{

    private static final long serialVersionUID = 4193514178172374190L;
    /**
     * 用户id
     */
    @Id
    private Long id;

    /**
     * 用户名,长度限制20字内
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 用户token
     */
    @Column(name = "user_token")
    private String userToken;

    /**
     * token的salt
     */
    private String salt;

    /**
     * 邮箱,长度限制64
     */
    private String email;

    /**
     * 财富值
     */
    private Long wealth;

    /**
     * 充值总额
     */
    @Column(name = "total_recharge")
    private Long totalRecharge;

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

    /**
     * 获取用户名,长度限制20字内
     *
     * @return user_name - 用户名,长度限制20字内
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置用户名,长度限制20字内
     *
     * @param userName 用户名,长度限制20字内
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取用户token
     *
     * @return user_token - 用户token
     */
    public String getUserToken() {
        return userToken;
    }

    /**
     * 设置用户token
     *
     * @param userToken 用户token
     */
    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    /**
     * 获取token的salt
     *
     * @return salt - token的salt
     */
    public String getSalt() {
        return salt;
    }

    /**
     * 设置token的salt
     *
     * @param salt token的salt
     */
    public void setSalt(String salt) {
        this.salt = salt;
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

    /**
     * 获取财富值
     *
     * @return wealth - 财富值
     */
    public Long getWealth() {
        return wealth;
    }

    /**
     * 设置财富值
     *
     * @param wealth 财富值
     */
    public void setWealth(Long wealth) {
        this.wealth = wealth;
    }

    /**
     * 获取充值总额
     *
     * @return total_recharge - 充值总额
     */
    public Long getTotalRecharge() {
        return totalRecharge;
    }

    /**
     * 设置充值总额
     *
     * @param totalRecharge 充值总额
     */
    public void setTotalRecharge(Long totalRecharge) {
        this.totalRecharge = totalRecharge;
    }

    /**
     * 获取用户状态1为正常,2为未激活,3为冻结
     *
     * @return status - 用户状态1为正常,2为未激活,3为冻结
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置用户状态1为正常,2为未激活,3为冻结
     *
     * @param status 用户状态1为正常,2为未激活,3为冻结
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * 获取注册时间
     *
     * @return create_time - 注册时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置注册时间
     *
     * @param createTime 注册时间
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