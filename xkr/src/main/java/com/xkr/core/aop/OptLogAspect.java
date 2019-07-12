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

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/25
 */
@Aspect
public class OptLogAspect {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OptLogService optLogService;

    /**
     * 匹配所有带@MethodValidate
     */
    @Pointcut(value = "execution(@com.xkr.common.annotation.OptLog * *(..))")
    public void optLog() {

    }

    @Around("optLog()")
    public Object optLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        //getMethod 获取所有public方法
        Method targetMethod = methodSignature.getMethod();

        OptLog optLog = targetMethod.getAnnotation(OptLog.class);
        if (Objects.nonNull(optLog)) {
            OptLogModuleEnum moduleEnum = optLog.moduleEnum();
            OptEnum optEnum = optLog.optEnum();
            String detail = optEnum.getDesc() + ",参数:" + JSON.toJSONString(joinPoint.getArgs());
            if (!OptEnum.SYSTEM.equals(optEnum)) {
                Subject subject = SecurityUtils.getSubject();
                optLogService.saveOptLog(subject, moduleEnum, detail);
            }else {
                optLogService.saveOptLogByAuto(moduleEnum, detail);
            }
        }
        return joinPoint.proceed(joinPoint.getArgs());
    }

}
