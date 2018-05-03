package com.xkr.domain;

import com.xkr.dao.mapper.XkrAdminRoleMapper;
import com.xkr.domain.entity.XkrAdminAccount;
import com.xkr.domain.entity.XkrAdminRole;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/2
 */
@Service
public class XkrAdminRoleAgent {

    @Autowired
    private XkrAdminRoleMapper xkrAdminRoleMapper;

    @Autowired
    private XkrAdminAccountAgent xkrAdminAccountAgent;

    public List<XkrAdminRole> getAdminRoleByIds(List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.emptyList();
        }
        return xkrAdminRoleMapper.selectByIds(ids);
    }


    public XkrAdminRole getAdminRoleById(Integer id) {
        if (Objects.isNull(id)) {
            return null;
        }
        return xkrAdminRoleMapper.selectByPrimaryKey(id);
    }



}
