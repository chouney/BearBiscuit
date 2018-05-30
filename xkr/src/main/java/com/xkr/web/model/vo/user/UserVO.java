package com.xkr.web.model.vo.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.xkr.util.DateUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/11
 */
public class UserVO implements Serializable {

    private static final long serialVersionUID = -7552628344602202975L;
    /**
     * "userId":"会员id"
     * "userName":"会员名",
     * "email":"邮箱",
     * "createTime":"注册时间",
     * "status":"会员状态"
     */
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long userId;

    private String userName;

    private String email;

    private String createTime;

    private Integer status;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = DateUtil.yyyyMMdd.format(createTime);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
