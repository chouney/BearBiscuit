package com.xkr.domain.dto.backup;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/30
 */
public class BackUpDTO implements Serializable{

    private static final long serialVersionUID = -6516133073604189857L;

    private Long backUpId;

    private String backUpName;

    private String accountName;

    private Date date;

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
