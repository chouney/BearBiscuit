package com.xkr.domain;

import com.google.common.collect.Lists;
import com.xkr.dao.mapper.XkrAdminPermissionMapper;
import com.xkr.domain.entity.XkrAdminPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 *
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/2
 */
@Service
public class XkrAdminPermissionAgent {

    @Autowired
    private XkrAdminPermissionMapper xkrAdminPermissionMapper;


    public List<XkrAdminPermission> getPermissionByIds(List<Integer> ids){
        if(CollectionUtils.isEmpty(ids)){
            return Lists.newArrayList();
        }
        return xkrAdminPermissionMapper.selectByIds(ids);
    }

    public List<XkrAdminPermission> getAll(){
        return xkrAdminPermissionMapper.getAll();
    }

}
