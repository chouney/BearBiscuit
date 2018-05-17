package com.xkr.domain.dto;

import com.xkr.common.ErrorStatus;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/18
 */
public class FileDownloadResponseDTO {

    private String msg;

    private int status;

    private String token;

    public FileDownloadResponseDTO(String token) {
        this.status = ErrorStatus.OK.getCode();
        this.msg = ErrorStatus.OK.getDesc();
        this.token = token;
    }

    public FileDownloadResponseDTO(ErrorStatus errorStatus) {
        this.status = errorStatus.getCode();
        this.msg = errorStatus.getDesc();
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
