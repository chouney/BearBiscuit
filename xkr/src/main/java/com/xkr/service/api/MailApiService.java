package com.xkr.service.api;

import io.github.biezhi.ome.OhMyEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import java.util.Properties;

import static io.github.biezhi.ome.OhMyEmail.defaultConfig;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/15
 */
@Service
public class MailApiService {

    @Value("${front.domain}")
    private String frontDomain;

    @PostConstruct
    public void init(){
        Properties props = defaultConfig(false);
        props.put("mail.smtp.host", "smtp.ym.163.com");
        props.put("mail.smtp.port", "994");
        OhMyEmail.config(props,"administrator@sharecoder.cn","hellotest520");
    }

    public boolean sendRegValidCaptcha(String email,String userName,String captcha) throws MessagingException {
        OhMyEmail.subject("sharecoder邮箱验证")
                .from("sharecoder.cn")
                .to(email)
//                .html("您好，您的账号<b>"+userName+"</b>已经注册成功，请您<a href=\""+frontDomain+"/front_pages/checkEmail.html?token=" + captcha + "\">点此链接</a>进行激活操作，没有激活的账号无法正常使用，此链接转发无效。")
                .html("您好，您的账号<b>"+userName+"</b>已经注册成功，请您点击链接"+frontDomain+"/front_pages/checkEmail.html?token=" + captcha + "进行激活操作，没有激活的账号无法正常使用，此链接转发无效。")
                .send();
        return true;
    }

    public boolean sendPasswordUpdateValidCaptcha(String email,String userName,String captcha) throws MessagingException {
        OhMyEmail.subject("sharecoder邮箱验证")
                .from("sharecoder.cn")
                .to(email)
//                .html("您好，您的账号<b>"+userName+"</b>正在进行修改密码操作，请您<a href=\"" + frontDomain + "/front_pages/checkEmail.html?token=" + captcha + "\">点此链接</a>进行密码修改操作，此链接转发无效。")
                .html("您好，您的账号<b>"+userName+"</b>正在进行修改密码操作，请您点击链接" + frontDomain + "/front_pages/checkEmail.html?token=" + captcha + "进行密码修改操作，此链接转发无效。")
                .send();
        return true;
    }


}
