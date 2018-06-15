package com.xkr.web.model.vo.optlog;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.xkr.util.DateUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/24
 */
public class OptLogVO implements Serializable {

    private static final long serialVersionUID = -7788769681043149880L;
    /**
     "optlogId":"操作日志id",
     "adminAccountId":"管理员账号id",
     "accountName":"管理员账号",
     "optModule":"操作模块", //
     "optDetail":"操作内容",
     "clientIp":"ip",
     "date":"YY-mm-dd HH:MM:SS"

     */
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long optlogId;

    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long adminAccountId;

    private String accountName;

    private String optModule;

    private String optDetail;

    private String clientIp;

    private String date;

    public Long getOptlogId() {
        return optlogId;
    }

    public void setOptlogId(Long optlogId) {
        this.optlogId = optlogId;
    }

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

    public String getOptModule() {
        return optModule;
    }

    public void setOptModule(String optModule) {
        this.optModule = optModule;
    }

    public String getOptDetail() {
        return optDetail;
    }

    public void setOptDetail(String optDetail) {
        this.optDetail = optDetail;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(Date date) {
        if(Objects.isNull(date)){
            return;
        }
        this.date = DateUtil.yyyyMMddHHmmss.format(date);
    }
}
