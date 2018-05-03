package com.xkr.domain;

import com.xkr.dao.mapper.XkrAdminAccountMapper;
import com.xkr.domain.entity.XkrAdminAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 *
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/2
 */
@Service
public class XkrAdminAccountAgent {

    @Autowired
    private XkrAdminAccountMapper xkrAdminAccountMapper;

    public XkrAdminAccount getAdminAccountByName(String accountName){
        if(StringUtils.isEmpty(accountName)){
            return null;
        }
        return xkrAdminAccountMapper.selectByAccountName(accountName);
    }
}
