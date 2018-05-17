package com.xkr.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xkr.domain.XkrClassAgent;
import com.xkr.domain.dto.ClassMenuDTO;
import com.xkr.domain.entity.XkrClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public List<ClassMenuDTO> getAllClass() {
        List<ClassMenuDTO> list = Lists.newArrayList();

        List<XkrClass> xkrClasses = classAgent.getAllChildClassByClassId((long) XkrClassAgent.ROOT_CLASS_ID);

        build(list, xkrClasses);

        return list;
    }

    public ClassMenuDTO getAllChildClassByClassId(Long classId) {
        List<ClassMenuDTO> list = Lists.newArrayList();
        if (Objects.isNull(classId)) {
            logger.error("ClassService getAllChildClassByClassId invalid null param, will return empty list");
            return null;
        }

        List<XkrClass> xkrClasses = classAgent.getAllChildClassByClassId(classId);

        build(list, xkrClasses);

        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public boolean updateClassNameById(Long classId, String className) {
        return classAgent.updateClassNameByClassId(classId, className);
    }

    public boolean deleteClassByClassId(Long classId) {
        return classAgent.deleteClassByClassId(classId);
    }

    public boolean saveNewClass(String className, Long parentClassId) {
        return classAgent.saveNewClass(className, parentClassId);
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
