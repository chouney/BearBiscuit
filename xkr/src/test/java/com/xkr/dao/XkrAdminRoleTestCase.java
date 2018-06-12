package com.xkr.dao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.xkr.BaseDaoTest;
import com.xkr.dao.mapper.XkrAdminAccountMapper;
import com.xkr.dao.mapper.XkrAdminRoleMapper;
import com.xkr.domain.entity.XkrAdminAccount;
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
public class XkrAdminRoleTestCase extends BaseDaoTest{

    @Autowired
    private XkrAdminRoleMapper adminRoleMapper;


    @Test
    public void testSelectList(){
        Assert.assertEquals(2,adminRoleMapper.selectList().size());
    }

    @Test
    public void testGetById(){
        Assert.assertNotNull(adminRoleMapper.selectById(1));
    }

    @Test
    public void testGetByIds(){
        Assert.assertEquals(2,adminRoleMapper.selectByIds(ImmutableList.of(1,2,3)).size());
    }

    @Test
    public void testUpdateRoleById(){
        adminRoleMapper.updateRoleById(ImmutableMap.of(
                "id",1,"roleName","manatea"
        ));

        XkrAdminRole adminRole = adminRoleMapper.selectById(1);
        Assert.assertEquals("manatea",adminRole.getRoleName());
    }

    @Test
    public void testUpdateStatusById(){
        adminRoleMapper.batchUpdateRoleByIds(
                ImmutableMap.of("status",2,"ids",ImmutableList.of(1,2,3))
        );
        Assert.assertEquals(0L,adminRoleMapper.selectList().size());
    }

}
