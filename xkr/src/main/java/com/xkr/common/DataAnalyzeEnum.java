package com.xkr.common;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/7
 */
public enum DataAnalyzeEnum {

    SEARCH("1", "搜索"),
    DOWNLOAD("2", "下载"),
    ;
    private String code;
    private String desc;

    DataAnalyzeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static DataAnalyzeEnum getByPermissionId(String code) {
        for (DataAnalyzeEnum dataAnalyzeEnum : DataAnalyzeEnum.values()) {
            if (dataAnalyzeEnum.getCode().equals(code)) {
                return dataAnalyzeEnum;
            }
        }
        return null;
    }
}

