package com.xkr.dao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.xkr.BaseDaoTest;
import com.xkr.dao.mapper.XkrResourceMapper;
import com.xkr.domain.dto.resource.ResourceStatusEnum;
import com.xkr.domain.entity.XkrResource;
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
public class ResourceDaoTestCase extends BaseDaoTest {

    @Autowired
    private XkrResourceMapper mapper;


    @Before
    public void init(){
    }


    @Test
    public void testGetResourceByIds(){
        Map<String,Object> param = Maps.newHashMap();
        param.put("statuses", ResourceStatusEnum.NON_DELETE_STATUSED.stream().map(ResourceStatusEnum::getCode).collect(Collectors.toList()));
        param.put("ids", ImmutableList.of(1,3));
        Assert.assertEquals(mapper.getResourceByIds(param).size(),2);
    }

    @Test
    public void testGetResourceByClassIds(){
        Map<String,Object> param = Maps.newHashMap();
        param.put("statuses", ResourceStatusEnum.NON_DELETE_STATUSED.stream().map(ResourceStatusEnum::getCode).collect(Collectors.toList()));
        param.put("classIds", ImmutableList.of(10,12));
        Assert.assertEquals(mapper.getResourceByClassIds(param).size(),2);
    }

    @Test
    public void testGetResourceByUserIds(){
        Map<String,Object> param = Maps.newHashMap();
        param.put("statuses", ResourceStatusEnum.NON_DELETE_STATUSED.stream().map(ResourceStatusEnum::getCode).collect(Collectors.toList()));
        param.put("userId", 100);
        Assert.assertEquals(mapper.getResourceByUserId(param).size(),1);
    }

    @Test
    public void testGetResourceById(){
        Map<String,Object> param = Maps.newHashMap();
        param.put("statuses", ResourceStatusEnum.NON_DELETE_STATUSED.stream().map(ResourceStatusEnum::getCode).collect(Collectors.toList()));
        param.put("id", 1);
        Assert.assertNotNull(mapper.getResourceById(param));
    }

    @Test
    public void testXBatchUpdateStatus(){
        Map<String,Object> param = Maps.newHashMap();
        param.put("status",4);
        param.put("list", ImmutableList.of(1,2));
        mapper.batchUpdateResourceByIds(param);
        param = Maps.newHashMap();
        param.put("statuses", ResourceStatusEnum.NON_DELETE_STATUSED.stream().map(ResourceStatusEnum::getCode).collect(Collectors.toList()));
        param.put("ids", ImmutableList.of(1,2));
        Assert.assertEquals(0,mapper.getResourceByIds(param).size());
    }

    @Test
    public void testYBatchUpdateStatus(){
        Map<String,Object> param = Maps.newHashMap();
        param.put("classId",20);
        param.put("list", ImmutableList.of(3,4));
        mapper.batchUpdateResourceClassByIds(param);
        param = Maps.newHashMap();
        param.put("statuses", ResourceStatusEnum.NON_DELETE_STATUSED.stream().map(ResourceStatusEnum::getCode).collect(Collectors.toList()));
        param.put("ids", ImmutableList.of(3,4));
        List<XkrResource> resourceList = mapper.getResourceByIds(param);
        resourceList.forEach(resource -> Assert.assertEquals(20L,resource.getClassId().longValue()));

    }

    @Test
    public void testZBatchDeleteStatus(){
        Map<String,Object> param = Maps.newHashMap();
        param.put("statuses", ImmutableList.of(4));
        param.put("ids", ImmutableList.of(1,2));
        Assert.assertEquals(2,mapper.getResourceByIds(param).size());
        mapper.batchDeleteResourceByIds(ImmutableList.of(1L,2L));
        param = Maps.newHashMap();
        param.put("statuses", ImmutableList.of(1,2,3,4,-1));
        param.put("ids", ImmutableList.of(1,2));
        Assert.assertEquals(0,mapper.getResourceByIds(param).size());
    }


}
