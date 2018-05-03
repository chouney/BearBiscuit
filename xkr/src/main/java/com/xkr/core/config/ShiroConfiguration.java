/*
 * Copyright (c) 2017 <l_iupeiyu@qq.com> All rights reserved.
 */

package com.xkr.core.config;

import com.xkr.common.Const;
import com.xkr.core.IdGenerator;
import com.xkr.core.redis.RedisCacheManager;
import com.xkr.core.redis.RedisSessionDAO;
import com.xkr.core.shiro.LoginModularRealmAuthenticator;
import com.xkr.core.shiro.user.UserShiroRealm;
import com.xkr.core.shiro.admin.*;
import com.xkr.core.shiro.user.UserCredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.*;

/**
 * Shiro 配置
 * <p>
 * Apache Shiro 核心通过 Filter 来实现，就好像SpringMvc 通过DispachServlet 来主控制一样。
 * 既然是使用 Filter 一般也就能猜到，是通过URL规则来进行过滤和权限校验，所以我们需要定义一系列关于URL的规则和访问权限。
 */
@Configuration
public class ShiroConfiguration {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 前台身份认证realm;
     *
     * @return
     */
    @Bean(name = "userShiroRealm")
    public UserShiroRealm userShiroRealm() {
        logger.debug("ShiroConfiguration.userShiroRealm()");
        UserShiroRealm userShiroRealm = new UserShiroRealm();
        userShiroRealm.setCacheManager(redisCacheManager());//redis权限缓存 默认缓存可注释此行
        userShiroRealm.setCredentialsMatcher(userCredentialsMatcher());
        return userShiroRealm;
    }

    /**
     * 后台身份认证realm;
     *
     * @return
     */
    @Bean(name = "adminShiroRealm")
    public AdminShiroRealm adminShiroRealm() {
        logger.debug("ShiroConfiguration.adminShiroRealm()");
        AdminShiroRealm adminShiroRealm = new AdminShiroRealm();
        adminShiroRealm.setCacheManager(redisCacheManager());//redis权限缓存 默认缓存可注释此行
        adminShiroRealm.setCredentialsMatcher(adminCredentialsMatcher());
        return adminShiroRealm;
    }

    /**
     * shiro缓存管理器;
     * 需要注入对应的其它的实体类中：
     * 1、安全管理器：securityManager
     * 可见securityManager是整个shiro的核心；
     *
     * @return
     */
    @Bean(name = "ehCacheManager")
    public EhCacheManager ehCacheManager() {
        logger.debug("ShiroConfiguration.ehCacheManager()");
        EhCacheManager cacheManager = new EhCacheManager();
        cacheManager.setCacheManagerConfigFile("classpath:ehcache-shiro.xml");
        return cacheManager;
    }

    @Bean(name = "redisCacheManager")
    public RedisCacheManager redisCacheManager() {
        logger.debug("ShiroConfiguration.redisCacheManager()");
        return new RedisCacheManager();
    }

    @Bean(name = "redisSessionDAO")
    public RedisSessionDAO redisSessionDAO() {
        logger.debug("ShiroConfiguration.redisSessionDAO()");
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setSessionIdGenerator(idGenerator());
        return redisSessionDAO;
    }

    @Bean(name = "idGenerator")
    public IdGenerator idGenerator() {
        logger.debug("ShiroConfiguration.idGenerator()");
        return new IdGenerator();
    }

    @Bean(name = "adminRolePermissionResolver")
    public AdminRolePermissionResolver adminRolePermissionResolver() {
        logger.debug("ShiroConfiguration.adminRolePermissionResolver()");
        return new AdminRolePermissionResolver();
    }

    @Bean(name = "customSessionListener")
    public CustomSessionListener customSessionListener() {
        logger.debug("ShiroConfiguration.customSessionListener()");
        return new CustomSessionListener();
    }

    /**
     * @return
     * @see DefaultWebSessionManager
     */
    @Bean(name = "sessionManager")
    public DefaultWebSessionManager defaultWebSessionManager() {
        logger.debug("ShiroConfiguration.defaultWebSessionManager()");
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        //用户信息必须是序列化格式，要不创建用户信息创建不过去，此坑很大，
        sessionManager.setSessionDAO(redisSessionDAO());//如不想使用REDIS可注释此行
        Collection<SessionListener> sessionListeners = new ArrayList<>();
        sessionListeners.add(customSessionListener());
        sessionManager.setSessionListeners(sessionListeners);
        //3600000 milliseconds = 1 hour
        sessionManager.setGlobalSessionTimeout(3600000);
        //是否删除无效的，默认也是开启
        sessionManager.setDeleteInvalidSessions(true);
        //是否开启 检测，默认开启
        sessionManager.setSessionValidationSchedulerEnabled(false);
        //创建会话Cookie
        Cookie cookie = new SimpleCookie(Const.SESSION_COOKIE_NAME);
        cookie.setName("WEBID");
        cookie.setDomain("xkr.com");
        //1 hour
        cookie.setMaxAge(3600);
        cookie.setHttpOnly(true);
        sessionManager.setSessionIdCookie(cookie);
        return sessionManager;
    }

