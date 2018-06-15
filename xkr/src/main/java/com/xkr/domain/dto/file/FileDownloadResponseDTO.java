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

    private String downloadUrl;

    private String date;

    public FileDownloadResponseDTO(String token, String downloadUrl, String date) {
        this.token = token;
        this.downloadUrl = downloadUrl;
        this.date = date;
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

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
