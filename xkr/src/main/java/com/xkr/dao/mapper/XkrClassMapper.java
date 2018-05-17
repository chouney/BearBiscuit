package com.xkr.dao.mapper;

import com.xkr.domain.entity.XkrClass;
import com.xkr.util.CustomerMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface XkrClassMapper extends CustomerMapper<XkrClass> {

    List<XkrClass> getClassByClassIds(List<Long> classIds);

    List<XkrClass> getAllChildClassByClassId(Long classId);

    List<XkrClass> getAll();

    Integer deleteClassByClassId(Long classId);
}