package com.xkr.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xkr.common.ErrorStatus;
import com.xkr.common.OptEnum;
import com.xkr.common.OptLogModuleEnum;
import com.xkr.common.annotation.OptLog;
import com.xkr.domain.XkrClassAgent;
import com.xkr.domain.XkrResourceAgent;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.clazz.ClassMenuDTO;
import com.xkr.domain.dto.resource.ResourceStatusEnum;
import com.xkr.domain.entity.XkrClass;
import com.xkr.domain.entity.XkrResource;
import org.apache.ibatis.jdbc.RuntimeSqlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    private XkrResourceAgent xkrResourceAgent;

    public List<ClassMenuDTO> getAllClass() {
        List<ClassMenuDTO> list = Lists.newArrayList();

        List<XkrClass> xkrClasses = classAgent.getAllChildClassByClassId((long) XkrClassAgent.ROOT_CLASS_ID);

        build(list, xkrClasses);

        return list;
    }

    public ClassMenuDTO getAllChildClassByClassId(Long classId) {
        ClassMenuDTO result = new ClassMenuDTO();
        if (Objects.isNull(classId)) {
            logger.error("ClassService getAllChildClassByClassId invalid null param, will return empty list");
            result.setStatus(ErrorStatus.PARAM_ERROR);
            return result;
        }

        List<ClassMenuDTO> list = Lists.newArrayList();

        List<XkrClass> xkrClasses = classAgent.getAllChildClassByClassId(classId);

        build(list, xkrClasses);

        if (CollectionUtils.isEmpty(list)) {
            result.setStatus(ErrorStatus.ERROR);
            return result;
        }
        return list.get(0);
    }

    @OptLog(moduleEnum = OptLogModuleEnum.CLASSIFY,optEnum = OptEnum.UPDATE)
    public ResponseDTO<Boolean> updateClassNameById(Long classId, String className) {
        if(Objects.isNull(classId) || StringUtils.isEmpty(className)){
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        return new ResponseDTO<>(classAgent.updateClassNameByClassId(classId, className));
    }

    /**
     * 将搜索该分类下的资源移动至其父分类处,更新资源索引,再删除!!!
     * @param classId
     * @return
     */
    @OptLog(moduleEnum = OptLogModuleEnum.CLASSIFY,optEnum = OptEnum.DELETE)
    public ResponseDTO<Boolean> deleteClassByClassId(Long classId) {
        if(Objects.isNull(classId) || classId.equals(XkrClassAgent.ROOT_CLASS_ID)){
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        List<XkrClass> classList = classAgent.getAllChildClassByClassId(classId);
        Optional<XkrClass> optional = classList.stream().filter(xkrClass -> xkrClass.getId().equals(classId)).findFirst();
        if(!optional.isPresent()){
            return new ResponseDTO<>(ErrorStatus.ERROR);
        }
        XkrClass currentClass = optional.get();
        List<Long> ids = classList.stream().map(XkrClass::getId).collect(Collectors.toList());

        List<XkrResource> resources = xkrResourceAgent.getResourceListByClassIds(ids, ResourceStatusEnum.NON_DELETE_STATUSED.stream().map(ResourceStatusEnum::getCode).collect(Collectors.toList()));

        List<Long> resourceId = resources.stream().map(XkrResource::getId).collect(Collectors.toList());

        boolean isSuccess = xkrResourceAgent.batchUpdateResourceClassByIds(resourceId,currentClass.getParentClassId());
        if(!isSuccess){
            return new ResponseDTO<>(ErrorStatus.ERROR);
        }
        return new ResponseDTO<>(classAgent.deleteClassByClassId(classId));
    }

    /**
     * 将搜索该分类下的资源移动至其父分类处,更新资源索引,再删除!!!
     * @param classIds
     * @return
     */
    @OptLog(moduleEnum = OptLogModuleEnum.CLASSIFY,optEnum = OptEnum.DELETE)
    public ResponseDTO<Boolean> batchDeleteClassByClassId(List<Long> classIds) {
        if(CollectionUtils.isEmpty(classIds)){
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        classIds.forEach(this::deleteClassByClassId);
        return new ResponseDTO<>(true);
    }

    @OptLog(moduleEnum = OptLogModuleEnum.CLASSIFY,optEnum = OptEnum.INSERT)
    public ResponseDTO<Boolean> saveNewClass(String className, Long parentClassId) {
        if(StringUtils.isEmpty(className) || Objects.isNull(parentClassId)){
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        return new ResponseDTO<>(classAgent.saveNewClass(className, parentClassId));
    }


    private void build(List<ClassMenuDTO> result, List<XkrClass> xkrClasses) {
        Map<Long, ClassMenuDTO> tmpMap = Maps.newHashMap();
        xkrClasses.forEach(xkrClass -> {
            ClassMenuDTO menuDTO = new ClassMenuDTO();
            int totalCount = xkrResourceAgent.getResourceTotal(ResourceStatusEnum.NON_DELETE_STATUSED, ImmutableList.of(xkrClass.getId()));
            menuDTO.setClassId(xkrClass.getId());
            menuDTO.setClassName(xkrClass.getClassName());
            menuDTO.setCount(totalCount);
            tmpMap.put(menuDTO.getClassId(), menuDTO);
        });
        xkrClasses.forEach(xkrClass -> {
            ClassMenuDTO parent = tmpMap.get(xkrClass.getParentClassId());
            ClassMenuDTO current = tmpMap.get(xkrClass.getId());
            //如果为空,说明是根目录
            if (Objects.isNull(parent)) {
                result.add(current);
                return;
            }
            parent.setCount(parent.getCount()+current.getCount());
            parent.getChild().add(current);
        });
    }
}
