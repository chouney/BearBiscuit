package com.xkr.core.shiro.admin;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.springframework.stereotype.Component;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/3
 */
public class AdminPermissionResolver implements PermissionResolver {

    @Override
    public Permission resolvePermission(String permissionId) {
        return new AdminPermission(permissionId);
    }
}
