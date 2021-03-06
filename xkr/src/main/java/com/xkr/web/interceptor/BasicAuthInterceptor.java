package com.xkr.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.Hashing;
import com.xkr.common.ErrorStatus;
import com.xkr.common.annotation.NoBasicAuth;
import com.xkr.web.model.BasicResult;
import org.apache.commons.codec.digest.HmacUtils;
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
import java.nio.charset.Charset;
import java.util.Arrays;
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
            "xkr_sec", "45668d5e8dff0e2a62f9741d91bdff17"
    );

    @Value("${spring.profiles.active}")
    private String runEnv;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        if(!"pro".equals(runEnv) && StringUtils.isNotEmpty(httpServletRequest.getParameter("debug_sec"))){
            return true;
        }
        if (o instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) o;
            NoBasicAuth noBasicAuth = method.getMethodAnnotation(NoBasicAuth.class);
            if (Objects.nonNull(noBasicAuth)) {
                return true;
            }
            try {
                String date = httpServletRequest.getHeader("X-Date");
                String uri = httpServletRequest.getRequestURI();
                String signature = httpServletRequest.getHeader("Authorization");
                if(StringUtils.isEmpty(signature)){
                    logger.info("Basic Auth认证未通过");
                    httpServletResponse.setCharacterEncoding("UTF-8");
                    httpServletResponse.setContentType("application/json");
                    httpServletResponse.getWriter().write(JSON.toJSONString(new BasicResult(ErrorStatus.BASIC_AUTH_ERROR)));
                    return false;
                }
                String[] s = signature.split(":");
                String secret = PROJECT_SCCURITY_MAP.get(s[0]);

                String needValidate = Base64.encodeToString(
                        Hashing.hmacSha256(secret.getBytes(Charset.forName("utf-8")))
                                .hashBytes(String.join("&", uri, date).getBytes(Charset.forName("utf-8"))).toString().getBytes(Charset.forName("utf-8")));

                if (s.length>1
                        && s[1].equals(needValidate)) {
                    return true;
                }
                logger.info("Basic Auth认证未通过,uri:{}, date:{}", uri, date);
            } catch (Exception e) {
                logger.error("Basic Auth认证异常 ", e);
            }
        }
        logger.info("Basic Auth认证未通过");
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json");
        httpServletResponse.getWriter().write(JSON.toJSONString(new BasicResult(ErrorStatus.BASIC_AUTH_ERROR)));
        return false;
    }

    // TODO: 2018/5/7 待验证

//    public static void main(String[] args){
//        String secret = "a75db4ba27967da94d3ddc3c3675bb9e";
//        String req = String.join("&", "/api/cls/list", "Sat,08 Jun 2019 13:20:43 GMT");
//        System.out.println(req);
//        System.out.println(secret);
//        String needValidate = Base64.encodeToString(
//                Hashing.hmacSha256(secret.getBytes(Charset.forName("utf-8")))
//                        .hashBytes(req.getBytes(Charset.forName("utf-8"))).toString().getBytes(Charset.forName("utf-8")));
//        System.out.println(needValidate);
//    }
}