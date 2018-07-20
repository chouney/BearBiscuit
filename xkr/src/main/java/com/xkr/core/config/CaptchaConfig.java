package com.xkr.core.config;

import com.google.code.kaptcha.servlet.KaptchaServlet;
import com.xkr.core.CaptchaServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletException;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/21
 */
@Configuration
public class CaptchaConfig {

//    @Value("${kaptcha.border}")
//    private String kborder;
//
//    @Value("${kaptcha.textproducer.font.color}")
//    private String fcolor;
//
//    @Value("${kaptcha.textproducer.font.size}")
//    private String fsize;
//
//    @Value("${kaptcha.obscurificator.impl}")
//    private String obscurificator;
//
//    @Value("${kaptcha.noise.impl}")
//    private String noise;
//
//    @Value("${kaptcha.image.width}")
//    private String width;
//
//    @Value("${kaptcha.image.height}")
//    private String height;
//
//    @Value("${kaptcha.textproducer.char.length}")
//    private String clength;
//
//    @Value("${kaptcha.textproducer.char.space}")
//    private String cspace;
//
//    @Value("${kaptcha.background.clear.from}")
//    private String from;
//
//    @Value("${kaptcha.background.clear.to}")
//    private String to;

    @Bean
    public ServletRegistrationBean servletRegistrationBean() throws ServletException {
        ServletRegistrationBean servlet = new ServletRegistrationBean(new CaptchaServlet(), "/images/xkr_captcha.jpg");
//        servlet.addInitParameter("kaptcha.border", "no"/*kborder*/);//无边框
//        servlet.addInitParameter("kaptcha.textproducer.font.color", fcolor);
//        servlet.addInitParameter("kaptcha.textproducer.font.size", fsize);
//        servlet.addInitParameter("kaptcha.obscurificator.impl", obscurificator);
//        servlet.addInitParameter("kaptcha.noise.impl", noise);
//        servlet.addInitParameter("kaptcha.image.width", width);
//        servlet.addInitParameter("kaptcha.image.height", height);
//        servlet.addInitParameter("kaptcha.textproducer.char.length", clength);
//        servlet.addInitParameter("kaptcha.textproducer.char.space", cspace);
//        servlet.addInitParameter("kaptcha.background.clear.from", from); //和登录框背景颜色一致
//        servlet.addInitParameter("kaptcha.background.clear.to", to);
        return servlet;
    }
}
