package com.xkr.domain.dto.user;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/25
 */
public enum UserStatusEnum {
    USER_STATUS_NORMAL(1,"正常状态"),
    USER_STATUS_UNVERIFIED(2,"待验证"),
    USER_STATUS_FREEZED(3,"冻结状态"),
    USER_STATUS_DELETE(-1,"删除状态"),
    ;
    private int code;

    private String desc;

    UserStatusEnum(int code, String desc) {
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
    public static List<UserStatusEnum> NON_DELETE_STATUSED = ImmutableList.of(
            USER_STATUS_NORMAL,USER_STATUS_UNVERIFIED,USER_STATUS_FREEZED
    );

    /**
     * 带修改状态集合
     */
    public static List<UserStatusEnum> TOUPDATE_STATUSED = ImmutableList.of(
            USER_STATUS_DELETE,USER_STATUS_NORMAL,USER_STATUS_FREEZED
    );

    public static UserStatusEnum getByCode(int code){
        for(UserStatusEnum statusEnum : UserStatusEnum.values()){
            if(statusEnum.code == code){
                return statusEnum;
            }
        }
        return null;
    }

}
