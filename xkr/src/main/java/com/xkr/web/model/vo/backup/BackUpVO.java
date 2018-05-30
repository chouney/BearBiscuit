package com.xkr.web.model.vo.backup;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.xkr.util.DateUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/30
 */
public class BackUpVO implements Serializable{

    private static final long serialVersionUID = 6895213521983598132L;

    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long backUpId;

    private String backUpName;

    private String accountName;

    private String date;

    public Long getBackUpId() {
        return backUpId;
    }

    public void setBackUpId(Long backUpId) {
        this.backUpId = backUpId;
    }

    public String getBackUpName() {
        return backUpName;
    }

    public void setBackUpName(String backUpName) {
        this.backUpName = backUpName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = DateUtil.yyyyMMddHHmmss.format(date);
    }
}
