package com.xkr.dao;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.xkr.BaseDaoTest;
import com.xkr.dao.mapper.XkrAboutRemarkMapper;
import com.xkr.dao.mapper.XkrAdminAccountMapper;
import com.xkr.domain.entity.XkrAdminAccount;
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
public class XkrAdminTestCase extends BaseDaoTest{

    @Autowired
    private XkrAdminAccountMapper adminAccountMapper;


    @Test
    public void testAdminAccountByRole(){
//        Assert.assertEquals(1,adminAccountMapper.getAdminAccountByRoleId(1).size());
    }

    @Test
    public void testSelectList(){
        Assert.assertEquals(2,adminAccountMapper.selectList().size());
    }

    @Test
    public void testGetByName(){
        Assert.assertNotNull(adminAccountMapper.selectByAccountName("zqx"));
    }

    @Test
    public void testGetById(){
        Assert.assertNotNull(adminAccountMapper.getById(1L));
    }

    @Test
    public void testGetByIds(){
        Assert.assertEquals(2,adminAccountMapper.getListByIds(ImmutableList.of(1L,2L,3L)).size());
    }

    @Test
    public void testUpdateNameById(){
        adminAccountMapper.updateAdminAccountById(ImmutableMap.of(
                "id",3L,"accountName","manatea","accountToken","manatea"
        ));

        XkrAdminAccount adminAccount = adminAccountMapper.getById(3L);
        Assert.assertEquals("manatea",adminAccount.getAccountName());
    }

    @Test
    public void testUpdateStatusById(){
        adminAccountMapper.batchUpdateAdminAccountByIds(
                ImmutableMap.of("status",2,"ids",ImmutableList.of(1L,3L))
        );
        Assert.assertEquals(0L,adminAccountMapper.selectList().size());
    }

}
