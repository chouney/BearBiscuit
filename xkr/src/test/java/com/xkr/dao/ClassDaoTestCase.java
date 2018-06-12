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
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
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
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ClassDaoTestCase extends BaseDaoTest{

    @Autowired
    private XkrClassMapper xkrClassMapper;



    @Test
    public void testGetChildClassById(){
        Long id = 1L;
        List<XkrClass> list = xkrClassMapper.getAllChildClassByClassId(id);
        assertEquals(3,list.size());
    }

    @Test
    public void testGetClassByClassIds(){
        List<Long> classIds = ImmutableList.of(
                1L,4124124214124L,12312125125125L
        );
        List<XkrClass> list = xkrClassMapper.getClassByClassIds(classIds);
        assertEquals(3,list.size());
    }

    @Test
    public void testXDeleteClassByClassId(){
        Long id = 4124124214124L;
        xkrClassMapper.deleteClassByClassId(id);
        List<XkrClass> list = xkrClassMapper.getAllChildClassByClassId(1L);
        assertEquals(1,list.size());
    }

    @Test
    public void testZSaveNewClass(){
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
