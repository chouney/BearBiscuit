package com.xkr.common.annotation.valid;

import com.xkr.web.handler.handler.UserCheckValidator;
import com.xkr.web.handler.handler.UserValidValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hu.song
 * @version 1.0
 * @date 2020/2/4
 */
@Target({ElementType.PARAMETER,  ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = {UserValidValidator.class}
)
public @interface UserValValid {
    String message() default "账号只能是数字、字母下划线或者其组合";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int value() default 0;
}
