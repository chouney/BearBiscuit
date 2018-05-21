package com.xkr.domain.dto;

import com.google.common.collect.Lists;
import com.xkr.domain.dto.search.ResourceIndexDTO;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class ListResourceDTO extends BaseDTO implements Serializable{

    private static final long serialVersionUID = -3102169549302568639L;

    private List<ResourceDTO> resList = Lists.newArrayList();

    private int totalCount;

    public List<ResourceDTO> getResList() {
        return resList;
    }

    public void setResList(List<ResourceDTO> resList) {
        this.resList = resList;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
