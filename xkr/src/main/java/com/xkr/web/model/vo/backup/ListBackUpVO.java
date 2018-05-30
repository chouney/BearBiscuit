package com.xkr.web.model.vo.backup;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class ListBackUpVO implements Serializable{

    private static final long serialVersionUID = -1907503226089489544L;

    private List<BackUpVO> list = Lists.newArrayList();

    private int totalCount;

    public List<BackUpVO> getList() {
        return list;
    }

    public void setList(List<BackUpVO> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
