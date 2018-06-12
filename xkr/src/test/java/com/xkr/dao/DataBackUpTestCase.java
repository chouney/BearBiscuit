package com.xkr.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.xkr.BaseDaoTest;
import com.xkr.dao.mapper.XkrAdminAccountMapper;
import com.xkr.dao.mapper.XkrDatabaseBackupMapper;
import com.xkr.domain.entity.XkrAdminAccount;
import com.xkr.domain.entity.XkrDatabaseBackup;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.xkr.domain.XkrDatabaseBackUpAgent.EXT_LOCAL_FILEPATH_KEY;
import static com.xkr.domain.XkrDatabaseBackUpAgent.EXT_YUN_FILEPATH_KEY;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DataBackUpTestCase extends BaseDaoTest{

    @Autowired
    private XkrDatabaseBackupMapper backupMapper;


    @Test
    public void testSelectList(){
        Assert.assertEquals(3,backupMapper.selectList().size());
    }

    @Test
    public void testGetById(){
        Assert.assertNotNull(backupMapper.getBackUpById(1L));
    }

    @Test
    public void testGetByIds(){
        Assert.assertEquals(3,backupMapper.getListByIds(ImmutableList.of(1L,2L,3L,4L)).size());
    }

    @Test
    public void testYUpdateStatusById(){
        backupMapper.batchDeleteBackUpByIds(ImmutableList.of(1L,2L,3L,4L));
        Assert.assertEquals(0L,backupMapper.selectList().size());
    }

    @Test
    public void testZSave(){
        XkrDatabaseBackup backup = new XkrDatabaseBackup();
        Long id = 5L;
        backup.setAdminAccountId(1L);
        backup.setBackupName("test");
        JSONObject ext = new JSONObject();
        ext.put(EXT_LOCAL_FILEPATH_KEY,"/xkr/local");
        ext.put(EXT_YUN_FILEPATH_KEY,"/xkr/yun");
        backup.setStatus((byte)1);
        backup.setExt(JSON.toJSONString(ext));
        backup.setId(id);
        System.out.println(JSON.toJSONString(backup));
        Assert.assertEquals(1,backupMapper.insert(backup));
    }

}
