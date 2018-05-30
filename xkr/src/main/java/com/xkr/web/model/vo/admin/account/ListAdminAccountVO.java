package com.xkr.web.model.vo.admin.account;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class ListAdminAccountVO implements Serializable{


    private static final long serialVersionUID = -44375231173276744L;
    private List<AdminAccountVO> list = Lists.newArrayList();

    private int totalCount;

    public List<AdminAccountVO> getList() {
        return list;
    }

    public void setList(List<AdminAccountVO> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
