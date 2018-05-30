package com.xkr.web.model.vo.comment;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class ListCommentVO implements Serializable{


    private static final long serialVersionUID = 1372071919390186831L;
    private List<CommentVO> comments = Lists.newArrayList();

    private int totalCount;

    public List<CommentVO> getComments() {
        return comments;
    }

    public void setComments(List<CommentVO> comments) {
        this.comments = comments;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
