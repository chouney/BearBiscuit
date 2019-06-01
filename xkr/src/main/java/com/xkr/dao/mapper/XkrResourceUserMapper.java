package com.xkr.dao.mapper;

import com.xkr.domain.entity.XkrResourceUser;
import com.xkr.util.CustomerMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface XkrResourceUserMapper extends CustomerMapper<XkrResourceUser> {

    List<XkrResourceUser> getResourceByUserId(Map<String,Object> params);

    List<XkrResourceUser> getResourceByResAndUserId(Map<String,Object> params);

}