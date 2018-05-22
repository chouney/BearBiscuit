package com.xkr.domain.dto.admin.role;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class AdminRoleDTO implements Serializable{
    private static final long serialVersionUID = 8921006659763417678L;


    /**
     * “roleId”:"2" //角色id
     "roleName":"管理员账号",
     "roleDetail":"XXXX", //角色描述
     */

    private Integer roleId;

    private String roleName;

    private String roleDetail;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDetail() {
        return roleDetail;
    }

    public void setRoleDetail(String roleDetail) {
        this.roleDetail = roleDetail;
    }
}
