package com.xkr.core.aop;

import com.alibaba.fastjson.JSON;
import com.xkr.common.OptEnum;
import com.xkr.common.OptLogModuleEnum;
import com.xkr.common.annotation.OptLog;
import com.xkr.service.OptLogService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/25
 */
@Aspect
@Order(0)
public class LogAspect {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 匹配所有带@MethodValidate
     */
    @Pointcut(value = "execution(* com.xkr.web.controller..*(..))")
    public void log() {

    }

    @Around("log()")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("正在执行类:{},方法:{}",joinPoint.getSignature().getDeclaringTypeName(),joinPoint.getSignature().getName());
        Object obj = joinPoint.proceed(joinPoint.getArgs());
        logger.info("正在执行类:{},方法:{} 返回结果:{}",joinPoint.getSignature().getDeclaringTypeName(),joinPoint.getSignature().getName(),JSON.toJSONString(obj));
        return obj;
    }

}
