package com.xkr.domain.dto.comment;

import com.google.common.collect.Lists;
import com.xkr.domain.dto.BaseDTO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
public class CommentDTO implements Serializable{


    private static final long serialVersionUID = 3605348949401837814L;

    private Long commentId;

    private String userName;

    private String content;

    private Date commentDate;

    private Integer status;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(Date commentDate) {
        this.commentDate = commentDate;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
