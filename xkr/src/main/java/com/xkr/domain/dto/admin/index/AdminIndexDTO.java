package com.xkr.domain.dto.admin.index;

import com.xkr.domain.dto.BaseDTO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/25
 */
public class AdminIndexDTO extends BaseDTO implements Serializable{
    private static final long serialVersionUID = 8334426097728001569L;
    /**
     "accountName":"用户名",
     "lastLoginDate":"YYYY-mm-dd HH:MM",
     "loginCount":412, //今日登录人数
     "regCount":412, //今日注册人数
     "uploadCount":412, //今日上传人数
     "downloadCount":412, //今日下载人数
     "userTotalCount":412 //会员总人数
     */

    private String accountName;

    private Date lastLoginDate;

    private UserAccountDTO userAccountDTO;

    private ResourceAccountDTO designAccountDTO;

    private ResourceAccountDTO resoureAccountDTO;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public UserAccountDTO getUserAccountDTO() {
        return userAccountDTO;
    }

    public void setUserAccountDTO(UserAccountDTO userAccountDTO) {
        this.userAccountDTO = userAccountDTO;
    }

    public ResourceAccountDTO getDesignAccountDTO() {
        return designAccountDTO;
    }

    public void setDesignAccountDTO(ResourceAccountDTO designAccountDTO) {
        this.designAccountDTO = designAccountDTO;
    }

    public ResourceAccountDTO getResoureAccountDTO() {
        return resoureAccountDTO;
    }

    public void setResoureAccountDTO(ResourceAccountDTO resoureAccountDTO) {
        this.resoureAccountDTO = resoureAccountDTO;
    }
}
