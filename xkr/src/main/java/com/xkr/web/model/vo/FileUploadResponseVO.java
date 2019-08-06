package com.xkr.web.model.vo;

import java.io.Serializable;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/18
 */
public class FileUploadResponseVO implements Serializable{

    private static final long serialVersionUID = -4633516381238394022L;

    private String authorization;

    private String date ;

    private String dirUri ;

    private String policy ;

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDirUri() {
        return dirUri;
    }

    public void setDirUri(String dirUri) {
        this.dirUri = dirUri;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    //    private String compressMd5;
//
//    private String unCompressMd5;
//
//    private String fileName;
//
//    private String imageMd5;
//
//    public FileUploadResponseVO() {
//    }
//
//
//    public String getCompressMd5() {
//        return compressMd5;
//    }
//
//    public void setCompressMd5(String compressMd5) {
//        this.compressMd5 = compressMd5;
//    }
//
//    public String getFileName() {
//        return fileName;
//    }
//
//    public void setFileName(String fileName) {
//        this.fileName = fileName;
//    }
//
//    public String getUnCompressMd5() {
//        return unCompressMd5;
//    }
//
//    public void setUnCompressMd5(String unCompressMd5) {
//        this.unCompressMd5 = unCompressMd5;
//    }
//
//    public String getImageMd5() {
//        return imageMd5;
//    }
//
//    public void setImageMd5(String imageMd5) {
//        this.imageMd5 = imageMd5;
//    }
}
