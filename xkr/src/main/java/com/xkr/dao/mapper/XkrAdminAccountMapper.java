package com.xkr.dao.mapper;

import com.xkr.domain.entity.XkrAdminAccount;
import com.xkr.util.CustomerMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface XkrAdminAccountMapper extends CustomerMapper<XkrAdminAccount> {

//    /**
//     * 目前role_ids字段只存储单个角色id
//     * @param roleId
//     * @return
//     */
//    List<XkrAdminAccount> getAdminAccountByRoleId(Integer roleId);

    Integer updateAdminAccountById(Map<String,Object> params);

    Integer batchUpdateAdminAccountByIds(Map<String,Object> param);

    XkrAdminAccount selectByAccountName(String accountName);

    XkrAdminAccount getById(Long id);

    List<XkrAdminAccount> selectList();

    List<XkrAdminAccount> getListByIds(List<Long> list);

}