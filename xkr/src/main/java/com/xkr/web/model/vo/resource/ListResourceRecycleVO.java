package com.xkr.web.model.vo.resource;

import com.google.common.collect.Lists;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.web.model.BasicResult;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/11
 */
public class ListResourceRecycleVO implements Serializable{

    private List<ResourceRecycleVO> list = Lists.newArrayList();
    private int totalCount;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<ResourceRecycleVO> getList() {
        return list;
    }

    public void setList(List<ResourceRecycleVO> list) {
        this.list = list;
    }
}
