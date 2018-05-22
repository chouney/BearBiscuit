package com.xkr.dao.mapper;

import com.xkr.domain.entity.XkrAdminOptLog;
import com.xkr.util.CustomerMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface XkrAdminOptLogMapper extends CustomerMapper<XkrAdminOptLog> {
    List<XkrAdminOptLog> getAllOptLogByAdminAccount(Long adminAccount);

    Integer batchUpdateOptLogByIds(Map<String,Object> params);
}