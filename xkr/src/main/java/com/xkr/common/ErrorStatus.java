package com.xkr.common;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/7
 */
public enum ErrorStatus {

    OK(200,"操作成功"),
    UNLOGIN_REDIRECT(302,"用户未登录"),
    PARAM_ERROR(400,"参数错误"),
    UNAUTHORIZED(401,"未授权"),
    BASIC_AUTH_ERROR(403,"认证异常了"),
    CSRF_TOEKN_ERROR(403,"认证异常"),
    NOT_FOUND(404,"资源未找到"),
    ERROR(500,"服务器异常"),


    ;
    private int code;
    private String desc;

    ErrorStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

