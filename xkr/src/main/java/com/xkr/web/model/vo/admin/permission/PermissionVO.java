package com.xkr.web.model.vo.admin.permission;

import java.io.Serializable;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class PermissionVO implements Serializable{


    private static final long serialVersionUID = 1809468187098364211L;
    /**
     * “permissionId”:"1" //权限id
     "permissionName":"XXX",//权限名称
     */

    private String permissionId;

    private String permissionName;

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }
}
