package com.xkr.domain;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.xkr.core.IdGenerator;
import com.xkr.dao.mapper.XkrClassMapper;
import com.xkr.domain.entity.XkrClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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


    private LoadingCache<Long, List<XkrClass>> childClassCache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .build(new CacheLoader<Long, List<XkrClass>>() {
                @Override
                public List<XkrClass> load(Long key) throws Exception {
                    return xkrClassMapper.getAllChildClassByClassId(key);
                }
            });

    public XkrClass getClassById(Long classId){
        if(Objects.isNull(classId)){
            logger.info("XkrClassAgent getClassById param classId invalid null");
            return null;
        }
        return xkrClassMapper.getClassById(classId);
    }

    public List<XkrClass> getClassByIds(List<Long> classIds){
        if(CollectionUtils.isEmpty(classIds)){
            logger.info("XkrClassAgent getClassByIds param classId invalid null");
            return null;
        }
        return xkrClassMapper.getClassByClassIds(classIds);
    }

    public List<XkrClass> getAllChildClassByClassId(Long classId){
        List<XkrClass> list = Lists.newArrayList();
        if(Objects.isNull(classId)){
            logger.info("XkrClassAgent getAllChildClassByClassId param classId invalid null");
            return list;
        }
        try {
            list = childClassCache.get(classId);
        } catch (ExecutionException e) {
            logger.error("ClassService getAllChildClassByClassId error, will return empty list,error:",e);
        }
        return list;
    }

    public boolean updateClassNameByClassId(Long classId,String className){
        if(Objects.isNull(classId)){
            logger.info("XkrClassAgent updateClassNameByClassId param classId invalid null");
            return false;
        }
        XkrClass xkrClass = new XkrClass();
        xkrClass.setId(classId);
        xkrClass.setClassName(className);
        xkrClass.setUpdateTime(new Date());
        if(xkrClassMapper.updateByPrimaryKeySelective(xkrClass) == 1){
            childClassCache.invalidateAll();
            return true;
        }
        return false;
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
        if(xkrClassMapper.deleteClassByClassId(classId) == 1){
            childClassCache.invalidateAll();
            return true;
        }
        return false;
    }

    /**
     * 删除Class及其子class
     * @param classIds
     * @return
     */
    public boolean batchDeleteClassByClassIds(List<Long> classIds){
        if(CollectionUtils.isEmpty(classIds)){
            logger.info("XkrClassAgent batchDeleteClassByClassId param classId invalid null");
            return false;
        }
        classIds.forEach(classId-> xkrClassMapper.deleteClassByClassId(classId));
        childClassCache.invalidateAll();
        return true;
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
        XkrClass parentClass = xkrClassMapper.getClassById(parendClassId);
        if(Objects.isNull(parentClass)){
            xkrClass.setPath(ROOT_CLASS_ID+"-"+classId);
            xkrClass.setParentClassId(Long.valueOf(ROOT_CLASS_ID));
        }else {
            xkrClass.setPath(parentClass.getPath()+"-"+classId);
            xkrClass.setParentClassId(parendClassId);
        }
        if(xkrClassMapper.insertSelective(xkrClass) == 1){
            childClassCache.invalidateAll();
            return true;
        }
        return false;
    }

}
