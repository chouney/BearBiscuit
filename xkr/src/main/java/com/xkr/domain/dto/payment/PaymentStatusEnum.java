package com.xkr.domain.dto.payment;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/25
 */
public enum PaymentStatusEnum {
    STATUS_WAIT_PAY(1, "待支付"),
    STATUS_PAYED(2, "已支付"),
    STATUS_PAY_FAIL(3, "支付失败"),
    STATUS_DELETED(-1, "删除"),

    ;
    private int code;

    private String desc;

    PaymentStatusEnum(int code, String desc) {
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
    public static List<PaymentStatusEnum> NON_DELETE_STATUSED = ImmutableList.of(
            STATUS_WAIT_PAY, STATUS_PAYED,STATUS_PAY_FAIL
    );

    /**
     * 正常状态集合
     */
    public static List<PaymentStatusEnum> NORMAL_STATUSED = ImmutableList.of(
            STATUS_WAIT_PAY, STATUS_PAYED
    );


    public static PaymentStatusEnum getByCode(int code) {
        for (PaymentStatusEnum statusEnum : PaymentStatusEnum.values()) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
