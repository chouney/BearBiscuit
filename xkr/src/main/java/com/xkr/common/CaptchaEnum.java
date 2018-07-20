package com.xkr.common;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/7
 */
public enum CaptchaEnum {

    LOGIN_TYPE(1,"登录类型"),
    UPDATE_PASS_TYPE(2,"更新密码类型"),
    UPLOAD_RES_TYPE(3,"上传资源类型"),
//    REG_TYPE(4,"注册"),


    ;
    private int code;
    private String desc;

    CaptchaEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static CaptchaEnum getByCode(int code){
        for(CaptchaEnum captchaEnum : CaptchaEnum.values()){
            if(captchaEnum.getCode() == code) {
                return captchaEnum;
            }
        }
        return null;
    }
}

