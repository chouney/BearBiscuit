package com.xkr.domain.dto.admin.account;

import com.google.common.collect.Lists;
import com.xkr.domain.dto.BaseDTO;
import com.xkr.domain.dto.comment.CommentDTO;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class ListAdminAccountDTO extends BaseDTO implements Serializable{


    private static final long serialVersionUID = 8258940940869543264L;
    private List<AdminAccountDTO> list = Lists.newArrayList();

    private int totalCount;

    public List<AdminAccountDTO> getList() {
        return list;
    }

    public void setList(List<AdminAccountDTO> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