    /**
     * @return
     * @see org.apache.shiro.mgt.SecurityManager
     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager() {
        logger.debug("ShiroConfiguration.getDefaultWebSecurityManage()");
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        Map<String, Object> shiroAuthenticatorRealms = new HashMap<>();
        shiroAuthenticatorRealms.put("adminShiroRealm", adminShiroRealm());
        shiroAuthenticatorRealms.put("userShiroRealm", userShiroRealm());

        Collection<Realm> shiroAuthorizerRealms = new ArrayList<Realm>();
        shiroAuthorizerRealms.add(adminShiroRealm());
        shiroAuthorizerRealms.add(userShiroRealm());

        ModularRealmAuthorizer authorizer = new ModularRealmAuthorizer();
        authorizer.setRealms(shiroAuthorizerRealms);
        authorizer.setRolePermissionResolver(adminRolePermissionResolver());
        authorizer.setPermissionResolver(new AdminPermissionResolver());

        LoginModularRealmAuthenticator loginModularRealmAuthenticator = new LoginModularRealmAuthenticator();
        loginModularRealmAuthenticator.setDefinedRealms(shiroAuthenticatorRealms);
        loginModularRealmAuthenticator.setAuthenticationStrategy(authenticationStrategy());
        securityManager.setAuthenticator(loginModularRealmAuthenticator);
        securityManager.setAuthorizer(authorizer);
        securityManager.setRealms(shiroAuthorizerRealms);
        //注入缓存管理器;
        securityManager.setCacheManager(redisCacheManager());
        securityManager.setSessionManager(defaultWebSessionManager());
        return securityManager;
    }

    /**
     * 开启shiro aop注解支持.
     * 使用代理方式;所以需要开启代码支持;
     *
     * @param securityManager
     * @return
     */
    @Bean(name = "authorizationAttributeSourceAdvisor")
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        logger.debug("ShiroConfiguration.authorizationAttributeSourceAdvisor()");
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }




    /**
     * Shiro默认提供了三种 AuthenticationStrategy 实现：
     * AtLeastOneSuccessfulStrategy ：其中一个通过则成功。
     * FirstSuccessfulStrategy ：其中一个通过则成功，但只返回第一个通过的Realm提供的验证信息。
     * AllSuccessfulStrategy ：凡是配置到应用中的Realm都必须全部通过。
     * authenticationStrategy
     *
     * @return
     */
    @Bean(name = "authenticationStrategy")
    public AuthenticationStrategy authenticationStrategy() {
        logger.debug("ShiroConfiguration.authenticationStrategy()");
        return new FirstSuccessfulStrategy();
    }

    /**
     * 凭证匹配器
     * （由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了
     * 所以我们需要修改下doGetAuthenticationInfo中的代码;
     * ）
     *
     * @return
     */
    @Bean(name = "adminCredentialsMatcher")
    public AdminCredentialsMatcher adminCredentialsMatcher() {
        logger.debug("ShiroConfiguration.adminCredentialsMatcher()");
        return new AdminCredentialsMatcher(redisCacheManager());
    }

    @Bean(name = "userCredentialsMatcher")
    public UserCredentialsMatcher userCredentialsMatcher() {
        logger.debug("ShiroConfiguration.userCredentialsMatcher()");
        return new UserCredentialsMatcher(redisCacheManager());
    }

    /**
     * 注入LifecycleBeanPostProcessor
     *
     * @return
     */
    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        logger.debug("ShiroConfiguration.lifecycleBeanPostProcessor()");
        return new LifecycleBeanPostProcessor();
    }

    @ConditionalOnMissingBean
    @Bean(name = "defaultAdvisorAutoProxyCreator")
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        logger.debug("ShiroConfiguration.getDefaultAdvisorAutoProxyCreator()");
        DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
        daap.setProxyTargetClass(true);
        return daap;
    }

    @Bean(name = "SecureRandomNumberGenerator")
    public SecureRandomNumberGenerator getSecureRandomNumberGenerator(){
        return new SecureRandomNumberGenerator();
    }
}
