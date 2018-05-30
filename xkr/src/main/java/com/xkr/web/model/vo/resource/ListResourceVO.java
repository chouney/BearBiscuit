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
public class ListResourceVO implements Serializable{

    private static final long serialVersionUID = 893872634424710134L;

    private List<ResourceVO> resList = Lists.newArrayList();

    private int totalCount;

    public List<ResourceVO> getResList() {
        return resList;
    }

    public void setResList(List<ResourceVO> resList) {
        this.resList = resList;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
