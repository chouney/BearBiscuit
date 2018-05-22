package com.xkr.domain.dto.comment;

import com.google.common.collect.Lists;
import com.xkr.domain.dto.BaseDTO;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class ListCommentDetailDTO extends BaseDTO implements Serializable{


    private static final long serialVersionUID = -8667120544041763935L;
    private List<CommentDetailDTO> list = Lists.newArrayList();

    private int totalCount;

    public List<CommentDetailDTO> getList() {
        return list;
    }

    public void setList(List<CommentDetailDTO> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
