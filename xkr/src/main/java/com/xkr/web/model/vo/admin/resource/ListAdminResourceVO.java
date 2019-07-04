package com.xkr.web.model.vo.admin.resource;

import com.google.common.collect.Lists;
import com.xkr.web.model.vo.resource.ResourceVO;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class ListAdminResourceVO implements Serializable{


    private static final long serialVersionUID = 3010681711260549586L;
    private List<AdminResourceVO> resList = Lists.newArrayList();

    private int totalCount;

    public List<AdminResourceVO> getResList() {
        return resList;
    }

    public void setResList(List<AdminResourceVO> resList) {
        this.resList = resList;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
