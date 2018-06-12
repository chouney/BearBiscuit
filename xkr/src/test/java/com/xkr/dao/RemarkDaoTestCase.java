package com.xkr.dao;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.xkr.BaseDaoTest;
import com.xkr.dao.mapper.XkrAboutRemarkMapper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
public class RemarkDaoTestCase extends BaseDaoTest{

    @Autowired
    private XkrAboutRemarkMapper remarkMapper;


    @Test
    public void testGetAllList(){
        System.out.println(JSON.toJSONString(remarkMapper.getAllList()));
        Assert.assertEquals(remarkMapper.getAllList().size(),2);
    }

    @Test
    public void testGetClassByClassIds(){
       Assert.assertNotNull(remarkMapper.getRemarkById(2L));
    }

    @Test
    public void testBatchUpdateRemarkByIds(){
        remarkMapper.batchUpdateRemarkByIds(ImmutableMap.of(
                "list", ImmutableList.of(1,3),"status",2
        ));

        Assert.assertEquals(remarkMapper.getAllList().size(),2);
    }



}
