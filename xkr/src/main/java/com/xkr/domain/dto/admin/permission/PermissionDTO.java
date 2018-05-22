package com.xkr.domain.dto.admin.permission;

import java.io.Serializable;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class PermissionDTO implements Serializable{


    private static final long serialVersionUID = 7886828470280566647L;
    /**
     * “permissionId”:"1" //权限id
     "permissionName":"XXX",//权限名称
     */

    private Integer permissionId;

    private String permissionName;

    public Integer getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }
}
