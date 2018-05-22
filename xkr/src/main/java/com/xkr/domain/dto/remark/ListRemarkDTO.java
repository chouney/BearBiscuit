package com.xkr.domain.dto.remark;

import com.google.common.collect.Lists;
import com.xkr.domain.dto.BaseDTO;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/22
 */
public class ListRemarkDTO extends BaseDTO implements Serializable{


    private static final long serialVersionUID = -5750059185570398014L;

    private List<RemarkDTO> list = Lists.newArrayList();

    private int totalCount;

    public List<RemarkDTO> getList() {
        return list;
    }

    public void setList(List<RemarkDTO> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
