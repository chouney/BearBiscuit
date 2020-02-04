package com.xkr.web.handler.handler;

import com.xkr.common.annotation.valid.UserValValid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author hu.song
 * @version 1.0
 * @date 2020/2/4
 */
public class UserValidValidator implements ConstraintValidator<UserValValid, String> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void initialize(UserValValid userValValid) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        Pattern p = Pattern.compile("^[0-9a-zA-Z_]{6,12}$");
        Matcher m = p.matcher(s);
        boolean b = m.matches();
        logger.info("b->>>>>>>>>>>>>>>>>>>>>>>>>>>>" + b);
        return false;
    }
}
