package com.xkr.domain.dto.file;

import com.xkr.common.ErrorStatus;
import com.xkr.domain.dto.BaseDTO;

import java.io.Serializable;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/18
 */
public class FileUploadResponseDTO extends BaseDTO implements Serializable{

    private static final long serialVersionUID = -5011995111976061889L;
    private String compressMd5;

    private String fileName;

    private String imageMd5;

    public FileUploadResponseDTO(ErrorStatus status) {
        super(status);
    }

    public FileUploadResponseDTO(String compressMd5,String fileName) {
        this.compressMd5 = compressMd5;
        this.fileName = fileName;
    }

    public FileUploadResponseDTO(String imageMd5) {
        this.imageMd5 = imageMd5;
    }

    public String getCompressMd5() {
        return compressMd5;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setCompressMd5(String compressMd5) {
        this.compressMd5 = compressMd5;
    }

    public String getImageMd5() {
        return imageMd5;
    }

    public void setImageMd5(String imageMd5) {
        this.imageMd5 = imageMd5;
    }
}
