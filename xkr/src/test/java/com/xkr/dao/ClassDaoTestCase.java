package com.xkr.dao;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.xkr.BaseDaoTest;
import com.xkr.common.LoginEnum;
import com.xkr.dao.mapper.XkrClassMapper;
import com.xkr.dao.mapper.XkrMessageMapper;
import com.xkr.domain.XkrMessageAgent;
import com.xkr.domain.entity.XkrClass;
import com.xkr.domain.entity.XkrMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.xkr.domain.XkrClassAgent.CLASS_STATUS_NORMAL;
import static com.xkr.domain.XkrClassAgent.ROOT_CLASS_ID;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
public class ClassDaoTestCase extends BaseDaoTest{

    @Autowired
    private XkrClassMapper xkrClassMapper;


    @Before
    public void init(){
        XkrClass xkrClass1 = new XkrClass();
        xkrClass1.setId(1L);
        xkrClass1.setClassName("毕设");
        xkrClass1.setStatus((byte)1);
        xkrClass1.setParentClassId(0L);
        xkrClass1.setPath("0-1");
        XkrClass xkrClass2 = new XkrClass();
        xkrClass2.setId(2L);
        xkrClass2.setClassName("资源");
        xkrClass2.setStatus((byte)1);
        xkrClass2.setParentClassId(0L);
        xkrClass2.setPath("0-2");
        XkrClass xkrClass3 = new XkrClass();
        xkrClass3.setId(4124124214124L);
        xkrClass3.setClassName("毕设1-1");
        xkrClass3.setStatus((byte)1);
        xkrClass3.setParentClassId(1L);
        xkrClass3.setPath("0-1-4124124214124");
        XkrClass xkrClass4 = new XkrClass();
        xkrClass4.setId(12312125125125L);
        xkrClass4.setClassName("毕设1-1-1");
        xkrClass4.setStatus((byte)1);
        xkrClass4.setParentClassId(4124124214124L);
        xkrClass4.setPath("0-1-4124124214124-12312125125125L");
        xkrClassMapper.insert(xkrClass1);
        xkrClassMapper.insert(xkrClass2);
        xkrClassMapper.insert(xkrClass3);
        xkrClassMapper.insert(xkrClass4);


    }

    @Test
    public void testGetChildClassById(){
        Long id = 1L;
        List<XkrClass> list = xkrClassMapper.getAllChildClassByClassId(id);
        assertEquals(3,list.size());
    }

    @Test
    public void testDeleteClassByClassId(){
        Long id = 4124124214124L;
        xkrClassMapper.deleteClassByClassId(id);
        List<XkrClass> list = xkrClassMapper.getAllChildClassByClassId(1L);
        assertEquals(1,list.size());
    }

    @Test
    public void testSaveNewClass(){
        XkrClass xkrClass = new XkrClass();
        xkrClass.setId(12345L);
        xkrClass.setClassName("哈哈");
        xkrClass.setStatus((byte)CLASS_STATUS_NORMAL);
        XkrClass parentClass = xkrClassMapper.selectByPrimaryKey(2L);
        if(Objects.isNull(parentClass)){
            xkrClass.setPath(ROOT_CLASS_ID+"-"+12345L);
            xkrClass.setParentClassId(Long.valueOf(ROOT_CLASS_ID));
        }else {
            xkrClass.setClassName(parentClass.getPath()+"-"+12345L);
            xkrClass.setParentClassId(2L);
        }
        System.out.println(JSON.toJSONString(xkrClass));
        assertEquals(1,xkrClassMapper.insert(xkrClass));
    }



}
