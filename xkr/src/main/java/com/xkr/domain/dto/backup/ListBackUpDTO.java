package com.xkr.domain.dto.backup;

import com.google.common.collect.Lists;
import com.xkr.domain.dto.BaseDTO;
import com.xkr.domain.dto.admin.permission.PermissionDTO;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class ListBackUpDTO extends BaseDTO implements Serializable{


    private static final long serialVersionUID = -2911419227160287998L;

    private List<BackUpDTO> list = Lists.newArrayList();

    private int totalCount;

    public List<BackUpDTO> getList() {
        return list;
    }

    public void setList(List<BackUpDTO> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
