package com.xkr.domain.dto.user;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/11
 */
public class UserDTO implements Serializable {

    private static final long serialVersionUID = -7981572961222192969L;
    /**
     * "userId":"会员id"
     * "userName":"会员名",
     * "email":"邮箱",
     * "wealth":"财富值",
     * "createTime":"注册时间",
     * "status":"会员状态"
     */
    private Long userId;

    private String userName;

    private String email;

    private Long wealth;

    private Date createTime;

    private Integer status;

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

    public Long getWealth() {
        return wealth;
    }

    public void setWealth(Long wealth) {
        this.wealth = wealth;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
