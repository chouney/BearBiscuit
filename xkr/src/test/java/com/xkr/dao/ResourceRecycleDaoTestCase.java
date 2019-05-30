package com.xkr.dao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.xkr.BaseDaoTest;
import com.xkr.dao.mapper.XkrResourceMapper;
import com.xkr.dao.mapper.XkrResourceRecycleMapper;
import com.xkr.domain.dto.resource.ResourceStatusEnum;
import com.xkr.domain.entity.XkrResource;
import com.xkr.domain.entity.XkrResourceRecycle;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/21
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ResourceRecycleDaoTestCase extends BaseDaoTest {

    @Autowired
    private XkrResourceRecycleMapper mapper;


    @Before
    public void init(){
    }


    @Test
    public void testGetResourceByIds(){
        List<Long> resourceIds = mapper.selectAll().stream().map(XkrResourceRecycle::getResourceId).collect(Collectors.toList());
        Assert.assertTrue(mapper.batchDeleteResourceRecycleByIds(resourceIds) > 0);
    }



}
