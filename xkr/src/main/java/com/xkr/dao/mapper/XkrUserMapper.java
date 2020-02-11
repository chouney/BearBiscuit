package com.xkr.dao.mapper;

import com.xkr.domain.entity.XkrUser;
import com.xkr.util.CustomerMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface XkrUserMapper extends CustomerMapper<XkrUser> {

    Integer getTotalUser(Map<String,Object> params);

    Integer batchUpdateUserByIds(Map<String,Object> params);

    XkrUser selectByUserName(Map<String,Object> params);

    XkrUser selectByEmail(Map<String,Object> params);

    List<XkrUser> getUserByIds(Map<String,Object> params);

    XkrUser getUserById(Map<String,Object> params);

    List<XkrUser> searchByFilter(Map<String,Object> params);

    XkrUser selectByEmailAndUserName(Map<String,Object> params);
}