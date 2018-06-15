package com.xkr.domain;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xkr.core.IdGenerator;
import com.xkr.dao.mapper.XkrAdminAccountMapper;
import com.xkr.domain.entity.XkrAdminAccount;
import com.xkr.util.EncodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
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
        logger.info("XkrAdminAccountAgent getById param:{}",accountId);
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
                                    String accountToken,String email,Integer roleId){
        if(StringUtils.isEmpty(accountName) || StringUtils.isEmpty(accountToken)
                || StringUtils.isEmpty(email) || Objects.isNull(roleId)){
            return null;
        }
        XkrAdminAccount adminAccount = new XkrAdminAccount();
        adminAccount.setId(idGenerator.generateId());
        adminAccount.setAccountName(accountName);
        adminAccount.setAccountToken(EncodeUtil.md5(accountToken));
        adminAccount.setEmail(email);
        adminAccount.setRoleId(roleId);
        adminAccount.setStatus((byte)STATUS_NORMAL);
        if(xkrAdminAccountMapper.insertSelective(adminAccount) == 1){
            return adminAccount;
        }
        return null;
    }

    public boolean updateAdminAccountById(Long adminAccountId,String accountName,
                                          String accountToken,String email,Integer roleId){
        if(Objects.isNull(adminAccountId)){
            return false;
        }
        Map<String,Object> param = Maps.newHashMap();
        param.put("id",adminAccountId);
        param.put("accountName",accountName);
        param.put("roleId",roleId);
        param.put("email",email);
        if(!StringUtils.isEmpty(accountToken)){
            param.put("accountToken",EncodeUtil.md5(accountToken));
        }
        return xkrAdminAccountMapper.updateAdminAccountById(param) == 1;
    }

    public boolean batchUpdateAdminAccountByIds(List<Long> ids,Integer status){
        if(CollectionUtils.isEmpty(ids) || Objects.isNull(status)){
            return false;
        }
        return xkrAdminAccountMapper.batchUpdateAdminAccountByIds(ImmutableMap.of(
                "ids",ids,"status",status
        )) > 0;
    }
}
