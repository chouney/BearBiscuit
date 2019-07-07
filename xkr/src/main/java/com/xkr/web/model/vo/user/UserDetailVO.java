package com.xkr.web.model.vo.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.xkr.util.DateUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/11
 */
public class UserDetailVO implements Serializable {

    private static final long serialVersionUID = 4142299880706576386L;
    /**
     * "userId":"会员id"
     * "userName":"会员名",
     * "email":"邮箱",
     * "userToken":"用户token",
     * "wealth":11,//财富值
     * "totalRecharge":"3333",//充值金额,单位为分
     * "lastLoginDate":"YY-mm-dd HH:MM:SS",
     * "status":1, //会员属性
     * "clientIp":"客户端ip地址"
     */
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long userId;

    private String userName;

    private String email;

    private String userToken;

    private Long wealth;

    private Long totalRecharge;

    private String lastLoginDate;

    private String clientIp;

    private Integer status;

    private Integer loginCount;

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

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public Long getWealth() {
        return wealth;
    }

    public void setWealth(Long wealth) {
        this.wealth = wealth;
    }

    public Long getTotalRecharge() {
        return totalRecharge;
    }

    public void setTotalRecharge(Long totalRecharge) {
        this.totalRecharge = totalRecharge;
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        if(Objects.isNull(lastLoginDate)){
            return;
        }
        this.lastLoginDate = DateUtil.yyyyMMddHHmmss.format(lastLoginDate);
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Integer getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }
}
