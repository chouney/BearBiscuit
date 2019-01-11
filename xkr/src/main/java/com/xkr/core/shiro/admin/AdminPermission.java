package com.xkr.core.shiro.admin;

import org.apache.shiro.authz.Permission;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/3
 */
public class AdminPermission implements Permission {

    private String permissionId;

    public AdminPermission(String permissionId) {
        this.permissionId = permissionId;
    }

    @Override
    public boolean implies(Permission permission) {
        if(!(permission instanceof AdminPermission)) {
            return false;
        }
        AdminPermission other = (AdminPermission) permission;

        if(!other.permissionId.equals(this.permissionId)){
            return false;
        }

        return true;
    }
}
