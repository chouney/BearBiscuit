package com.xkr.web.controller;

import cn.beecloud.BCEumeration;
import cn.beecloud.BCPay;
import cn.beecloud.BeeCloud;
import cn.beecloud.bean.BCAuth;
import cn.beecloud.bean.BCOrder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xkr.common.CaptchaEnum;
import com.xkr.common.ErrorStatus;
import com.xkr.common.PaymentEnum;
import com.xkr.common.annotation.NoBasicAuth;
import com.xkr.common.annotation.valid.Captcha;
import com.xkr.common.annotation.valid.IsNumberic;
import com.xkr.core.IdGenerator;
import com.xkr.domain.dto.BaseDTO;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.payment.PaymentDTO;
import com.xkr.domain.entity.XkrUser;
import com.xkr.service.PaymentService;
import com.xkr.service.RemarkService;
import com.xkr.util.IpUtil;
import com.xkr.util.MoneyUtil;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.payment.PaymentUpdateInVO;
import com.xkr.web.model.vo.payment.PaymentVO;
import org.apache.shiro.SecurityUtils;
import org.chris.redbud.validator.annotation.MethodValidate;
import org.chris.redbud.validator.result.ValidError;
import org.chris.redbud.validator.result.ValidResult;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@Controller
@RequestMapping("/api/payment")
public class PaymentController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PaymentService paymentService;



    @ResponseBody
    @MethodValidate
    @RequestMapping(value = "/pay" , method = {RequestMethod.POST})
    public BasicResult genPayOrder(
            HttpServletRequest request,
            @NotNull
            @RequestParam(name = "payTypeCode") Integer channelCode,
            @NotBlank
            @RequestParam(name = "returnUrl") String returnUrl,
            @IsNumberic
            @RequestParam(name = "amount") String amount,
            ValidResult result) {
        if (result.hasErrors()) {
            return new BasicResult(result);
        }

        if(Integer.parseInt(amount) % 10 != 0) {
            result = new ValidResult(400);
            result.getErrors().add(new ValidError("", "提交的金额必须为10的整数倍", amount));
            return new BasicResult(result);
        }

        try {
            PaymentVO paymentVO = new PaymentVO();
            PaymentDTO paymentDTO = paymentService.genBill(IpUtil.getIpAddr(request),amount,channelCode,returnUrl);
            if(!ErrorStatus.OK.equals(paymentDTO.getStatus())){
                return new BasicResult<>(paymentDTO.getStatus());
            }
            paymentVO.setCodeUrl(paymentDTO.getCodeUrl());
            paymentVO.setHtml(paymentDTO.getHtml());
            paymentVO.setUrl(paymentDTO.getUrl());
            return new BasicResult<>(paymentVO);
        } catch (Exception e) {
            logger.error("PaymentController genPayOrder error ,payTypeCode:{},returnUrl:{},amount:{}", channelCode, returnUrl, amount, e);
        }
        return new BasicResult(ErrorStatus.ERROR);
    }


    @NoBasicAuth
    @ResponseBody
    @RequestMapping(value = "/update" , method = {RequestMethod.POST},consumes = {"application/json"})
    public String updatePayOrder(
            HttpServletRequest request,
            @RequestBody PaymentUpdateInVO paymentUpdateInVO) {
        if (Objects.isNull(paymentUpdateInVO)) {
            return "error";
        }
        try {
            String orderId = paymentUpdateInVO.getTransaction_id();
            String transactionFee = String.valueOf(paymentUpdateInVO.getTransaction_fee());
            //进行签名验证
            if(BCPay.verifySign(paymentUpdateInVO.getSignature(),paymentUpdateInVO.getTransaction_id(),
                    paymentUpdateInVO.getTransaction_type(),paymentUpdateInVO.getChannel_type(),transactionFee)) {
                JSONObject messageDetail = paymentUpdateInVO.getMessageDetail();
                BaseDTO baseDTO = paymentService.complatePay(orderId,transactionFee,messageDetail);
                if(ErrorStatus.OK.equals(baseDTO.getStatus())) {
                    return "success";
                }
                logger.info("PaymentController updatePayOrder 更新订单状态失败,baseDTO:{}",JSON.toJSONString(baseDTO));
            }
            logger.info("PaymentController updatePayOrder 验证失败,paymentUpdateInVO:{}",JSON.toJSONString(paymentUpdateInVO));
        } catch (Exception e) {
            logger.error("PaymentController updatePayOrder error ,paymentUpdateInVO:{}", JSON.toJSONString(paymentUpdateInVO), e);
        }
        return "error";
    }

}
