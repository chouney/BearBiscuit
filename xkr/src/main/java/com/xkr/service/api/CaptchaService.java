package com.xkr.service.api;

import com.xkr.common.Const;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/21
 */
@Service
public class CaptchaService {

    public boolean checkCaptcha(String text){
        if(StringUtils.isEmpty(text)){
            return false;
        }
        Session session = SecurityUtils.getSubject().getSession();
        String captcha = (String)session.getAttribute(Const.CAPTCHA_SESSION_KEY);
        if(text.equals(captcha)){
            session.setAttribute(Const.CAPTCHA_SESSION_KEY,null);
            return true;
        }
        return false;
    }
}
