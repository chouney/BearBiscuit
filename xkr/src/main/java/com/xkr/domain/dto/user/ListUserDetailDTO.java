package com.xkr.domain.dto.user;

import com.google.common.collect.Lists;
import com.xkr.domain.dto.BaseDTO;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class ListUserDetailDTO extends BaseDTO implements Serializable{

    private static final long serialVersionUID = -6801888896388888124L;

    private List<UserDetailDTO> userList = Lists.newArrayList();

    private int totalCount;

    public List<UserDetailDTO> getUserList() {
        return userList;
    }

    public void setUserList(List<UserDetailDTO> userList) {
        this.userList = userList;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
