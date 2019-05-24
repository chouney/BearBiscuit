package com.xkr.web.handler.handler;

import com.xkr.common.annotation.valid.IsNumberic;
import com.xkr.common.annotation.valid.UserCheck;
import com.xkr.domain.dto.user.UserStatusEnum;
import com.xkr.domain.entity.XkrUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.shiro.SecurityUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/27
 */
public class UserCheckValidator implements ConstraintValidator<UserCheck, Integer> {

    @Override
    public void initialize(UserCheck isNumberic) {
    }

    @Override
    public boolean isValid(Integer o, ConstraintValidatorContext constraintValidatorContext) {
        XkrUser user = (XkrUser) SecurityUtils.getSubject().getPrincipal();

        return Objects.nonNull(user) && UserStatusEnum.USER_STATUS_NORMAL.getCode() == user.getStatus();
    }
}
