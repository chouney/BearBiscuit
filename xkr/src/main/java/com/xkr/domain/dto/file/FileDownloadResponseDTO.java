package com.xkr.domain.dto.file;

import com.xkr.common.ErrorStatus;

import java.io.Serializable;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/18
 */
public class FileDownloadResponseDTO implements Serializable{

    private static final long serialVersionUID = -4833123953280423993L;
    private ErrorStatus status = ErrorStatus.OK;

    private String token;

    public FileDownloadResponseDTO(String token) {
        this.token = token;
    }

    public FileDownloadResponseDTO(ErrorStatus errorStatus) {
        this.status = errorStatus;
    }

    public ErrorStatus getStatus() {
        return status;
    }

    public void setStatus(ErrorStatus status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
