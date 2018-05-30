package com.xkr.web.model.vo.admin;

import com.xkr.domain.dto.BaseDTO;
import com.xkr.util.DateUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/25
 */
public class AdminIndexVO implements Serializable{
    private static final long serialVersionUID = -6684233586855811601L;
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

    private Integer loginCount;

    private Integer regCount;

    private Integer uploadCount;

    private Integer downloadCount;

    private Integer userTotalCount;

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
        this.lastLoginDate = DateUtil.yyyyMMddHHmmss.format(lastLoginDate);
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

    public Integer getUploadCount() {
        return uploadCount;
    }

    public void setUploadCount(Integer uploadCount) {
        this.uploadCount = uploadCount;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Integer getUserTotalCount() {
        return userTotalCount;
    }

    public void setUserTotalCount(Integer userTotalCount) {
        this.userTotalCount = userTotalCount;
    }
}
