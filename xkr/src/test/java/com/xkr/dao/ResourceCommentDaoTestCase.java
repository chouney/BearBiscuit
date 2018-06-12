package com.xkr.dao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.xkr.BaseDaoTest;
import com.xkr.dao.mapper.XkrResourceCommentMapper;
import com.xkr.dao.mapper.XkrResourceMapper;
import com.xkr.domain.dto.comment.CommentDTO;
import com.xkr.domain.dto.comment.CommentStatusEnum;
import com.xkr.domain.dto.resource.ResourceStatusEnum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/21
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ResourceCommentDaoTestCase extends BaseDaoTest {

    @Autowired
    private XkrResourceCommentMapper mapper;


    @Before
    public void init(){
    }


    @Test
    public void testGetCommentByIds(){
        Map<String,Object> param = Maps.newHashMap();
        param.put("statuses", CommentStatusEnum.NON_DELETE_STATUSED.stream().map(CommentStatusEnum::getCode).collect(Collectors.toList()));
        param.put("list", ImmutableList.of(1,2,3));
        Assert.assertEquals(mapper.getCommentsByIds(param).size(),3);
    }

    @Test
    public void testGetCommentByResourceId(){
        Map<String,Object> param = Maps.newHashMap();
        param.put("resourceId",1);
        param.put("statuses", ImmutableList.of(1,3));
        Assert.assertEquals(mapper.getCommentsByResourceId(param).size(),3);
    }

    @Test
    public void testGetResourceByUserIds(){
        Map<String,Object> param = Maps.newHashMap();
        param.put("statuses", CommentStatusEnum.NON_DELETE_STATUSED.stream().map(CommentStatusEnum::getCode).collect(Collectors.toList()));
        param.put("id", 1);
        Assert.assertNotNull(mapper.getCommentById(param));
    }

    @Test
    public void testZBatchUpdateStatus(){
        Map<String,Object> param = Maps.newHashMap();
        param.put("status", CommentStatusEnum.STATUS_DELETED.getCode());
        param.put("list", ImmutableList.of(1,2,3));
        mapper.batchUpdateCommentByIds(param);
        param = Maps.newHashMap();
        param.put("statuses", CommentStatusEnum.NON_DELETE_STATUSED.stream().map(CommentStatusEnum::getCode).collect(Collectors.toList()));
        param.put("list", ImmutableList.of(1,2,3));
        Assert.assertEquals(0,mapper.getCommentsByIds(param).size());
    }
}
