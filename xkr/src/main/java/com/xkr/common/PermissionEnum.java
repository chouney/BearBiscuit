package com.xkr.common;

import com.google.common.collect.ImmutableSet;

import java.util.Arrays;
import java.util.Set;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/7
 */
public enum PermissionEnum {

    REMARK_PERM("1", "留言管理"),
    USER_PERM("2", "会员管理"),
    RESOURCE_PERM("3", "资源管理"),
    DATAANY_PERM("4", "数据分析"),
    RESOURCE_REMOVE_PERM("5", "回收站"),
    COMMENT_PERM("6", "评论管理"),
    DESIGN_PERM("7", "毕设管理"),
    DATABASE_PERM("8", "数据库"),
    PAY_PERM("9", "支付管理"),
    LOG_PERM("10", "日志"),;
    private String permissionId;
    private String permissionName;

    PermissionEnum(String permissionId, String permissionName) {
        this.permissionId = permissionId;
        this.permissionName = permissionName;
    }

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

    public static PermissionEnum getByPermissionId(int permissionId) {
        for (PermissionEnum permissionEnum : PermissionEnum.values()) {
            if (permissionEnum.getPermissionId().equals(permissionId)) {
                return permissionEnum;
            }
        }
        return null;
    }

    public class Constant {
        public static final String REMARK_PERM = "1";
        public static final String USER_PERM = "2";
        public static final String RESOURCE_PERM = "3";
        public static final String DATAANY_PERM = "4";
        public static final String RESOURCE_REMOVE_PERM = "5";
        public static final String COMMENT_PERM = "6";
        public static final String DESIGN_PERM = "7";
        public static final String DATABASE_PERM = "8";
        public static final String PAY_PERM = "9";
        public static final String LOG_PERM = "10";
    }
}

