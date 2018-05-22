package com.xkr.domain.dto.comment;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/11
 */
public class CommentDetailDTO implements Serializable {

    private static final long serialVersionUID = 8234286344271852114L;
    /**
     * "commentId":"评论id",
     "resId":"资源id",
     ”title“:"资源标题",
     "content":"评论内容",
     "clientIp":"ip地址",
     "userName":"会员账号",
     "updateTime":"更新时间",
     "status":"评论状态"
     */
    private Long commentId;

    private Long resourceId;

    private String title;

    private String content;

    private String clientIp;

    private String userName;

    private Date updateTime;

    private Integer status;

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
