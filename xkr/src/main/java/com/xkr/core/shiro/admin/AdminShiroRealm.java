/*
 * Copyright (c) 2017 <l_iupeiyu@qq.com> All rights reserved.
 */

package com.xkr.core.shiro.admin;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableSet;
import com.xkr.domain.XkrAdminAccountAgent;
import com.xkr.domain.entity.XkrAdminAccount;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * 后台身份校验核心类
 * author xkr
 * date 2017/3/13 0013 下午 15:38
 */
public class AdminShiroRealm extends AuthorizingRealm {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XkrAdminAccountAgent adminAccountAgent;


    @Override
    public String getName() {
        return "AdminShiroRealm";
    }

    /**
     * 认证信息.(身份验证)
     * :
     * Authentication 是用来验证用户身份
     *
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        logger.info("后台登录：AdminShiroRealm.doGetAuthenticationInfo()");
        //获取用户的输入的账号.
        String username = (String) token.getPrincipal();

        //通过username从数据库中查找 User对象，如果找到，没找到.
        //实际项目中，这里可以根据实际情况做缓存，如果不做，Shiro自己也是有时间间隔机制，2分钟内不会重复执行该方法
        XkrAdminAccount userInfo = adminAccountAgent.getAdminAccountByName(username);
        if (userInfo == null) {
            throw new UnknownAccountException();
        }

        //加密方式;
        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，如果觉得人家的不好可以自定义实现
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                userInfo, //用户名
                userInfo.getAccountToken(), //密码
                getName()  //realm name
        );

        return authenticationInfo;
    }


    /**
     * 此方法调用  hasRole,hasPermission的时候才会进行回调.
     * <p>
     * 权限信息.(授权):
     * 1、如果用户正常退出，缓存自动清空；
     * 2、如果用户非正常退出，缓存自动清空；
     * 3、如果我们修改了用户的权限，而用户不退出系统，修改的权限无法立即生效。
     * （需要手动编程进行实现；放在service进行调用）
     * 在权限修改后调用realm中的方法，realm已经由spring管理，所以从spring中获取realm实例，
     * 调用clearCached方法；
     * :Authorization 是授权访问控制，用于对用户进行的操作授权，证明该用户是否允许进行当前操作，如访问某个链接，某个资源文件等。
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) throws AuthenticationException {
       /*
        * 当没有使用缓存的时候，不断刷新页面的话，这个代码会不断执行，
        * 当其实没有必要每次都重新设置权限信息，所以我们需要放到缓存中进行管理；
        * 当放到缓存中时，这样的话，doGetAuthorizationInfo就只会执行一次了，
        * 缓存过期之后会再次执行。
        */
        logger.info("后台权限校验-->AdminShiroRealm.doGetAuthorizationInfo()");

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        XkrAdminAccount userInfo = (XkrAdminAccount) principals.getPrimaryPrincipal();
        if(StringUtils.isEmpty(userInfo.getPermissionIds())){
            return authorizationInfo;
        }

        Set<String> permissionIds = ImmutableSet.copyOf(userInfo.getPermissionIds().split(";"));


        authorizationInfo.setStringPermissions(permissionIds);
        logger.info("后台权限校验-->AdminShiroRealm.doGetAuthorizationInfo(),管理员用户权限为:{}", JSON.toJSONString(permissionIds));

        return authorizationInfo;
    }

    /**
     * 清空当前用户权限信息
     */
    public void clearCachedAuthorizationInfo() {
        PrincipalCollection principalCollection = SecurityUtils.getSubject().getPrincipals();
        SimplePrincipalCollection principals = new SimplePrincipalCollection(
                principalCollection, getName());
        super.clearCachedAuthorizationInfo(principals);
    }

    /**
     * 指定principalCollection 清除
     */
    public void clearCachedAuthorizationInfo(PrincipalCollection principalCollection) {
        SimplePrincipalCollection principals = new SimplePrincipalCollection(
                principalCollection, getName());
        super.clearCachedAuthorizationInfo(principals);
    }

}
