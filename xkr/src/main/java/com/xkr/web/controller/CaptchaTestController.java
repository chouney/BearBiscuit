package com.xkr.web.controller;

import com.xkr.common.Const;
import com.xkr.service.api.CaptchaService;
import com.xkr.web.model.BasicResult;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
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

    /**
     * 文件上传测试
     *
     * @return
     */
    @RequestMapping(value = "/captcha", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public BasicResult index4(@RequestParam(name = "captcha") String captcha) {
        return new BasicResult(captchaService.checkCaptcha(captcha));
    }


}
