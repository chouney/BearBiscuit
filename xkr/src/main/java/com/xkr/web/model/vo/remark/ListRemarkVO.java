package com.xkr.web.model.vo.remark;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/22
 */
public class ListRemarkVO implements Serializable{


    private static final long serialVersionUID = 4684350984411779413L;
    private List<RemarkVO> list = Lists.newArrayList();

    private int totalCount;

    public List<RemarkVO> getList() {
        return list;
    }

    public void setList(List<RemarkVO> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
