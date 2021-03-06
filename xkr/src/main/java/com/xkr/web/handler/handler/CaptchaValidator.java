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
import java.util.Objects;

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
            //如果是资源上传验证码，且验证码为空，则跳过验证码校验（防止图片上传被验证码拦截）
            if(CaptchaEnum.UPLOAD_RES_TYPE.equals(checkType)){
                return true;
            }
            return false;
        }
        Session session = SecurityUtils.getSubject().getSession();
        String captcha = (String)session.getAttribute(Const.CAPTCHA_SESSION_KEY_BASE+checkType.getCode());
        Object date = session.getAttribute(Const.CAPTCHA_SESSION_DATE_BASE+checkType.getCode());
        if(Objects.isNull(date)){
           return false;
        }
        if(text.equals(captcha) && ((Date)date).after(new Date())){
            session.setAttribute(Const.CAPTCHA_SESSION_KEY_BASE+checkType.getCode(),null);
            session.setAttribute(Const.CAPTCHA_SESSION_DATE_BASE+checkType.getCode(),null);
            return true;
        }
        return false;
    }
}
