package com.xkr.dao;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.xkr.BaseDaoTest;
import com.xkr.dao.mapper.XkrClassMapper;
import com.xkr.dao.mapper.XkrUserMapper;
import com.xkr.domain.dto.user.UserStatusEnum;
import com.xkr.domain.entity.XkrClass;
import com.xkr.domain.entity.XkrUser;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.xkr.domain.XkrClassAgent.CLASS_STATUS_NORMAL;
import static com.xkr.domain.XkrClassAgent.ROOT_CLASS_ID;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserDaoTestCase extends BaseDaoTest{

    @Autowired
    private XkrUserMapper userMapper;



    @Test
    public void testGetCount(){
        Integer count  = userMapper.getTotalUser(ImmutableMap.of(
                "statuses", UserStatusEnum.NON_DELETE_STATUSED.stream().map(UserStatusEnum::getCode).collect(Collectors.toList())
        ));
        assertEquals(4,count.intValue());
    }

    @Test
    public void testSelectByUserName(){
        XkrUser user  = userMapper.selectByUserName(ImmutableMap.of(
                "statuses", UserStatusEnum.NON_DELETE_STATUSED.stream().map(UserStatusEnum::getCode).collect(Collectors.toList()),
                "userLogin","manatea1"
        ));
        assertNotNull(user);
    }

    @Test
    public void testSelectByEmail(){
        XkrUser user  = userMapper.selectByEmail(ImmutableMap.of(
                "statuses", UserStatusEnum.NON_DELETE_STATUSED.stream().map(UserStatusEnum::getCode).collect(Collectors.toList()),
                "userLogin","a2@b.c"
        ));
        assertNotNull(user);
    }

    @Test
    public void testGetById(){
        XkrUser user  = userMapper.getUserById(ImmutableMap.of(
                "statuses", UserStatusEnum.NON_DELETE_STATUSED.stream().map(UserStatusEnum::getCode).collect(Collectors.toList()),
                "id","3"
        ));
        assertNotNull(user);
    }

    @Test
    public void testGetByIds(){
        List<XkrUser> users  = userMapper.getUserByIds(ImmutableMap.of(
                "statuses", UserStatusEnum.NON_DELETE_STATUSED.stream().map(UserStatusEnum::getCode).collect(Collectors.toList()),
                "ids",ImmutableList.of(1,2,3,4)
        ));
        assertEquals(4,users.size());
    }

    @Test
    public void testZBatchUpdateUserByIds(){
        userMapper.batchUpdateUserByIds(ImmutableMap.of(
                "status",-1,
                "list",ImmutableList.of(1,2,3,4)
        ));
        assertEquals(0,userMapper.getUserByIds(ImmutableMap.of(
                "statuses", UserStatusEnum.NON_DELETE_STATUSED.stream().map(UserStatusEnum::getCode).collect(Collectors.toList()),
                "ids",ImmutableList.of(1,2,3,4)
        )).size());
    }



}
