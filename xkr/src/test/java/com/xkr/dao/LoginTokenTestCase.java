package com.xkr.dao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.xkr.BaseDaoTest;
import com.xkr.dao.mapper.XkrLoginTokenMapper;
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
public class LoginTokenTestCase extends BaseDaoTest{

    @Autowired
    private XkrLoginTokenMapper loginTokenMapper;


    @Test
    public void testSelectList(){
        Assert.assertEquals(3,loginTokenMapper.getLoginTokensByIds(ImmutableList.of(1L,2L,3L)).size());
    }

}
