package com.xkr.domain.dto.admin.role;

import com.google.common.collect.Lists;
import com.xkr.domain.dto.BaseDTO;
import com.xkr.domain.dto.admin.account.AdminAccountDTO;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class ListAdminRoleDTO extends BaseDTO implements Serializable{


    private static final long serialVersionUID = 7554772921631986483L;
    private List<AdminRoleDTO> list = Lists.newArrayList();

    private int totalCount;

    public List<AdminRoleDTO> getList() {
        return list;
    }

    public void setList(List<AdminRoleDTO> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
