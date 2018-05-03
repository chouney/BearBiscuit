package com.xkr.dao.mapper;

import com.xkr.domain.entity.XkrAdminPermission;
import com.xkr.util.CustomerMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface XkrAdminPermissionMapper extends CustomerMapper<XkrAdminPermission> {
    List<XkrAdminPermission> selectByIds(List<Integer> ids);
}