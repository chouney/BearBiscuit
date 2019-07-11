/*
 * Copyright (c) 2017 <l_iupeiyu@qq.com> All rights reserved.
 */

package com.xkr.web.filter;

import com.alibaba.fastjson.JSON;
import com.xkr.common.Const;
import com.xkr.common.ErrorStatus;
import com.xkr.common.LoginEnum;
import com.xkr.domain.XkrLoginTokenAgent;
import com.xkr.domain.entity.XkrAdminAccount;
import com.xkr.domain.entity.XkrLoginToken;
import com.xkr.domain.entity.XkrUser;
import com.xkr.web.model.BasicResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * author xkr
 * date 2017/3/23 0023 上午 11:23
 */
public class AdminLoginShiroFilter extends AdviceFilter {

    @Value("${spring.profiles.active}")
    private String runEnv;

//    @Autowired
//    private XkrLoginTokenAgent xkrLoginTokenAgent;

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
//        if(!"pro".equals(runEnv) && StringUtils.isNotEmpty(request.getParameter("debug"))){
//            return true;
//        }
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        Subject principal = SecurityUtils.getSubject();
        Session session = principal.getSession();
        String loginType = (String)session.getAttribute(Const.SESSION_LOGIN_TYPE_KEY);
        if (!LoginEnum.ADMIN.getType().equals(loginType) || !principal.isAuthenticated()){
            httpServletResponse.setCharacterEncoding("UTF-8");
//            httpServletResponse.setContentType("application/json");
            httpServletResponse.getWriter().write(JSON.toJSONString(new BasicResult(ErrorStatus.UNLOGIN_REDIRECT)));
            return false;
        }
        //检查是否有人登录该账号
//        if(Objects.nonNull(principal.getPrincipal())) {
//            XkrAdminAccount user = (XkrAdminAccount) principal.getPrincipal();
//            if(Objects.nonNull(user)) {
//                XkrLoginToken xkrLoginToken = xkrLoginTokenAgent.getUserLoginRecordById(user.getId());
//                if (Objects.nonNull(xkrLoginToken)) {
//                    String sessionId = xkrLoginToken.getLoginToken();
//                    if (!session.getId().equals(sessionId)) {
//                        httpServletResponse.setCharacterEncoding("UTF-8");
////            httpServletResponse.setContentType("application/json");
//                        httpServletResponse.getWriter().write(JSON.toJSONString(new BasicResult(ErrorStatus.USER_LOGIN_NEW)));
//                        return false;
//                    }
//                }
//            }
//        }
        return true;
    }

}
