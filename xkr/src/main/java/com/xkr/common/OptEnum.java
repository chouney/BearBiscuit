package com.xkr.common;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/7
 */
public enum OptEnum {

    INSERT(1,"添加"),
    DELETE(2,"删除"),
    UPDATE(3,"修改"),
    QUERY(4,"查询"),
    RENEW(5,"回收站还原"),
    CLEAR(6,"回收站删除"),


    ;
    private int code;
    private String desc;

    OptEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static OptEnum getByCode(int code){
        for(OptEnum optLogModuleEnum : OptEnum.values()){
            if(optLogModuleEnum.getCode() == code) {
                return optLogModuleEnum;
            }
        }
        return null;
    }
}

