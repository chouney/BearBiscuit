package com.xkr.dao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.xkr.BaseDaoTest;
import com.xkr.dao.mapper.XkrResourceMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/21
 */
public class ResourceDaoTestCase extends BaseDaoTest {

    @Autowired
    private XkrResourceMapper mapper;


    @Before
    public void init(){
    }


    @Test
    public void testGetResourceByIds(){
        Map<String,Object> param = Maps.newHashMap();
        param.put("status",1);
        param.put("ids", ImmutableList.of(1,3));
        Assert.assertEquals(mapper.getResourceByIds(param).size(),2);
    }

    @Test
    public void testGetResourceByClassIds(){
        Map<String,Object> param = Maps.newHashMap();
        param.put("status",1);
        param.put("classIds", ImmutableList.of(10,12));
        Assert.assertEquals(mapper.getResourceByClassIds(param).size(),2);
    }

    @Test
    public void testGetResourceByUserIds(){
        Map<String,Object> param = Maps.newHashMap();
        param.put("status",1);
        param.put("userId", 100);
        Assert.assertEquals(mapper.getResourceByUserId(param).size(),1);
    }

}
