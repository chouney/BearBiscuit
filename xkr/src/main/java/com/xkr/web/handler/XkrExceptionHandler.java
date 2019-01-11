package com.xkr.web.handler;

import com.xkr.common.ErrorStatus;
import com.xkr.web.model.BasicResult;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 控制器层错误异常处理
 *
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/4
 */
@ControllerAdvice
public class XkrExceptionHandler {

    /**
     * 权限校验处理器
     * @param e
     * @return
     */
    @ExceptionHandler(value = {UnauthorizedException.class, UnauthenticatedException.class})
    @ResponseBody
    public BasicResult authorizationExceptionHandler(Exception e) {
        return new BasicResult(ErrorStatus.UNAUTHORIZED);
    }

    /**
     * 权限异常
     */
    @ExceptionHandler({AuthorizationException.class })
    @ResponseBody
    public BasicResult authorizationException(Exception e) {
        return new BasicResult(ErrorStatus.UNAUTHORIZED);
    }

}
