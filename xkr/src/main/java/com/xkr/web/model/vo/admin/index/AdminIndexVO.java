package com.xkr.web.model.vo.admin.index;

import com.xkr.domain.dto.BaseDTO;
import com.xkr.util.DateUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/25
 */
public class AdminIndexVO extends BaseDTO implements Serializable{
    private static final long serialVersionUID = 958286162839774992L;
    /**
     *  "roleName":"角色名称",
     "accountName":"用户名",
     "lastLoginDate":"YYYY-mm-dd HH:MM",
     "loginCount":412, //今日登录人数
     "regCount":412, //今日注册人数
     "uploadCount":412, //今日上传人数
     "downloadCount":412, //今日下载人数
     "userTotalCount":412 //会员总人数
     */

    private String roleName;

    private String accountName;

    private String lastLoginDate;

    private UserAccountVO user;

    private ResourceAccountVO design;

    private ResourceAccountVO resource;


    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        if (Objects.nonNull(lastLoginDate)) {
            this.lastLoginDate = DateUtil.yyyyMMddHHmmss.format(lastLoginDate);
        }
    }

    public UserAccountVO getUser() {
        return user;
    }

    public void setUser(UserAccountVO user) {
        this.user = user;
    }

    public ResourceAccountVO getDesign() {
        return design;
    }

    public void setDesign(ResourceAccountVO design) {
        this.design = design;
    }

    public ResourceAccountVO getResource() {
        return resource;
    }

    public void setResource(ResourceAccountVO resource) {
        this.resource = resource;
    }
}
