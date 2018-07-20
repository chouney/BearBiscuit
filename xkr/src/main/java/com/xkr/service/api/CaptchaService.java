package com.xkr.service.api;

import com.xkr.common.CaptchaEnum;
import com.xkr.common.Const;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/21
 */
@Service
public class CaptchaService {

    public boolean checkCaptcha(String text, CaptchaEnum checkType){
        if(StringUtils.isEmpty(text)){
            return false;
        }
        Session session = SecurityUtils.getSubject().getSession();
        String captcha = (String)session.getAttribute(Const.CAPTCHA_SESSION_KEY_BASE+checkType.getCode());
        Date date = (Date)session.getAttribute(Const.CAPTCHA_SESSION_KEY_BASE+checkType.getCode());
        if(text.equals(captcha) && date.after(new Date())){
            session.setAttribute(Const.CAPTCHA_SESSION_KEY_BASE+checkType.getCode(),null);
            return true;
        }
        return false;
    }
}
