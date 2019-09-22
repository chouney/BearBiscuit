package com.xkr.common;

import cn.beecloud.BCEumeration;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/7
 */
public enum PaymentEnum {

    ALIPAY_PC_ENUM(1,"支付宝pc", BCEumeration.PAY_CHANNEL.ALI_WEB),
    WECHAT_SCAN_ENUM(2,"微信扫码", BCEumeration.PAY_CHANNEL.WX_NATIVE),
    ;
    private int code;
    private String desc;
    private BCEumeration.PAY_CHANNEL payChannel;

    PaymentEnum(int code, String desc,BCEumeration.PAY_CHANNEL payChannel) {
        this.code = code;
        this.desc = desc;
        this.payChannel = payChannel;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public BCEumeration.PAY_CHANNEL getPayChannel() {
        return payChannel;
    }

    public static BCEumeration.PAY_CHANNEL getChannelByCode(int channelCode){
        PaymentEnum paymentEnum =  Arrays.stream(PaymentEnum.values()).filter(s -> channelCode == s.getCode()).findAny().orElse(null);
        return Objects.isNull(paymentEnum) ? null : paymentEnum.getPayChannel();
    }
}

