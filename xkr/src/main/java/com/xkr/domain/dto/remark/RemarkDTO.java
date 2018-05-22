package com.xkr.domain.dto.remark;

import java.io.Serializable;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/22
 */
public class RemarkDTO implements Serializable{

    private static final long serialVersionUID = 2733548857878447468L;
    /**
     * "remarkId":"留言id",
     "content":"标题",
     "phone":"1312312", //电话
     "qq":"QQ号",
     "userName":"会员名",
     */
    private Long remarkId;

    private String content;

    private String phone;

    private String qq;

    private String userName;

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
