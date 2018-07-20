package com.xkr.domain.dto.admin.index;

import java.io.Serializable;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/7/18
 */
public class UserAccountDTO implements Serializable {

    private static final long serialVersionUID = 5061242703146367994L;
    /**
     * 	"userTotalCount":441,//"会员数量"
     "userActiveTotalCount":411,//"会员激活数量"
     "loginCount":412, //今日登录人数
     "regCount":412 //今日注册人数
     */

    private Integer userTotalCount;

    private Integer userActiveTotalCount;

    private Integer loginCount;

    private Integer regCount;

    public Integer getUserTotalCount() {
        return userTotalCount;
    }

    public void setUserTotalCount(Integer userTotalCount) {
        this.userTotalCount = userTotalCount;
    }

    public Integer getUserActiveTotalCount() {
        return userActiveTotalCount;
    }

    public void setUserActiveTotalCount(Integer userActiveTotalCount) {
        this.userActiveTotalCount = userActiveTotalCount;
    }

    public Integer getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }

    public Integer getRegCount() {
        return regCount;
    }

    public void setRegCount(Integer regCount) {
        this.regCount = regCount;
    }
}
