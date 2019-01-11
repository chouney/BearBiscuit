//package com.xkr.core.shiro.admin;
//
//import com.google.common.collect.Lists;
//import com.xkr.domain.XkrAdminRoleAgent;
//import com.xkr.domain.entity.XkrAdminRole;
//import org.apache.shiro.authz.Permission;
//import org.apache.shiro.authz.permission.RolePermissionResolver;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.List;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
///**
// * @author chriszhang
// * @version 1.0
// * @date 2018/5/3
// */
//public class AdminRolePermissionResolver implements RolePermissionResolver {
//
//    @Autowired
//    private XkrAdminRoleAgent adminRoleAgent;
//
//
//    @Override
//    public Collection<Permission> resolvePermissionsInRole(String roleString) {
//        List<Permission> permissions = Lists.newArrayList();
//
//        XkrAdminRole xkrAdminRole = adminRoleAgent.getAdminRoleById(Integer.valueOf(roleString));
//        if(Objects.isNull(xkrAdminRole)){
//            return permissions;
//        }
//        permissions = Arrays.stream(xkrAdminRole.getPermissionIds().split(";")).
//                map(id -> new AdminPermission(Integer.valueOf(id))).collect(Collectors.toList());
//
//
//        return permissions;
//    }
//}
