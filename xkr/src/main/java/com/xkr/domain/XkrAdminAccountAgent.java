package com.xkr.domain;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.xkr.core.IdGenerator;
import com.xkr.dao.mapper.XkrAdminAccountMapper;
import com.xkr.domain.entity.XkrAdminAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/2
 */
@Service
public class XkrAdminAccountAgent {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XkrAdminAccountMapper xkrAdminAccountMapper;

    @Autowired
    private IdGenerator idGenerator;

    public static final int STATUS_NORMAL = 1;

    public static final int STATUS_DELETED = 2;

    public XkrAdminAccount getById(Long accountId){
        if(Objects.isNull(accountId)){
            return null;
        }
        return xkrAdminAccountMapper.getById(accountId);
    }

    public XkrAdminAccount getAdminAccountByName(String accountName){
        if(StringUtils.isEmpty(accountName)){
            return null;
        }
        return xkrAdminAccountMapper.selectByAccountName(accountName);
    }

    public List<XkrAdminAccount> getAdminAccountByRoleId(Integer roleId){
        if(Objects.isNull(roleId)){
            return Lists.newArrayList();
        }
        return xkrAdminAccountMapper.getAdminAccountByRoleId(roleId);
    }

    public List<XkrAdminAccount> getListByIds(List<Long> ids){
        if(CollectionUtils.isEmpty(ids)){
            return Lists.newArrayList();
        }
        return xkrAdminAccountMapper.getListByIds(ids);
    }

    public List<XkrAdminAccount> selectList(){
        return xkrAdminAccountMapper.selectList();
    }

    public XkrAdminAccount saveNewAdminAccount(String accountName,
                                    String accountToken,String email,String roleIds){
        if(StringUtils.isEmpty(accountName) || StringUtils.isEmpty(accountToken)
                || StringUtils.isEmpty(email) || StringUtils.isEmpty(roleIds)){
            return null;
        }
        XkrAdminAccount adminAccount = new XkrAdminAccount();
        adminAccount.setId(idGenerator.generateId());
        adminAccount.setAccountName(accountName);
        adminAccount.setAccountToken(accountToken);
        adminAccount.setEmail(email);
        adminAccount.setRoleIds(roleIds);
        adminAccount.setStatus((byte)STATUS_NORMAL);
        if(xkrAdminAccountMapper.insert(adminAccount) == 1){
            return adminAccount;
        }
        return null;
    }

    public boolean updateAdminAccountById(Long adminAccountId,String accountName,
                                          String accountToken,String email,String roleIds){
        if(Objects.isNull(adminAccountId)){
            return false;
        }
        return xkrAdminAccountMapper.updateAdminAccountById(ImmutableMap.of(
                "id",adminAccountId,
                "accountName",accountName,
                "accountToken",accountToken,
                "email",email,
                "roleIds",roleIds
        )) == 1;
    }

    public boolean batchUpdateAdminAccountByIds(List<Long> ids,Integer status){
        if(CollectionUtils.isEmpty(ids) || Objects.isNull(status)){
            return false;
        }
        return xkrAdminAccountMapper.batchUpdateAdminAccountByIds(ImmutableMap.of(
                "ids",ids,"status",status
        )) == 1;
    }
}
