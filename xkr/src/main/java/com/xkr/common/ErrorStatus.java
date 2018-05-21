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

    //资源
    RESOURCE_NOT_FOUND(10404,"资源未找到"),
    RESOURCE_FREEZED(10400,"资源已被冻结"),
    RESOURCE_USER_FREEZED(10401,"您无此权限"),
    RESOURCE_PAY_FAILED(10402,"财富值不足"),


    //用户
    USER_NAME_ALREADY_EXIST(11401,"用户名已注册"),
    USER_EMAIL_ALREADY_EXIST(11402,"邮箱已注册"),
    USER_NOT_EXIST(11410,"用户不存在"),
    USER_ALREADY_FREEZED(11411,"用户已冻结"),
    USER_ALREADY_ACTIVE(11412,"用户已激活"),

    //评论
    COMMENT_USER_NOT_PRILIVEGED(12400,"用户无评论权限"),

    //留言
    REMARK_USER_NOT_PRILIVEGED(12500,"用户无留言权限"),


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

