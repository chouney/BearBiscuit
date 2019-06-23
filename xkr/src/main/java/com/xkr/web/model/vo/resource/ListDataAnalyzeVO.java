package com.xkr.web.model.vo.resource;

import com.google.common.collect.Lists;
import com.xkr.domain.dto.BaseDTO;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class ListDataAnalyzeVO implements Serializable{


    private static final long serialVersionUID = 3918922784283047441L;
    private List<DataAnalyzeVO> list = Lists.newArrayList();

    private int totalCount;

    public List<DataAnalyzeVO> getList() {
        return list;
    }

    public void setList(List<DataAnalyzeVO> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
