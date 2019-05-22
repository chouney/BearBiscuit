/*
 * Copyright (c) 2017 <l_iupeiyu@qq.com> All rights reserved.
 */

package com.xkr.core.config;

import com.xkr.core.aop.CSRFAspect;
import com.xkr.core.aop.LogAspect;
import com.xkr.core.aop.OptLogAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class AopConfig {

    @Bean(name = "cSRFAspect")
    public CSRFAspect getCSRFAspect(){
        return new CSRFAspect();
    }

    @Bean(name = "optLogAspect")
    public OptLogAspect getOptLogAspect(){
        return new OptLogAspect();
    }

    @Bean(name = "logAspect")
    public LogAspect getLogAspect(){
        return new LogAspect();
    }
}
