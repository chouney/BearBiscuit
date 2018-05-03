package com.xkr.domain.dto;

import com.xkr.util.DateUtil;
import org.springframework.format.datetime.DateFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/9
 */
public class FileInfoDTO {

    private boolean isFile;

    private String size;

    private String Date;

    public FileInfoDTO() {
    }

    public FileInfoDTO(Map<String,String> fileInfoMap) {

        this.isFile = "file".equals(fileInfoMap.get(FileParamEnum.TYPE.getParamKey()));
        this.size = fileInfoMap.get(FileParamEnum.SIZE.getParamKey());
        this.Date = fileInfoMap.get(FileParamEnum.DATE.getParamKey());
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public enum FileParamEnum {
        TYPE("type"),
        SIZE("size"),
        DATE("date"),;
        private String paramKey;

        FileParamEnum(String paramKey) {
            this.paramKey = paramKey;
        }

        public String getParamKey() {
            return paramKey;
        }
    }
}
