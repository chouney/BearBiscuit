package com.xkr.domain;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.xkr.dao.mapper.XkrAdminPermissionMapper;
import com.xkr.domain.entity.XkrAdminPermission;
import com.xkr.domain.entity.XkrAdminRole;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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


    public List<XkrAdminPermission> getAdminRoleByIds(List<Integer> ids){
        if(CollectionUtils.isEmpty(ids)){
            return Lists.emptyList();
        }
        return xkrAdminPermissionMapper.selectByIds(ids);
    }

    public List<XkrAdminPermission> getAll(){
        return xkrAdminPermissionMapper.selectAll();
    }

}
