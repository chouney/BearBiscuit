package com.xkr.web.filter;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/4
 */
@Configuration
public class FilterConfig {

    private final static Logger LOGGER = LoggerFactory.getLogger(FilterConfig.class);

    /**
     * ShiroFilterFactoryBean 处理拦截资源文件问题。
     * 注意：单独一个ShiroFilterFactoryBean配置是或报错的，以为在
     * 初始化ShiroFilterFactoryBean的时候需要注入：SecurityManager
     * Filter Chain定义说明
     * 1、一个URL可以配置多个Filter，使用逗号分隔
     * 2、当设置多个过滤器时，全部验证通过，才视为通过
     * 3、部分过滤器可指定参数，如perms，roles
     */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shirFilter(DefaultWebSecurityManager securityManager) {
        LOGGER.debug("ShiroConfiguration.shirFilter()");
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 必须设置 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //增加自定义过滤
        Map<String, Filter> filters = new HashMap<>();
        filters.put("adminLogin",getAdminLoginShiroFilter());
        filters.put("userLogin",getUserLoginShiroFilter());
        shiroFilterFactoryBean.setFilters(filters);
        //拦截器.
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();

        //配置退出过滤器,其中的具体的退出代码Shiro已经替我们实现了
        /**
         * anon（匿名）  org.apache.shiro.web.filter.authc.AnonymousFilter
         * authc（身份验证）       org.apache.shiro.web.filter.authc.FormAuthenticationFilter
         * authcBasic（http基本验证）    org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter
         * logout（退出）        org.apache.shiro.web.filter.authc.LogoutFilter
         * noSessionCreation（不创建session） org.apache.shiro.web.filter.session.NoSessionCreationFilter
         * perms(许可验证)  org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter
         * port（端口验证）   org.apache.shiro.web.filter.authz.PortFilter
         * rest  (rest方面)  org.apache.shiro.web.filter.authz.HttpMethodPermissionFilter
         * roles（权限验证）  org.apache.shiro.web.filter.authz.RolesAuthorizationFilter
         * ssl （ssl方面）   org.apache.shiro.web.filter.authz.SslFilter
         * member （用户方面）  org.apache.shiro.web.filter.authc.UserFilter
         * user  表示用户不一定已通过认证,只要曾被Shiro记住过登录状态的用户就可以正常发起请求,比如rememberMe
         */

        //<!-- 过滤链定义，从上向下顺序执行，一般将 /**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
        //<!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
        //可以未登录访问的链接
        filterChainDefinitionMap.put("/**/login", "anon");
        // TODO: 2019/1/31 清除
        filterChainDefinitionMap.put("/test/**", "anon");

        filterChainDefinitionMap.put("/api/user/reg", "anon");
        filterChainDefinitionMap.put("/api/user/pre_update", "anon");
        filterChainDefinitionMap.put("/api/user/validate", "anon");
        filterChainDefinitionMap.put("/api/user/update", "anon");
        // 订单更新
        filterChainDefinitionMap.put("/api/payment/update", "anon");

        filterChainDefinitionMap.put("/api/res/cls/list", "anon");
        filterChainDefinitionMap.put("/api/res/detail", "anon");
        filterChainDefinitionMap.put("/api/res/res_list", "anon");
        filterChainDefinitionMap.put("/api/res/search_list", "anon");

        filterChainDefinitionMap.put("/api/cls/list", "anon");
        //数据分析
        filterChainDefinitionMap.put("/api/admin/data/list", "anon");

        filterChainDefinitionMap.put("/api/comment/list", "anon");

        //必须登录的url
        filterChainDefinitionMap.put("/api/admin/**", "adminLogin");
        filterChainDefinitionMap.put("/api/res/**", "userLogin");
        filterChainDefinitionMap.put("/api/common/**", "userLogin");
        filterChainDefinitionMap.put("/api/msg/**", "userLogin");
        filterChainDefinitionMap.put("/api/user/**", "userLogin");
        filterChainDefinitionMap.put("/api/cls/**", "userLogin");
        filterChainDefinitionMap.put("/api/comment/**", "userLogin");
        filterChainDefinitionMap.put("/api/remark/**", "userLogin");
        filterChainDefinitionMap.put("/api/payment/**", "userLogin");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

//    @Bean(name = "userLoginShiroFilter")
    public UserLoginShiroFilter getUserLoginShiroFilter(){
        return new UserLoginShiroFilter();
    }

//    @Bean(name = "adminLoginShiroFilter")
    public AdminLoginShiroFilter getAdminLoginShiroFilter(){
        return new AdminLoginShiroFilter();
    }

}
