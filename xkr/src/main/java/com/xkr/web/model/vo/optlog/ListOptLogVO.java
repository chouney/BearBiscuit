package com.xkr.web.model.vo.optlog;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/24
 */
public class ListOptLogVO implements Serializable {

    private static final long serialVersionUID = -1791326625692677685L;

    private List<OptLogVO> list = Lists.newArrayList();

    private int totalCount;

    public List<OptLogVO> getList() {
        return list;
    }

    public void setList(List<OptLogVO> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
