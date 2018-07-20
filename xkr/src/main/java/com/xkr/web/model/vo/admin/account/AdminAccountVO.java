package com.xkr.web.model.vo.admin.account;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.xkr.util.DateUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class AdminAccountVO implements Serializable {


    private static final long serialVersionUID = -745337237083156163L;
    /**
     * “adminAccountId”:"4124123123" g//管理员id
     * "accountName":"管理员账号",
     * "email":"XXXX", //邮箱
     * "clientIp":"ip",
     * "createDate":"YY-mm-dd", //创建时间
     * "lastLoginDate":"YY-mm-dd HH:MM:SS" //最后登录时间
     */
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long adminAccountId;

    private String accountName;

    private String email;

    private String clientIp;

    private String createDate;

    private String lastLoginDate;

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

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        if (Objects.nonNull(createDate)) {
            this.createDate = DateUtil.yyyyMMddHHmmss.format(createDate);
        }
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        if (Objects.nonNull(lastLoginDate)) {
            this.lastLoginDate = DateUtil.yyyyMMddHHmmss.format(lastLoginDate);
        }
    }
}
