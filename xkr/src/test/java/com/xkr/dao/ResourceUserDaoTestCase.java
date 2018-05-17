package com.xkr.dao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.xkr.BaseDaoTest;
import com.xkr.dao.mapper.XkrResourceUserMapper;
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
public class ResourceUserDaoTestCase extends BaseDaoTest {

    @Autowired
    private XkrResourceUserMapper mapper;


    @Before
    public void init(){
    }


    @Test
    public void testGetResourceByUserId(){
        Map<String, Object> params = ImmutableMap.of(
                "status", 1,
                "userId", 100
        );
        Assert.assertEquals(mapper.getResourceByUserId(params).size(),1);
    }

}
