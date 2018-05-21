package com.xkr.domain.dto;

import com.xkr.domain.dto.search.BaseIndexDTO;

import java.util.Date;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/11
 */
public class UserDTO extends BaseIndexDTO {

    /**
     * "userId":"会员id"
     * "userName":"会员名",
     * "email":"邮箱",
     * "createTime":"注册时间",
     * "status":"会员状态"
     */
    private Long userId;

    private String userName;

    private String email;

    private Date createTime;

    private Integer status;

    public UserDTO() {
        super("xkr", "user");
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String getIndexKey() {
        return String.valueOf(this.userId);
    }
}
