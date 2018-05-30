package com.xkr.common.annotation.valid;

import com.xkr.web.handler.handler.NumbericValidator;
import org.chris.redbud.validator.validate.ContainsIntValidator;

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
        validatedBy = {NumbericValidator.class}
)
public @interface IsNumberic {
    String message() default "参数不是数字";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int value() default 0;
}
