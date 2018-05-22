package com.xkr.domain.dto.admin.permission;

import com.google.common.collect.Lists;
import com.xkr.domain.dto.BaseDTO;
import com.xkr.domain.dto.admin.role.AdminRoleDTO;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class ListPermissionDTO extends BaseDTO implements Serializable{


    private static final long serialVersionUID = 1558794126936468189L;
    private List<PermissionDTO> list = Lists.newArrayList();

    private int totalCount;

    public List<PermissionDTO> getList() {
        return list;
    }

    public void setList(List<PermissionDTO> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
