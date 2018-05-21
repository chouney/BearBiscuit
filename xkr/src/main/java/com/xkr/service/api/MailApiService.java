package com.xkr.service.api;

import io.github.biezhi.ome.OhMyEmail;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/15
 */
@Service
public class MailApiService {

    @PostConstruct
    public void init(){
        OhMyEmail.config(OhMyEmail.SMTP_QQ(true),"64568559@qq.com","Zqx19921221");
    }

    public boolean sendCaptcha(String email,String captcha) throws MessagingException {
        OhMyEmail.subject("这是一封测试HTML邮件")
                .from("64568559@qq.com")
                .to(email)
                .html("<h1 font=red>信件内容:<a href=\"index#?token="+ captcha +"\">click here to verify</h1>")
                .send();
        return true;
    }

    public static void main(String[] args) throws MessagingException {
        OhMyEmail.config(OhMyEmail.SMTP_QQ(true),"64568559@qq.com","Zqx19921221");

        OhMyEmail.subject("这是一封测试HTML邮件")
                .from("64568559@qq.com")
                .to("chris_zqx@163.com")
                .html("<h1 font=red>信件内容</h1>")
                .send();

    }

}
