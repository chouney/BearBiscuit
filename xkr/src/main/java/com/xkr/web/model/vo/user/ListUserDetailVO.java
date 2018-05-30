package com.xkr.web.model.vo.user;

import com.google.common.collect.Lists;
import com.xkr.domain.dto.BaseDTO;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class ListUserDetailVO implements Serializable{

    private static final long serialVersionUID = -3598776721777930234L;
    private List<UserDetailVO> userList = Lists.newArrayList();

    private int totalCount;

    public List<UserDetailVO> getUserList() {
        return userList;
    }

    public void setUserList(List<UserDetailVO> userList) {
        this.userList = userList;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
