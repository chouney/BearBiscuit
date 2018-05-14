package com.xkr.domain.dto.search;

import java.util.Date;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/11
 */
public class OrderIndexDTO extends BaseIndexDTO {

    /**
     * "orderId":"账单id",
     * ”payAmount“:"充值金额,元",
     * "payOrderNo":"业务订单号",
     * "userName":"会员账号",
     * "userId":"会员id",
     * "payTime":"付款时间",
     * "payId":"第三方订单号"
     * "status":1 //支付状态
     */

    private Long orderId;

    private String payAmount;

    private String payOrderNo;

    private Long userId;

    private String userName;

    private Date payTime;

    private String payId;

    private Integer status;

    public OrderIndexDTO() {
        super("xkr", "order");
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }

    public String getPayOrderNo() {
        return payOrderNo;
    }

    public void setPayOrderNo(String payOrderNo) {
        this.payOrderNo = payOrderNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String getIndexKey() {
        return String.valueOf(this.orderId);
    }
}
