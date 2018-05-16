package com.xkr.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xkr.domain.XkrClassAgent;
import com.xkr.domain.dto.ClassMenuDTO;
import com.xkr.domain.entity.XkrClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@Service
public class ClassService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XkrClassAgent classAgent;

    private LoadingCache<String, List<ClassMenuDTO>> allClassCache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .build(new CacheLoader<String, List<ClassMenuDTO>>() {
                @Override
                public List<ClassMenuDTO> load(String key) throws Exception {
                    List<ClassMenuDTO> result = Lists.newArrayList();

                    List<XkrClass> xkrClassList;

                    xkrClassList = classAgent.getAllChildClassByClassId(Long.valueOf(key));


                    build(result, xkrClassList);

                    return result;
                }
            });

    public List<ClassMenuDTO> getAllClass() {
        try {
            return allClassCache.get(String.valueOf(XkrClassAgent.ROOT_CLASS_ID));
        } catch (ExecutionException e) {
            logger.error("ClassService getAllClass error, will return empty list,error:",e);
        }
        return Lists.newArrayList();
    }

    public List<ClassMenuDTO> getAllChildClassByClassId(Long classId) {
        List<ClassMenuDTO> list = Lists.newArrayList();
        if(Objects.isNull(classId)){
            logger.error("ClassService getAllChildClassByClassId invalid null param, will return empty list");
            return list;
        }

        try {
            return allClassCache.get(String.valueOf(classId));
        } catch (ExecutionException e) {
            logger.error("ClassService getAllChildClassByClassId error, will return empty list,error:",e);
        }
        return list;
    }

    public boolean updateClassNameById(Long classId,String className){
        if(classAgent.updateClassNameByClassId(classId,className)){
            //清空缓存
            allClassCache.cleanUp();
            return true;
        }
        return false;
    }

    public boolean deleteClassByClassId(Long classId){
        if(classAgent.deleteClassByClassId(classId)){
            //清空缓存
            allClassCache.cleanUp();
            return true;
        }
        return false;
    }

    public boolean saveNewClass(String className, Long parentClassId){
        if(classAgent.saveNewClass(className,parentClassId)){
            //清空缓存
            allClassCache.cleanUp();
            return true;
        }
        return false;
    }


    private void build(List<ClassMenuDTO> result, List<XkrClass> xkrClasses) {
        Map<Long, ClassMenuDTO> tmpMap = Maps.newHashMap();
        xkrClasses.forEach(xkrClass -> {
            ClassMenuDTO menuDTO = new ClassMenuDTO();
            menuDTO.setClassId(xkrClass.getId());
            menuDTO.setClassName(xkrClass.getClassName());
            tmpMap.put(menuDTO.getClassId(), menuDTO);
        });
        xkrClasses.forEach(xkrClass -> {
            ClassMenuDTO parent = tmpMap.get(xkrClass.getParentClassId());
            //如果为空,说明是根目录
            if (Objects.isNull(parent)) {
                result.add(tmpMap.get(xkrClass.getId()));
                return;
            }
            parent.getChild().add(tmpMap.get(xkrClass.getId()));
        });
    }
}
