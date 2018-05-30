package com.xkr.web.model.vo.resource;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/29
 */
public class ListResourceFolderVO implements Serializable{

    private static final long serialVersionUID = 3269437176532762140L;

    private List<ResourceFolderVO> list = Lists.newArrayList();


    public List<ResourceFolderVO> getList() {
        return list;
    }

    public void setList(List<ResourceFolderVO> list) {
        this.list = list;
    }
}
