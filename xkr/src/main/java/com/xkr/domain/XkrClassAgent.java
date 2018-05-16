package com.xkr.domain;

import com.google.common.collect.Lists;
import com.xkr.core.IdGenerator;
import com.xkr.dao.mapper.XkrClassMapper;
import com.xkr.domain.entity.XkrClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@Service
public class XkrClassAgent {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XkrClassMapper xkrClassMapper;

    @Autowired
    private IdGenerator idGenerator;

    public static final int ROOT_CLASS_ID = 0;

    public static final int ROOT_DESIGN_CLASS_ID = 1;

    public static final int ROOT_RESOURCE_CLASS_ID = 2;


    public static final int CLASS_STATUS_NORMAL = 1;

    public static final int CLASS_STATUS_DELETED = 2;


    public List<XkrClass> getAllChildClassByClassId(Long classId){
        List<XkrClass> list = Lists.newArrayList();
        if(Objects.isNull(classId)){
            logger.info("XkrClassAgent getAllChildClassByClassId param classId invalid null");
            return list;
        }
        list = xkrClassMapper.getAllChildClassByClassId(classId);
        return list;
    }

    public List<XkrClass> getAllClass(){
        return xkrClassMapper.getAll();
    }

    public boolean updateClassNameByClassId(Long classId,String className){
        if(Objects.isNull(classId)){
            logger.info("XkrClassAgent updateClassNameByClassId param classId invalid null");
            return false;
        }
        XkrClass xkrClass = new XkrClass();
        xkrClass.setId(classId);
        xkrClass.setClassName(className);
        return xkrClassMapper.updateByPrimaryKeySelective(xkrClass) == 1;
    }

    /**
     * 删除Class及其子class
     * @param classId
     * @return
     */
    public boolean deleteClassByClassId(Long classId){
        if(Objects.isNull(classId)){
            logger.info("XkrClassAgent deleteClassByClassId param classId invalid null");
            return false;
        }
        return xkrClassMapper.deleteClassByClassId(classId) == 1;
    }


    public boolean saveNewClass(String className,Long parendClassId){
        if(Objects.isNull(parendClassId)){
            logger.info("XkrClassAgent saveNewClass param parendClassId invalid null");
            return false;
        }
        long classId = idGenerator.generateId();
        XkrClass xkrClass = new XkrClass();
        xkrClass.setId(classId);
        xkrClass.setClassName(className);
        xkrClass.setStatus((byte)CLASS_STATUS_NORMAL);
        XkrClass parentClass = xkrClassMapper.selectByPrimaryKey(parendClassId);
        if(Objects.isNull(parentClass)){
            xkrClass.setPath(ROOT_CLASS_ID+"-"+classId);
            xkrClass.setParentClassId(Long.valueOf(ROOT_CLASS_ID));
        }else {
            xkrClass.setClassName(parentClass.getPath()+"-"+classId);
            xkrClass.setParentClassId(parendClassId);
        }
        return xkrClassMapper.insert(xkrClass) == 1;
    }

}
