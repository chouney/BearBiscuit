package com.xkr.web.controller.user;

import com.xkr.common.Const;
import com.xkr.common.ErrorStatus;
import com.xkr.common.LoginEnum;
import com.xkr.core.shiro.LoginAuthenticationToken;
import com.xkr.domain.XkrUserAgent;
import com.xkr.util.IpUtil;
import com.xkr.web.controller.admin.AdminTestController;
import com.xkr.web.model.BasicResult;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/7
 */
@Controller
@RequestMapping("/user")
public class UserTestController {
    private Logger logger = LoggerFactory.getLogger(AdminTestController.class);

    @Autowired
    private XkrUserAgent xkrUserAgent;

    @Autowired
    private SecureRandomNumberGenerator secureRandomNumberGenerator;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ResponseBody
    public String loginPost(
            @RequestParam(name = "userAccount")
                    String userAccount,
            @RequestParam(name = "userToken")
                    String userToken,
            HttpServletRequest request) {
        LoginAuthenticationToken token = new LoginAuthenticationToken(userAccount, userToken, false);
        token.setLoginType(LoginEnum.CUSTOMER.toString());
        //获取当前的Subject
        Subject currentUser = SecurityUtils.getSubject();
        try {
            //在调用了login方法后,SecurityManager会收到AuthenticationToken,并将其发送给已配置的Realm执行必须的认证检查
            //每个Realm都能在必要时对提交的AuthenticationTokens作出反应
            //所以这一步在调用login(token)方法时,它会走到MyRealm.doGetAuthenticationInfo()方法中,具体验证方式详见此方法
            logger.info("对用户[" + userAccount + "]进行登录验证..验证开始");
            currentUser.login(token);
            logger.info("对用户[" + userAccount + "]进行登录验证..验证通过");
        } catch (UnknownAccountException uae) {
            logger.info("对用户[" + userAccount + "]进行登录验证..验证未通过,未知账户");
        } catch (IncorrectCredentialsException ice) {
            logger.info("对用户[" + userAccount + "]进行登录验证..验证未通过,错误的凭证");
        } catch (LockedAccountException lae) {
            logger.info("对用户[" + userAccount + "]进行登录验证..验证未通过,账户已锁定");
        } catch (ExcessiveAttemptsException eae) {
            logger.info("对用户[" + userAccount + "]进行登录验证..验证未通过,错误次数过多");
        } catch (AuthenticationException ae) {
            //通过处理Shiro的运行时AuthenticationException就可以控制用户登录失败或密码错误时的情景
            logger.info("对用户[" + userAccount + "]进行登录验证..验证未通过,堆栈轨迹如下");
        }
        //验证是否登录成功
        if (currentUser.isAuthenticated()) {
            Session session = SecurityUtils.getSubject().getSession();
            session.setAttribute(Const.SESSION_LOGIN_TYPE_KEY, LoginEnum.CUSTOMER.toString());

            logger.info("用户[" + userAccount + "]登录认证通过(这里可以进行一些认证通过后的一些系统参数初始化操作)");
            String ip = IpUtil.getIpAddr(request);
            return "loginSuccess";
        } else {
            token.clear();
            return "loginError";
        }
    }

    /**
     * @return
     */
    @RequestMapping(value = "/reg", method = RequestMethod.POST)
    @ResponseBody
    public BasicResult doReg(
            @RequestParam(name = "userName")
                    String userName,
            @RequestParam(name = "email")
                    String email,
            @RequestParam(name = "userToken")
                    String userToken) {
        try {
            String salt = secureRandomNumberGenerator.nextBytes().toHex();
            xkrUserAgent.createUserAccount(userName, userToken, salt, email);
            return new BasicResult(ErrorStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new BasicResult(ErrorStatus.ERROR);
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ResponseBody
    public String logout(RedirectAttributes redirectAttributes) {
        //使用权限管理工具进行用户的退出，跳出登录，给出提示信息
        System.out.println("PublicController.logout()");
        SecurityUtils.getSubject().logout();
        return "logoutOk";
    }

}
