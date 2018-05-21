package com.xkr.domain.dto;

import com.google.common.collect.Lists;
import com.xkr.common.ErrorStatus;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
public class BaseDTO implements Serializable{

    private ErrorStatus status = ErrorStatus.OK;

    public BaseDTO() {
    }

    public BaseDTO(ErrorStatus status) {
        this.status = status;
    }

    public ErrorStatus getStatus() {
        return status;
    }

    public void setStatus(ErrorStatus status) {
        this.status = status;
    }
}
