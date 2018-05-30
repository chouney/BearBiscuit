package com.xkr.web.model.vo.admin.role;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class ListAdminRoleVO implements Serializable{


    private static final long serialVersionUID = -3791946707158337999L;
    private List<AdminRoleVO> list = Lists.newArrayList();

    private int totalCount;

    public List<AdminRoleVO> getList() {
        return list;
    }

    public void setList(List<AdminRoleVO> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
