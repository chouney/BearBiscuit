package com.xkr.dao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.xkr.BaseDaoTest;
import com.xkr.dao.mapper.XkrAdminPermissionMapper;
import com.xkr.dao.mapper.XkrAdminRoleMapper;
import com.xkr.domain.entity.XkrAdminRole;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class XkrAdminPermissionTestCase extends BaseDaoTest{

    @Autowired
    private XkrAdminPermissionMapper permissionMapper;


    @Test
    public void testSelectList(){
        Assert.assertEquals(2,permissionMapper.getAll().size());
    }

    @Test
    public void testGetByIds(){
        Assert.assertEquals(2,permissionMapper.selectByIds(ImmutableList.of(1,2,3)).size());
    }

}
