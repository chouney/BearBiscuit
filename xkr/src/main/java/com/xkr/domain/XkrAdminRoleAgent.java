package com.xkr.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.xkr.dao.mapper.XkrAdminRoleMapper;
import com.xkr.domain.entity.XkrAdminRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public static final String ROLE_DETAIL_EXT_KEY = "roleDetail";

    public static final int STATUS_NORMARL = 1;

    public static final int STATUS_DELETED = 2;


    public XkrAdminRole saveNewAdminRole(String roleName,String roleDetail,List<Integer> permissionIds){
        if(StringUtils.isEmpty(roleDetail)|| StringUtils.isEmpty(roleName) || CollectionUtils.isEmpty(permissionIds)){
            return null;
        }
        XkrAdminRole adminRole = new XkrAdminRole();
        JSONObject ext = new JSONObject();
        ext.put(ROLE_DETAIL_EXT_KEY,roleDetail);
        adminRole.setPermissionIds(String.join(";",permissionIds.stream().map(String::valueOf).collect(Collectors.toList())));
        adminRole.setRoleName(roleName);
        adminRole.setStatus((byte)STATUS_NORMARL);
        adminRole.setExt(ext.toJSONString());
        boolean isSuccess = xkrAdminRoleMapper.insertSelective(adminRole) == 1;
        if(isSuccess){
            return adminRole;
        }
        return null;

    }

    public boolean batchUpdateRoleByIds(List<Integer> roleIds,Integer status){
        if(CollectionUtils.isEmpty(roleIds) || Objects.isNull(status)){
            return false;
        }
        return xkrAdminRoleMapper.batchUpdateRoleByIds(ImmutableMap.of(
                "status",status,"ids",roleIds
        )) == 1;
    }

    public boolean updateRoleById(Integer roleId, String roleName,String roleDetail,List<Integer> pIds){
        if(StringUtils.isEmpty(roleDetail)|| StringUtils.isEmpty(roleName) || CollectionUtils.isEmpty(pIds)){
            return false;
        }
        XkrAdminRole role = getAdminRoleById(roleId);
        if(Objects.isNull(role)){
            return false;
        }
        JSONObject ext = JSON.parseObject(role.getExt());
        ext.put(ROLE_DETAIL_EXT_KEY,roleDetail);

        return xkrAdminRoleMapper.updateRoleById(ImmutableMap.of(
               "permissionIds",String.join(";",pIds.stream().map(String::valueOf).collect(Collectors.toList())),
                "roleName",roleName,
                "ext", ext.toJSONString(),
                "id",roleId
        )) == 1;
    }

    public List<XkrAdminRole> selectList(){
        return xkrAdminRoleMapper.selectList();
    }

    public List<XkrAdminRole> getAdminRoleByIds(List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        return xkrAdminRoleMapper.selectByIds(ids);
    }


    public XkrAdminRole getAdminRoleById(Integer id) {
        if (Objects.isNull(id)) {
            return null;
        }
        return xkrAdminRoleMapper.selectById(id);
    }



}
