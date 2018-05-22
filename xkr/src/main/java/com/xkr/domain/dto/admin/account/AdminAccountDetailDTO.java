package com.xkr.domain.dto.admin.account;

import com.xkr.domain.dto.BaseDTO;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/25
 */
public class AdminAccountDetailDTO extends BaseDTO implements Serializable{

    private static final long serialVersionUID = 852878965890933740L;

    /**
     * adminAccountId":"12412431124",
     "accountName":"得得得",
     "email":"qweqwe@xx.c",
     "roleId":"14214124124"
     */

    private Long adminAccountId;

    private String accountName;

    private String email;

    private Integer roleId;

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

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}
