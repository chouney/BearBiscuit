package com.xkr.domain.dto.file;

import java.io.Serializable;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/18
 */
public class FileUploadResponseDTO implements Serializable{

    private static final long serialVersionUID = -5011995111976061889L;
    private String compressMd5;

    private String unCompressMd5;

    private String imageMd5;

    public FileUploadResponseDTO(String compressMd5, String unCompressMd5) {
        this.compressMd5 = compressMd5;
        this.unCompressMd5 = unCompressMd5;
    }

    public FileUploadResponseDTO(String imageMd5) {
        this.imageMd5 = imageMd5;
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
