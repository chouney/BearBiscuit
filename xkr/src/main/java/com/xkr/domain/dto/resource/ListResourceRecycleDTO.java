package com.xkr.domain.dto.resource;

import com.google.common.collect.Lists;
import com.xkr.domain.dto.ResponseDTO;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/11
 */
public class ListResourceRecycleDTO extends ResponseDTO implements Serializable{

    private static final long serialVersionUID = 5712910912621058840L;
    private List<ResourceRecycleDTO> list = Lists.newArrayList();
    private int totalCount;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<ResourceRecycleDTO> getList() {
        return list;
    }

    public void setList(List<ResourceRecycleDTO> list) {
        this.list = list;
    }
}
