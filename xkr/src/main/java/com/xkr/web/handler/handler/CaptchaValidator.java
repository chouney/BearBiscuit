package com.xkr.web.handler.handler;

import com.xkr.common.CaptchaEnum;
import com.xkr.common.Const;
import com.xkr.common.annotation.valid.Captcha;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Date;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/27
 */
public class CaptchaValidator implements ConstraintValidator<Captcha, String> {

    private CaptchaEnum checkType;

    @Override
    public void initialize(Captcha captcha) {
        this.checkType = captcha.value();
    }

    @Override
    public boolean isValid(String text, ConstraintValidatorContext constraintValidatorContext) {
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
