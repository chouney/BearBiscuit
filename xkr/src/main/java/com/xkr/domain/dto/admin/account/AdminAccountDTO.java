package com.xkr.domain.dto.admin.account;

import com.google.common.collect.Lists;
import com.xkr.domain.dto.BaseDTO;
import com.xkr.domain.dto.comment.CommentDTO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class AdminAccountDTO  implements Serializable{


    private static final long serialVersionUID = 2644198064206744003L;

    /**
     * “adminAccountId”:"4124123123" g//管理员id
     "accountName":"管理员账号",
     "email":"XXXX", //邮箱
     "clientIp":"ip",
     "createDate":"YY-mm-dd HH:MM:SS", //创建时间
     "lastLoginDate":"YY-mm-dd" //最后登录时间
     */

    private Long adminAccountId;

    private String accountName;

    private String email;

    private String clientIp;

    private Date createDate;

    private Date lastLoginDate;

    public Long getAdminAccountId() {
        return adminAccountId;
    }

    public void setAdminAccountId(Long adminAccountId) {
        this.adminAccountId = adminAccountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
}
