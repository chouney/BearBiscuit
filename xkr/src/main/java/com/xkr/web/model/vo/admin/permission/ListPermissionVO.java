package com.xkr.web.model.vo.admin.permission;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class ListPermissionVO implements Serializable{


    private static final long serialVersionUID = 3873942390270504483L;
    private List<PermissionVO> list = Lists.newArrayList();

    private int totalCount;

    public List<PermissionVO> getList() {
        return list;
    }

    public void setList(List<PermissionVO> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
