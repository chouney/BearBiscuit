package com.xkr.dao.mapper;

import com.xkr.domain.entity.XkrAdminAccount;
import com.xkr.util.CustomerMapper;
import org.springframework.stereotype.Service;

@Service
public interface XkrAdminAccountMapper extends CustomerMapper<XkrAdminAccount> {
    XkrAdminAccount selectByAccountName(String accountName);

}