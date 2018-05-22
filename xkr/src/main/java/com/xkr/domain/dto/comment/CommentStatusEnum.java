package com.xkr.domain.dto.comment;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/25
 */
public enum CommentStatusEnum {
    STATUS_NORMAL(1,"正常"),
    STATUS_TOVERIFY(2,"待验证"),
    STATUS_DELETED(3,"删除"),
    ;
    private int code;

    private String desc;

    CommentStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 非删除状态集合
     */
    public static List<CommentStatusEnum> NON_DELETE_STATUSED = ImmutableList.of(
            STATUS_NORMAL,STATUS_TOVERIFY
    );

    /**
     * 带修改状态集合
     */
    public static List<CommentStatusEnum> TOUPDATE_STATUSED = ImmutableList.of(
            STATUS_NORMAL,STATUS_DELETED
    );

    public static CommentStatusEnum getByCode(int code){
        for(CommentStatusEnum statusEnum : CommentStatusEnum.values()){
            if(statusEnum.code == code){
                return statusEnum;
            }
        }
        return null;
    }

}
