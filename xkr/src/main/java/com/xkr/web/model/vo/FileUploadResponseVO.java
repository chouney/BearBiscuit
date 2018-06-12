package com.xkr.web.model.vo;

import java.io.Serializable;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/18
 */
public class FileUploadResponseVO implements Serializable{

    private static final long serialVersionUID = -4633516381238394022L;
    private String compressMd5;

    private String unCompressMd5;

    private String imageMd5;

    public FileUploadResponseVO() {
    }


    public String getCompressMd5() {
        return compressMd5;
    }

    public void setCompressMd5(String compressMd5) {
        this.compressMd5 = compressMd5;
    }

    public String getUnCompressMd5() {
        return unCompressMd5;
    }

    public void setUnCompressMd5(String unCompressMd5) {
        this.unCompressMd5 = unCompressMd5;
    }

    public String getImageMd5() {
        return imageMd5;
    }

    public void setImageMd5(String imageMd5) {
        this.imageMd5 = imageMd5;
    }
}