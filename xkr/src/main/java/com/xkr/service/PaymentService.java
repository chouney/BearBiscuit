package com.xkr.service;

import cn.beecloud.BCEumeration;
import cn.beecloud.BCPay;
import cn.beecloud.bean.BCException;
import cn.beecloud.bean.BCOrder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xkr.common.ErrorStatus;
import com.xkr.common.PaymentEnum;
import com.xkr.core.IdGenerator;
import com.xkr.domain.XkrPayOrderAgent;
import com.xkr.domain.XkrUserAgent;
import com.xkr.domain.dto.BaseDTO;
import com.xkr.domain.dto.payment.PaymentDTO;
import com.xkr.domain.dto.payment.PaymentStatusEnum;
import com.xkr.domain.entity.XkrPayOrder;
import com.xkr.domain.entity.XkrUser;
import com.xkr.service.api.PaymentApiService;
import com.xkr.util.MoneyUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/15
 */
@Service
public class PaymentService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PaymentApiService paymentApiService;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private XkrPayOrderAgent xkrPayOrderAgent;

    @Autowired
    private XkrUserAgent xkrUserAgent;


    /**
     * 生成订单
     * @param amount
     * @param payTypeCode
     * @param returnUrl
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public PaymentDTO genBill(String clientIp,String amount,int payTypeCode,String returnUrl){
        PaymentDTO paymentDTO = new PaymentDTO();
        XkrUser xkrUser = (XkrUser) SecurityUtils.getSubject().getPrincipal();
        if(xkrUser == null){
            paymentDTO.setStatus(ErrorStatus.USER_NOT_EXIST);
            return paymentDTO;
        }
        //生成业务订单号
        String busOrderNo = "XKR"+idGenerator.generateId();
        BCOrder bcOrder = paymentApiService.genBill(busOrderNo,amount,payTypeCode,returnUrl);
        if(Objects.isNull(bcOrder)){
            logger.error("生成支付订单失败,clientIp:{},amount:{},payTypeCode,:{},returnUrl:{}",clientIp,amount,payTypeCode,returnUrl);
            paymentDTO.setStatus(ErrorStatus.ORDER_GENERATE_FAILED);
            return paymentDTO;
        }
        //插入订单流水
        if(xkrPayOrderAgent.insertPayOrder(xkrUser.getId(),payTypeCode,busOrderNo,"",clientIp,(int)MoneyUtil.yuan2fen(amount),bcOrder.getCodeUrl(),bcOrder.getBillTimeout())){
            paymentDTO.setHtml(bcOrder.getHtml());
            paymentDTO.setUrl(bcOrder.getUrl());
            paymentDTO.setCodeUrl(bcOrder.getCodeUrl());
            return paymentDTO;
        }
        paymentDTO.setStatus(ErrorStatus.ORDER_GENERATE_FAILED);
        return paymentDTO;
    }


    /**
     * 完成订单支付
     * @param orderId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public BaseDTO complatePay(String orderId, String transactionFee, JSONObject messageDetail){
        BaseDTO baseDTO = new BaseDTO();
        //获取订单详情
        XkrPayOrder order = xkrPayOrderAgent.getOrderByOrderId(orderId);
        //查看订单状态，是否为待支付或者已支付（保证幂等性）
        if(Objects.isNull(order) || !PaymentStatusEnum.NORMAL_STATUSED.contains(PaymentStatusEnum.getByCode(order.getStatus()))){
            baseDTO.setStatus(ErrorStatus.ORDER_SUBMIT_FAILED_STATUS);
            logger.error("生成支付订单失败,orderId:{},baseDTO:{}",orderId,JSON.toJSONString(baseDTO));
            return baseDTO;
        }
        //验证金额是否相等
        BigDecimal retFee = new BigDecimal(transactionFee);
        BigDecimal orderFee = new BigDecimal(order.getPayAmount());
        if(retFee.compareTo(orderFee) != 0){
            baseDTO.setStatus(ErrorStatus.ORDER_SUBMIT_UNCOMPAREABLE_WEALTH);
            logger.error("生成支付订单失败,orderId:{},baseDTO:{}",orderId, JSON.toJSONString(baseDTO));
            return baseDTO;
        }

        //获取支付用户信息
        XkrUser user = xkrUserAgent.getUserById(order.getUserId());
        if(Objects.isNull(user)){
            baseDTO.setStatus(ErrorStatus.ORDER_SUBMIT_FAILED_USER);
            logger.error("生成支付订单失败,orderId:{},baseDTO:{}",orderId,JSON.toJSONString(baseDTO));
            return baseDTO;
        }
        //更新支付金额
        user.setWealth(new BigDecimal(order.getPayAmount())
                .divide(BigDecimal.TEN).setScale(2,BigDecimal.ROUND_HALF_UP).longValue());
        if(!xkrUserAgent.updateUserByPKSelective(user)){
            baseDTO.setStatus(ErrorStatus.ORDER_SUBMIT_FAILED_USER_WEALTH);
            logger.error("生成支付订单失败,orderId:{},baseDTO:{}",orderId,JSON.toJSONString(baseDTO));
            return baseDTO;
        }
        BCEumeration.PAY_CHANNEL chanlType = PaymentEnum.getChannelByCode(order.getPayTypeCode());
        String tradeNo=null;
        if(BCEumeration.PAY_CHANNEL.ALI_WEB.equals(chanlType)){
            tradeNo = messageDetail.getString("tradeNo");
        }
        if(BCEumeration.PAY_CHANNEL.BC_WX_SCAN.equals(chanlType)){
            tradeNo = messageDetail.getString("transaction_id");
        }
        //更新订单状态
        if(!xkrPayOrderAgent.payOrderStatusByOrderId(orderId,tradeNo,messageDetail)){
            logger.error("生成支付订单失败,orderId:{},baseDTO:{}",orderId,JSON.toJSONString(baseDTO));
            throw new RuntimeException("更新订单失败");
        }
        return baseDTO;
    }


}
