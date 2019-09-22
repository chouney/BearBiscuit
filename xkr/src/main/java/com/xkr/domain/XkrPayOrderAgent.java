package com.xkr.domain;

import cn.beecloud.BCEumeration;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xkr.common.PaymentEnum;
import com.xkr.core.IdGenerator;
import com.xkr.dao.mapper.XkrAdminAccountMapper;
import com.xkr.dao.mapper.XkrPayOrderMapper;
import com.xkr.domain.dto.payment.PaymentStatusEnum;
import com.xkr.domain.entity.XkrAdminAccount;
import com.xkr.domain.entity.XkrPayOrder;
import com.xkr.util.EncodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/2
 */
@Service
public class XkrPayOrderAgent {

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private XkrPayOrderMapper xkrPayOrderMapper;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public boolean insertPayOrder(Long userId, int payTypeCode,String busOrderNo,String preOrderNo,
                                      String clientIp,Integer payAmount,String codeUrl,int expireTime){

        XkrPayOrder payOrder = new XkrPayOrder();
        long id = idGenerator.generateId();
        payOrder.setId(id);
        payOrder.setUserId(userId);
        payOrder.setPayTypeCode((byte) payTypeCode);
        payOrder.setTradeType((byte) 0); //暂存字段，留存
        payOrder.setPayOrderNo(busOrderNo);
        payOrder.setPrePayId(preOrderNo);
        payOrder.setClientIp(clientIp);
        payOrder.setPayAmount(Long.valueOf(payAmount));
        payOrder.setStatus((byte) PaymentStatusEnum.STATUS_WAIT_PAY.getCode());
        payOrder.setCodeUrl(codeUrl);
        LocalDateTime time = LocalDateTime.now().plusSeconds(expireTime);
        payOrder.setExpireTime(new Date(time.toEpochSecond(ZoneOffset.UTC)));
        if(xkrPayOrderMapper.insert(payOrder)==1) {
            return true;
        }
        return false;
    }

    public XkrPayOrder getOrderByOrderId(String orderId){
        Map<String,Object> params = Maps.newHashMap();
        params.put("orderId",orderId);
        params.put("statuses",PaymentStatusEnum.NON_DELETE_STATUSED);
        return xkrPayOrderMapper.getOrderByOrderId(params);
    }

    public boolean payOrderStatusByOrderId(String orderId,JSONObject ext){
        Map<String,Object> params = Maps.newHashMap();
        params.put("orderId",orderId);
        params.put("status",PaymentStatusEnum.STATUS_PAYED);
        if(Objects.nonNull(ext)) {
            params.put("ext", JSON.toJSONString(ext));
        }
        if(xkrPayOrderMapper.payOrderStatusByOrderId(params) == 1){
            return true;
        }
        return false;
    }
}
