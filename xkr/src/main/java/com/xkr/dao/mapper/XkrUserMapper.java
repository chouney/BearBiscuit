package com.xkr.dao.mapper;

import com.xkr.domain.entity.XkrUser;
import com.xkr.util.CustomerMapper;
import org.springframework.stereotype.Service;

@Service
public interface XkrUserMapper extends CustomerMapper<XkrUser> {
    XkrUser selectByUserName(String userName);

    XkrUser selectByEmail(String email);
}