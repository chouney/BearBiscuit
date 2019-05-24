package com.xkr.common.annotation.valid;

import com.xkr.web.handler.handler.NumbericValidator;
import com.xkr.web.handler.handler.UserCheckValidator;

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
@Target({ElementType.METHOD,  ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = {UserCheckValidator.class}
)
public @interface UserCheck {
    String message() default "该用户未激活或已被冻结";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int value() default 0;
}
