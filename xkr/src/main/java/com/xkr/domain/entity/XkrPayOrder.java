package com.xkr.domain.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "xkr_pay_order")
public class XkrPayOrder {
    /**
     * 账单id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 支付类型,微信,支付宝
     */
    @Column(name = "pay_type_code")
    private Byte payTypeCode;

    /**
     * 交易类型,扫码,app等
     */
    @Column(name = "trade_type")
    private Byte tradeType;

    /**
     * 业务订单号
     */
    @Column(name = "pay_order_no")
    private String payOrderNo;

    /**
     * 预支付订单号
     */
    @Column(name = "pre_pay_id")
    private String prePayId;

    /**
     * 支付订单号
     */
    @Column(name = "pay_id")
    private String payId;

    /**
     * 客户端ip
     */
    @Column(name = "client_ip")
    private String clientIp;

    /**
     * 支付金额,分为单位
     */
    @Column(name = "pay_amount")
    private Long payAmount;

    /**
     * 订单状态
     */
    private Byte status;

    /**
     * 二维码url
     */
    @Column(name = "code_url")
    private String codeUrl;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 过期时间
     */
    @Column(name = "expire_time")
    private Date expireTime;

    /**
     * 付款时间
     */
    @Column(name = "pay_time")
    private Date payTime;

    /**
     * 扩展字段,包含支付结果信息,买家账号信息,订单详情信息等
     */
    private String ext;

    /**
     * 获取账单id
     *
     * @return id - 账单id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置账单id
     *
     * @param id 账单id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取用户id
     *
     * @return user_id - 用户id
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置用户id
     *
     * @param userId 用户id
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取支付类型,微信,支付宝
     *
     * @return pay_type_code - 支付类型,微信,支付宝
     */
    public Byte getPayTypeCode() {
        return payTypeCode;
    }

    /**
     * 设置支付类型,微信,支付宝
     *
     * @param payTypeCode 支付类型,微信,支付宝
     */
    public void setPayTypeCode(Byte payTypeCode) {
        this.payTypeCode = payTypeCode;
    }

    /**
     * 获取交易类型,扫码,app等
     *
     * @return trade_type - 交易类型,扫码,app等
     */
    public Byte getTradeType() {
        return tradeType;
    }

    /**
     * 设置交易类型,扫码,app等
     *
     * @param tradeType 交易类型,扫码,app等
     */
    public void setTradeType(Byte tradeType) {
        this.tradeType = tradeType;
    }

    /**
     * 获取业务订单号
     *
     * @return pay_order_no - 业务订单号
     */
    public String getPayOrderNo() {
        return payOrderNo;
    }

    /**
     * 设置业务订单号
     *
     * @param payOrderNo 业务订单号
     */
    public void setPayOrderNo(String payOrderNo) {
        this.payOrderNo = payOrderNo;
    }

    /**
     * 获取预支付订单号
     *
     * @return pre_pay_id - 预支付订单号
     */
    public String getPrePayId() {
        return prePayId;
    }

    /**
     * 设置预支付订单号
     *
     * @param prePayId 预支付订单号
     */
    public void setPrePayId(String prePayId) {
        this.prePayId = prePayId;
    }

    /**
     * 获取支付订单号
     *
     * @return pay_id - 支付订单号
     */
    public String getPayId() {
        return payId;
    }

    /**
     * 设置支付订单号
     *
     * @param payId 支付订单号
     */
    public void setPayId(String payId) {
        this.payId = payId;
    }

    /**
     * 获取客户端ip
     *
     * @return client_ip - 客户端ip
     */
    public String getClientIp() {
        return clientIp;
    }

    /**
     * 设置客户端ip
     *
     * @param clientIp 客户端ip
     */
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    /**
     * 获取支付金额,分为单位
     *
     * @return pay_amount - 支付金额,分为单位
     */
    public Long getPayAmount() {
        return payAmount;
    }

    /**
     * 设置支付金额,分为单位
     *
     * @param payAmount 支付金额,分为单位
     */
    public void setPayAmount(Long payAmount) {
        this.payAmount = payAmount;
    }

    /**
     * 获取订单状态
     *
     * @return status - 订单状态
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置订单状态
     *
     * @param status 订单状态
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * 获取二维码url
     *
     * @return code_url - 二维码url
     */
    public String getCodeUrl() {
        return codeUrl;
    }

    /**
     * 设置二维码url
     *
     * @param codeUrl 二维码url
     */
    public void setCodeUrl(String codeUrl) {
        this.codeUrl = codeUrl;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取过期时间
     *
     * @return expire_time - 过期时间
     */
    public Date getExpireTime() {
        return expireTime;
    }

    /**
     * 设置过期时间
     *
     * @param expireTime 过期时间
     */
    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    /**
     * 获取付款时间
     *
     * @return pay_time - 付款时间
     */
    public Date getPayTime() {
        return payTime;
    }

    /**
     * 设置付款时间
     *
     * @param payTime 付款时间
     */
    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    /**
     * 获取扩展字段,包含支付结果信息,买家账号信息,订单详情信息等
     *
     * @return ext - 扩展字段,包含支付结果信息,买家账号信息,订单详情信息等
     */
    public String getExt() {
        return ext;
    }

    /**
     * 设置扩展字段,包含支付结果信息,买家账号信息,订单详情信息等
     *
     * @param ext 扩展字段,包含支付结果信息,买家账号信息,订单详情信息等
     */
    public void setExt(String ext) {
        this.ext = ext;
    }
}