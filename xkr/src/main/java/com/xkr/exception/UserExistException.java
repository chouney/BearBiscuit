package com.xkr.exception;


/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/9
 */
public class UserExistException extends Exception {
    public UserExistException() {
    }

    public UserExistException(String msg) {
        super(msg);
    }
}
