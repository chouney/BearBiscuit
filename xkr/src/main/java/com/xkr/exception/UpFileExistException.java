package com.xkr.exception;

import main.java.com.upyun.UpException;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/9
 */
public class UpFileExistException extends UpException {
    public UpFileExistException(String msg) {
        super(msg);
    }
}
