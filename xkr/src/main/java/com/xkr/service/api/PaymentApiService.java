package com.xkr.service.api;

import cn.beecloud.BCPay;
import cn.beecloud.BeeCloud;
import cn.beecloud.bean.BCException;
import cn.beecloud.bean.BCOrder;
import com.xkr.common.ErrorStatus;
import com.xkr.common.PaymentEnum;
import com.xkr.core.IdGenerator;
import com.xkr.domain.dto.payment.PaymentDTO;
import com.xkr.util.MoneyUtil;
import io.github.biezhi.ome.OhMyEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import java.util.Properties;

import static io.github.biezhi.ome.OhMyEmail.defaultConfig;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/15
 */
@Service
public class PaymentApiService implements InitializingBean{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("spring.profiles.active")
    private String profile;

    @Value("${beecloud.app_id}")
    private String appId;

    @Value("${beecloud.test_secret}")
    private String testSecret;

    @Value("${beecloud.master_secret}")
    private String masterSecret;

    @Value("${beecloud.app_secret}")
    private String appSecret;

    /**
     * 生成订单
     * @param amount
     * @param payTypeCode
     * @param returnUrl
     * @return
     */
    public BCOrder genBill(String orderId,String amount,int payTypeCode,String returnUrl){
        BCOrder bcOrder = new BCOrder(PaymentEnum.getChannelByCode(payTypeCode),(int) MoneyUtil.yuan2fen(amount),orderId,"需客儿充值订单");
        bcOrder.setBillTimeout(360);//设置订单超时时间
        bcOrder.setReturnUrl(returnUrl);//设置return url
        try {
            bcOrder = BCPay.startBCPay(bcOrder);
        } catch (BCException e) {
            logger.error("创建订单失败,amount:{},payTypeCode:{},returnUrl:{},error:{}",amount,payTypeCode,returnUrl, e);
            return null;
        }
        return bcOrder;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        BeeCloud.registerApp(appId,testSecret,appSecret,masterSecret);
        if(!"pro".equals(profile)){
            BeeCloud.setSandbox(true);
        }
    }

}
