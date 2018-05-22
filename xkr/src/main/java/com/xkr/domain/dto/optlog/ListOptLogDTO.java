package com.xkr.domain.dto.optlog;

import com.google.common.collect.Lists;
import com.xkr.domain.dto.BaseDTO;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/24
 */
public class ListOptLogDTO extends BaseDTO implements Serializable {
    private static final long serialVersionUID = 5457035925677210247L;

    private List<OptLogDTO> list = Lists.newArrayList();

    private int totalCount;

    public List<OptLogDTO> getList() {
        return list;
    }

    public void setList(List<OptLogDTO> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
