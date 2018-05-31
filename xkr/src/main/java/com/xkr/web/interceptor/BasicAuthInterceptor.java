package com.xkr.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.xkr.common.ErrorStatus;
import com.xkr.common.annotation.NoBasicAuth;
import com.xkr.web.model.BasicResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.hash.Sha1Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/7
 */
public class BasicAuthInterceptor extends HandlerInterceptorAdapter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static Map<String, String> PROJECT_SCCURITY_MAP = ImmutableMap.of(
            "xkr", "a75db4ba27967da94d3ddc3c3675bb9e"
    );

    @Value("${spring.profiles.active}")
    private String runEnv;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        if("dev".equals(runEnv) && StringUtils.isNotEmpty(httpServletRequest.getParameter("debug"))){
            return true;
        }
        if (o instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) o;
            NoBasicAuth noBasicAuth = method.getMethodAnnotation(NoBasicAuth.class);
            if (Objects.nonNull(noBasicAuth)) {
                return true;
            }
            try {
                String date = httpServletRequest.getHeader("Date");
                String uri = httpServletRequest.getRequestURI();
                String signature = httpServletRequest.getHeader("Authorization");
                String[] s = signature.split(":");
                String secret = PROJECT_SCCURITY_MAP.get(s[0]);

                String needValidate = Base64.encodeToString(new Sha1Hash(secret, String.join("&", uri, date)).getBytes());
                if (signature.equals(needValidate)) {
                    return true;
                }
                logger.info("Basic Auth认证异常,uri:{}, date:{}", uri, date);
            } catch (Exception e) {
                logger.error("Basic Auth认证异常 ", e);
            }
        }
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json");
        httpServletResponse.getWriter().write(JSON.toJSONString(new BasicResult(ErrorStatus.BASIC_AUTH_ERROR)));
        return false;
    }

    // TODO: 2018/5/7 待验证
}