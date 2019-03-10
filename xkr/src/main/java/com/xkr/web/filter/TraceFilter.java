/*
 * Copyright (c) 2017 <l_iupeiyu@qq.com> All rights reserved.
 */

package com.xkr.web.filter;

import com.alibaba.fastjson.JSON;
import com.xkr.util.IpUtil;
import com.xkr.util.UuidUtil;
import org.apache.catalina.connector.ResponseFacade;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

/**
 * author xkr
 * date 2017/3/23 0023 上午 11:23
 */
@Order(1)
@WebFilter(filterName = "traceFilter",urlPatterns = "/*")
public class TraceFilter extends AdviceFilter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 在访问controller前判断是否登录，返回json，不进行重定向。
     *
     * @param request
     * @param response
     * @return true-继续往下执行，false-该filter过滤器已经处理，不继续执行其他过滤器
     * @throws Exception
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String ip = IpUtil.getIpAddr(httpServletRequest);
        String traceId = UuidUtil.getUUID();
        MDC.put("traceId",traceId);
        logger.info("HttpRequst ip:{},uri:{},paramterMap:{}",ip,httpServletRequest.getRequestURI(),JSON.toJSONString(request.getParameterMap()));
        return true;
    }

}
