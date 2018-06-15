package com.xkr.core.aop;

import com.google.common.collect.ImmutableMap;
import com.xkr.common.Const;
import com.xkr.common.ErrorStatus;
import com.xkr.common.annotation.CSRFGen;
import com.xkr.common.annotation.CSRFValid;
import com.xkr.dao.cache.BaseRedisService;
import com.xkr.util.UuidUtil;
import com.xkr.web.model.BasicResult;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Objects;


/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/8
 */
@Aspect
public class CSRFAspect {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BaseRedisService baseRedisService;

    @Value("${spring.profiles.active}")
    private String runEnv;

    /**
     * 匹配所有带@MethodValidate
     */
    @Pointcut(value = "execution(@com.xkr.common.annotation.CSRFGen * *(..))")
    public void csrfGen() {

    }

    @Pointcut(value = "execution(@com.xkr.common.annotation.CSRFValid * *(..))")
    public void csrfValid() {

    }

    @Around("csrfGen()")
    public Object csrfGen(ProceedingJoinPoint joinPoint) throws Throwable {

        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        //getMethod 获取所有public方法
        Method targetMethod = methodSignature.getMethod();


        Object retValue = joinPoint.proceed(joinPoint.getArgs());
        CSRFGen csrfGen = targetMethod.getAnnotation(CSRFGen.class);
        if (Objects.nonNull(csrfGen) && retValue instanceof BasicResult) {
            BasicResult basicResult = (BasicResult) retValue;
            if(ErrorStatus.OK.getCode() == basicResult.getCode()) {
                genTokenAndSetReturn(basicResult);
            }
        }
        return retValue;
}

    @Around("csrfValid()")
    public Object csrfValid(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();

        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest httpServletRequest = sra.getRequest();

        if("dev".equals(runEnv) && StringUtils.isNotEmpty(httpServletRequest.getParameter("debug"))){
            return joinPoint.proceed(joinPoint.getArgs());
        }

        CSRFValid csrfValid = targetMethod.getAnnotation(CSRFValid.class);
        if (Objects.isNull(csrfValid)) {
            return joinPoint.proceed(joinPoint.getArgs());
        }
        try {
            String token = httpServletRequest.getParameter(Const.CSRF_TOKEN_PARAM);
            String csrfKey = Const.CSRF_TOKEN_PREFIX+token;
            String savedSessionId = baseRedisService.get(csrfKey);
            if(Objects.nonNull(savedSessionId) && savedSessionId.equals(String.valueOf(SecurityUtils.getSubject().getSession().getId()))){
                if(baseRedisService.compareAndSet(csrfKey,savedSessionId,"")){
                    Object retValue = joinPoint.proceed(joinPoint.getArgs());

                    //执行失败后重新注入token
                    if (retValue instanceof BasicResult) {
                        BasicResult basicResult = (BasicResult) retValue;
                        if(ErrorStatus.OK.getCode() != basicResult.getCode()) {
                            genTokenAndSetReturn(basicResult);
                        }
                    }
                    return retValue;
                }
                logger.info("请求频率太快");
            }
            logger.info("token认证异常");
        } catch (Exception e) {
            logger.error("token认证异常", e);
        }
        return new BasicResult(ErrorStatus.CSRF_TOEKN_ERROR);
    }

    private void genTokenAndSetReturn(BasicResult basicResult) throws IOException {
        Subject subject = SecurityUtils.getSubject();
        String uuid = UuidUtil.getUUID();
        String csrfKey = Const.CSRF_TOKEN_PREFIX+uuid;
        if(baseRedisService.compareAndSet(csrfKey,"",String.valueOf(subject.getSession().getId()))){
            basicResult.setExt(ImmutableMap.of(
                    Const.CSRF_TOKEN_PARAM,uuid
            ));
        }
    }
}

