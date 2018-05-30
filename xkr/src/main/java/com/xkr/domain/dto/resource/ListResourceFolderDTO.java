package com.xkr.domain.dto.resource;

import com.google.common.collect.Lists;
import com.xkr.domain.dto.BaseDTO;
import com.xkr.domain.dto.file.FolderItemDTO;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/29
 */
public class ListResourceFolderDTO extends BaseDTO implements Serializable{

    private static final long serialVersionUID = 6452560572364064869L;
    private List<ResourceFolderDTO> list = Lists.newArrayList();


    public List<ResourceFolderDTO> getList() {
        return list;
    }

    public void setList(List<ResourceFolderDTO> list) {
        this.list = list;
    }
}
