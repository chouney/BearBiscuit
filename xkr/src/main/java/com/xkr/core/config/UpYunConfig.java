package com.xkr.core.config;

import main.java.com.UpYun;
import main.java.com.upyun.MediaHandler;
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

    @Value("${upyun.bucket.file}")
    private String fileBucket;

    @Value("${upyun.bucket.image}")
    private String imageBucket;

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

    @Bean(name = "fileYun")
    public UpYun getFileUpYun(){
        UpYun upYun =  new UpYun(fileBucket,optUser,optPassword);
        upYun.setApiDomain(UpYun.ED_AUTO);
        upYun.setDebug(debug);
        upYun.setTimeout(timeout);
        return upYun;
    }

    @Bean(name = "imageYun")
    public UpYun getImageUpYun(){
        UpYun upYun =  new UpYun(imageBucket,optUser,optPassword);
        upYun.setApiDomain(UpYun.ED_AUTO);
        upYun.setDebug(debug);
        upYun.setTimeout(timeout);
        return upYun;
    }

    @Bean(name = "mediaHandler")
    public MediaHandler getMediaHandler(){
        MediaHandler mediaHandler =  new MediaHandler(fileBucket,optUser,optPassword);
        return mediaHandler;
    }

}
