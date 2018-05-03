package com.xkr.core.config;

import main.java.com.UpYun;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/8
 */
@Configuration
public class UpYunConfig {

    @Value("${upyun.cdn.host}")
    private String host;

    @Value("${upyun.bucket}")
    private String bucket;

    @Value("${upyun.opt.user}")
    private String optUser;

    @Value("${upyun.opt.password}")
    private String optPassword;

    @Value("${upyun.debug}")
    private boolean debug;

    @Value("${upyun.timeout}")
    private int timeout;

    @Value("${upyun.root.path}")
    private String rootPath;

    @Bean
    public UpYun getUpYun(){
        UpYun upYun =  new UpYun(bucket,optUser,optPassword);
        upYun.setApiDomain(UpYun.ED_AUTO);
        upYun.setDebug(debug);
        upYun.setTimeout(timeout);
        return upYun;
    }

}
