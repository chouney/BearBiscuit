package com.xkr.common;

import com.xkr.core.compress.MyRar;
import com.xkr.core.compress.MyTar;
import com.xkr.core.compress.MyZip;

import java.util.Arrays;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/10
 */
public enum FileTypeEnum {

    DIR(-1,"-"),
    RAR(0,"rar", MyRar.class),
    TAR(1,"tar", MyTar.class),
    ZIP(2,"zip", MyZip.class),
    PNG(10,"png"),
    JSP(11,"jpg"),
    JPEG(12,"jpeg"),
    GIF(13,"gif"),

    ;
    private int code;
    private String desc;
    private Class processorClazz;

    FileTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    FileTypeEnum(int code, String desc, Class processorClazz) {
        this.code = code;
        this.desc = desc;
        this.processorClazz = processorClazz;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public Class getProcessorClazz() {
        return processorClazz;
    }

    public static FileTypeEnum getEnumByFileDesc(String s){
        return Arrays.stream(FileTypeEnum.values()).
                filter(fileTypeEnum -> fileTypeEnum.getDesc().equals(s)).
                findFirst().orElse(null);
    }
}
