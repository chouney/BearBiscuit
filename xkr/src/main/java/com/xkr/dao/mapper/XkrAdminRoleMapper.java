package com.xkr.dao.mapper;

import com.xkr.domain.entity.XkrAdminRole;
import com.xkr.util.CustomerMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface XkrAdminRoleMapper extends CustomerMapper<XkrAdminRole> {
    List<XkrAdminRole> selectByIds(List<Integer> ids);

}