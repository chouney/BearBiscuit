package com.xkr.web.model.vo.payment;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.Map;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/11
 */
public class PaymentUpdateInVO implements Serializable {


    private static final long serialVersionUID = 5429925769602363445L;

    private String signature;
    private Long timestamp;
    private String amount;
    private Boolean trade_success;
    private String transaction_type;
    private String transaction_id;
    private Integer billFee;
    private Integer transaction_fee;
    private String channel_type;
    private JSONObject messageDetail;
    private JSONObject optional;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Boolean getTrade_success() {
        return trade_success;
    }

    public void setTrade_success(Boolean trade_success) {
        this.trade_success = trade_success;
    }

    public String getTransaction_type() {
        return transaction_type;
    }

    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }


    public JSONObject getMessageDetail() {
        return messageDetail;
    }

    public void setMessageDetail(JSONObject messageDetail) {
        this.messageDetail = messageDetail;
    }

    public JSONObject getOptional() {
        return optional;
    }

    public void setOptional(JSONObject optional) {
        this.optional = optional;
    }

    public Integer getBillFee() {
        return billFee;
    }

    public void setBillFee(Integer billFee) {
        this.billFee = billFee;
    }

    public Integer getTransaction_fee() {
        return transaction_fee;
    }

    public void setTransaction_fee(Integer transaction_fee) {
        this.transaction_fee = transaction_fee;
    }

    public String getChannel_type() {
        return channel_type;
    }

    public void setChannel_type(String channel_type) {
        this.channel_type = channel_type;
    }
}
