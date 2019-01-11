package com.xkr.domain.dto.resource;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/25
 */
public enum ResourceStatusEnum {
    STATUS_NORMAL(1, "正常"),
    STATUS_UNVERIFIED(2, "待审核"),
    STATUS_FREEZED(3, "冻结"),
    STATUS_DELETED(4, "删除"),

    ;
    private int code;

    private String desc;

    ResourceStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 非删除状态集合
     */
    public static List<ResourceStatusEnum> NON_DELETE_STATUSED = ImmutableList.of(
            STATUS_NORMAL, STATUS_FREEZED,STATUS_UNVERIFIED
    );

    /**
     * 带修改状态集合
     */
    public static List<ResourceStatusEnum> TOUPDATE_STATUSED = ImmutableList.of(
            STATUS_NORMAL, STATUS_FREEZED,STATUS_DELETED
    );

    public static ResourceStatusEnum getByCode(int code) {
        for (ResourceStatusEnum statusEnum : ResourceStatusEnum.values()) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
