package com.xkr.domain.dto.message;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/25
 */
public enum MessageStatusEnum {

    MESSAGE_STATUS_UNREAD(1, "未读消息"),
    MESSAGE_STATUS_READ(2, "已读消息"),
    MESSAGE_STATUS_DELETE(3, "删除"),;

    private int code;

    private String desc;

    MessageStatusEnum(int code, String desc) {
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
    public static List<MessageStatusEnum> NON_DELETE_STATUSED = ImmutableList.of(
            MESSAGE_STATUS_UNREAD, MESSAGE_STATUS_READ
    );

    public static MessageStatusEnum getByCode(int code) {
        for (MessageStatusEnum statusEnum : MessageStatusEnum.values()) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
