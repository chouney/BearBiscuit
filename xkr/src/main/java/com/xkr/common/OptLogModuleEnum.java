package com.xkr.common;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/7
 */
public enum OptLogModuleEnum {

    USER(1,"会员"),
    RESOURCE(2,"资源"),
    CLASSIFY(3,"栏目"),
    ORDER(4,"支付"),
    COMMENT(5,"评论"),
    REMARK(6,"留言"),
    ADMIN(7,"管理后台"),
    BACKUP(8,"备份"),
    OPT_LOG(9,"操作日志"),


    ;
    private int code;
    private String desc;

    OptLogModuleEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static OptLogModuleEnum getByCode(int code){
        for(OptLogModuleEnum optLogModuleEnum : OptLogModuleEnum.values()){
            if(optLogModuleEnum.getCode() == code) {
                return optLogModuleEnum;
            }
        }
        return null;
    }
}

