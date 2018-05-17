package com.xkr.domain.dto.search;


import java.io.Serializable;
import java.util.Date;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/11
 */
public class CommentIndexDTO extends BaseIndexDTO implements Serializable{

    private static final long serialVersionUID = -4029774414056704378L;
    /**
     * "commentId":"评论id",
     * "resId":"资源id",
     * ”title“:"资源标题",
     * "content":"评论内容",
     * "clientIp":"ip地址",
     * "userId":"会员id",
     * "userName":"会员账号",
     * "updateTime":"更新时间",
     * "status":"评论状态"
     */
    private Long commentId;

    private Long resourceId;

    private String title;

    private String content;

    private String clientIp;

    private Long userId;

    private String userName;

    private Date updateTime;

    private Integer status;

    public CommentIndexDTO() {
        super("xkr", "comment");
    }


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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String getIndexKey() {
        return String.valueOf(this.commentId);
    }
}
