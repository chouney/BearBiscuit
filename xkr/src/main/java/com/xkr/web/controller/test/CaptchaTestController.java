package com.xkr.web.controller.test;

import com.xkr.common.CaptchaEnum;
import com.xkr.common.ErrorStatus;
import com.xkr.common.LoginEnum;
import com.xkr.domain.entity.XkrAdminAccount;
import com.xkr.domain.entity.XkrUser;
import com.xkr.exception.RegUserException;
import com.xkr.service.AdminService;
import com.xkr.service.UserService;
import com.xkr.service.api.CaptchaService;
import com.xkr.web.model.BasicResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/3
 */
@Controller
@RequestMapping("/test")
public class CaptchaTestController {
    private Logger logger = LoggerFactory.getLogger(CaptchaTestController.class);

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    /**
     * 文件上传测试
     *
     * @return
     */
    @RequestMapping(value = "/captcha", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public BasicResult index4(@RequestParam(name = "captcha") String captcha,
                              @RequestParam(name = "type") Integer type) {
        return new BasicResult(captchaService.checkCaptcha(captcha, CaptchaEnum.getByCode(type)));
    }

    /**
     * 创建用户测试
     *
     * @return
     */
    @RequestMapping(value = "/userCreate", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public BasicResult createUser(@RequestParam(name = "userName") String userName,
                                  @RequestParam(name = "email",required = false) String email,
                                  @RequestParam(name = "userToken") String userToken,
                                  @RequestParam(name = "userType") String type) throws RegUserException {
        if(LoginEnum.CUSTOMER.equals(type)){
            userService.createUserAccount(userName,email,userToken);
        }else if(LoginEnum.ADMIN.equals(type)){
            adminService.saveNewAdminAccount(userName,userToken,email,1);
        }
        return new BasicResult(ErrorStatus.OK);
    }


}
