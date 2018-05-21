package com.xkr.exception;


/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/9
 */
public class EmailExistException extends Exception {
    public EmailExistException() {
    }

    public EmailExistException(String msg) {
        super(msg);
    }
}
