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
public class RemarkVO implements Serializable{

    private static final long serialVersionUID = 5462103987619685654L;
    /**
     * "remarkId":"留言id",
     "content":"标题",
     "phone":"1312312", //电话
     "qq":"QQ号",
     "userName":"会员名",
     */
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long remarkId;

    private String content;

    private String phone;

    private String qq;

    private String userName;

    private String submitDate;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
