package com.xkr.domain.dto.remark;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/25
 */
public enum RemarkStatusEnum {

    STATUS_NORMAL_USER_REMARK(1, "用户留言"),
    STATUS_NORMAL_ADMIN_REPLY(2, "管理员回复"),
    STATUS_DELETED(3, "删除"),;

    private int code;

    private String desc;

    RemarkStatusEnum(int code, String desc) {
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
    public static List<RemarkStatusEnum> NON_DELETE_STATUSED = ImmutableList.of(
            STATUS_NORMAL_USER_REMARK, STATUS_NORMAL_ADMIN_REPLY
    );

    public static RemarkStatusEnum getByCode(int code) {
        for (RemarkStatusEnum statusEnum : RemarkStatusEnum.values()) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
