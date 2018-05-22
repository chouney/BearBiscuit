package com.xkr.dao.mapper;

import com.xkr.domain.entity.XkrAdminRole;
import com.xkr.util.CustomerMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface XkrAdminRoleMapper extends CustomerMapper<XkrAdminRole> {

    Integer batchUpdateRoleByIds(Map<String,Object> params);

    List<XkrAdminRole> selectList();

    Integer updateRoleById(Map<String,Object> params);

    List<XkrAdminRole> selectByIds(List<Integer> list);

    XkrAdminRole selectById(Integer id);

}