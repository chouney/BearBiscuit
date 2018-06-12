package com.xkr.dao;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.xkr.BaseDaoTest;
import com.xkr.dao.mapper.XkrAboutRemarkMapper;
import com.xkr.dao.mapper.XkrAdminOptLogMapper;
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
public class AdminOptLotTestCase extends BaseDaoTest{

    @Autowired
    private XkrAdminOptLogMapper adminOptLogMapper;


    @Test
    public void testGetByAdminId(){
        Assert.assertEquals(adminOptLogMapper.getAllOptLogByAdminAccount(1L).size(),2);
        Assert.assertEquals(adminOptLogMapper.getAllOptLogByAdminAccount(2L).size(),1);
    }

    @Test
    public void testUpdateOptLogByIds(){
        adminOptLogMapper.batchUpdateOptLogByIds(ImmutableMap.of(
                "list", ImmutableList.of(1L,2L,3L,4L),"status",2
        ));

        Assert.assertEquals(adminOptLogMapper.getAllOptLogByAdminAccount(1L).size(),0);
        Assert.assertEquals(adminOptLogMapper.getAllOptLogByAdminAccount(2L).size(),0);
    }



}
