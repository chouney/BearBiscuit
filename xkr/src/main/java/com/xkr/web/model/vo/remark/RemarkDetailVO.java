package com.xkr.web.model.vo.remark;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.xkr.util.DateUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/22
 */
public class RemarkDetailVO implements Serializable{

    private static final long serialVersionUID = -4925275209092120537L;

    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long remarkId;

    private String content;

    private String submitDate;

    private RemarkDetailVO pRemark;

    public String getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        if(Objects.isNull(submitDate)){
            return;
        }
        this.submitDate = DateUtil.yyyyMMddHHmmss.format(submitDate);
    }

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

    public RemarkDetailVO getpRemark() {
        return pRemark;
    }

    public void setpRemark(RemarkDetailVO pRemark) {
        this.pRemark = pRemark;
    }
}
