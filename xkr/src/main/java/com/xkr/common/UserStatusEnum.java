package com.xkr.common;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/7
 */
public enum UserStatusEnum {

    NORMAL((byte)1,"正常"),
    UNAUTHORIZED((byte)2,"未验证"),
    FREEZE((byte)3,"冻结"),

    ;
    private byte code;
    private String desc;

    UserStatusEnum(byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public byte getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
