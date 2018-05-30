package com.xkr.web.handler.handler;

import com.xkr.common.Const;
import com.xkr.common.annotation.valid.Captcha;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/27
 */
public class CaptchaValidator implements ConstraintValidator<Captcha, String> {

    @Override
    public void initialize(Captcha captcha) {
    }

    @Override
    public boolean isValid(String text, ConstraintValidatorContext constraintValidatorContext) {
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
