package com.xkr.domain.dto.resource;

import com.google.common.collect.Lists;
import com.xkr.domain.dto.BaseDTO;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class ListDataAnalyzeDTO extends BaseDTO implements Serializable{


    private List<DataAnalyzeDTO> resList = Lists.newArrayList();

    private int totalCount;

    public List<DataAnalyzeDTO> getResList() {
        return resList;
    }

    public void setResList(List<DataAnalyzeDTO> resList) {
        this.resList = resList;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
