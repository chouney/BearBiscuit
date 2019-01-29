package com.xkr.common;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/4
 */
public class Const {

    public static final String SESSION_LOGIN_TOKEN_KEY = "SESSION_LOGIN_TOKEN_KEY";

    public static final String CAPTCHA_SESSION_KEY_BASE = "KAPTCHA_SESSION_KEY_";

    public static final String CAPTCHA_SESSION_DATE_BASE = "KAPTCHA_SESSION_DATE_";

    public static final String SESSION_COOKIE_NAME = "XKRSID";

    public static final String SESSION_REMEMBER_COOKIE_NAME = "XKRREMSID";

    public static final String SESSION_LOGIN_TYPE_KEY = "login_type";

    public static final String CSRF_TOKEN_PREFIX = "csrf_";

    public static final String CSRF_TOKEN_PARAM = "token";

    public static final int USER_ACCOUNT_VERIFY_TYPE_REG = 1;

    public static final int USER_ACCOUNT_VERIFY_TYPE_UPDATE_PASSWORD = 2;

    //邮箱验证时间长度(分钟)
    public static final int VALIDATE_SESSION_EXPIRE = 60;

}
