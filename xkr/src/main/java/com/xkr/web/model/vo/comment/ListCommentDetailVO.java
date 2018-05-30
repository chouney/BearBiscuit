package com.xkr.web.model.vo.comment;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class ListCommentDetailVO implements Serializable{


    private static final long serialVersionUID = 2908885199741055621L;
    private List<CommentDetailVO> list = Lists.newArrayList();

    private int totalCount;

    public List<CommentDetailVO> getList() {
        return list;
    }

    public void setList(List<CommentDetailVO> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
