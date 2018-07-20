package com.xkr.common.annotation.valid;

import com.xkr.common.CaptchaEnum;
import com.xkr.web.handler.handler.CaptchaValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/27
 */
@Target({ElementType.PARAMETER,  ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = {CaptchaValidator.class}
)
public @interface Captcha {
    String message() default "验证码过期或者未通过";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    CaptchaEnum value();
}
