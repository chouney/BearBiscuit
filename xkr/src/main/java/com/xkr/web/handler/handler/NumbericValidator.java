package com.xkr.web.handler.handler;

import com.xkr.common.annotation.valid.IsNumberic;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/27
 */
public class NumbericValidator implements ConstraintValidator<IsNumberic, String> {

    @Override
    public void initialize(IsNumberic isNumberic) {
    }

    @Override
    public boolean isValid(String o, ConstraintValidatorContext constraintValidatorContext) {
        return StringUtils.isNotEmpty(o) && NumberUtils.isNumber(o);
    }
}
