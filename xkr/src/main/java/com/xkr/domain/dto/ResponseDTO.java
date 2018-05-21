package com.xkr.domain.dto;

import com.xkr.common.ErrorStatus;

import java.io.Serializable;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
public class ResponseDTO<T> extends BaseDTO implements Serializable{


    private static final long serialVersionUID = 8771546288050976990L;

    public ResponseDTO() {
    }

    public ResponseDTO(T data) {
        this.data = data;
    }

    public ResponseDTO(ErrorStatus status) {
        super(status);
    }

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
