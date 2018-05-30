package com.xkr.domain.dto.remark;

import com.xkr.domain.dto.BaseDTO;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/22
 */
public class RemarkDetailDTO extends BaseDTO implements Serializable{

    private static final long serialVersionUID = -4311106279927233337L;

    private Long remarkId;

    private String content;

    private Date submitDate;

    private RemarkDetailDTO pRemark;

    public Long getRemarkId() {
        return remarkId;
    }

    public void setRemarkId(Long remarkId) {
        this.remarkId = remarkId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public RemarkDetailDTO getpRemark() {
        return pRemark;
    }

    public void setpRemark(RemarkDetailDTO pRemark) {
        this.pRemark = pRemark;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }
}
