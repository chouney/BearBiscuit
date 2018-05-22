package com.xkr.dao.mapper;

import com.xkr.domain.entity.XkrResource;
import com.xkr.util.CustomerMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface XkrResourceMapper extends CustomerMapper<XkrResource> {

    Integer batchUpdateResourceClassByIds(Map<String,Object> params);

    XkrResource getResourceById(Map<String,Object> params);

    Integer batchDeleteResourceByIds(List<Long> list);

    Integer batchUpdateResourceByIds(Map<String,Object> params);

    List<XkrResource> getResourceByClassIds(Map<String,Object> params);

    List<XkrResource> getResourceByUserId(Map<String,Object> params);

    List<XkrResource> getResourceByIds(Map<String,Object> params);
}