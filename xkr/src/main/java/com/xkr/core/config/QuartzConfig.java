package com.xkr.core.config;

import com.xkr.service.BackUpService;
import org.quartz.Trigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.concurrent.Executors;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/26
 */
@Configuration
public class QuartzConfig {

    @Bean(name = "jobDetail")
    public MethodInvokingJobDetailFactoryBean detailFactoryBean(BackUpService backUpService) {
        // ScheduleTask为需要执行的任务
        MethodInvokingJobDetailFactoryBean jobDetail = new MethodInvokingJobDetailFactoryBean();
        /*
         *  是否并发执行
         *  例如每5s执行一次任务，但是当前任务还没有执行完，就已经过了5s了，
         *  如果此处为true，则下一个任务会bing执行，如果此处为false，则下一个任务会等待上一个任务执行完后，再开始执行
         */
        jobDetail.setConcurrent(true);

        jobDetail.setName("backup");// 设置任务的名字
        jobDetail.setGroup("backup_service");// 设置任务的分组，这些属性都可以存储在数据库中，在多任务的时候使用

        /*
         * 这两行代码表示执行task对象中的scheduleTest方法。定时执行的逻辑都在scheduleTest。
         */
        jobDetail.setTargetObject(backUpService);
        jobDetail.setArguments(new Object[]{0L});

        jobDetail.setTargetMethod("backup");
        return jobDetail;
    }

    @Bean(name = "jobTrigger")
    public CronTriggerFactoryBean cronJobTrigger(MethodInvokingJobDetailFactoryBean jobDetail) {
        CronTriggerFactoryBean tigger = new CronTriggerFactoryBean();
        tigger.setJobDetail(jobDetail.getObject());
        tigger.setCronExpression("0/6 * * * * ?");// 表示每隔6秒钟执行一次
        //tigger.set
        tigger.setName("backUpTrigger");// trigger的name
        return tigger;

    }

    @Bean(name = "scheduler")
    public SchedulerFactoryBean schedulerFactory(Trigger cronJobTrigger) {
        SchedulerFactoryBean bean = new SchedulerFactoryBean();
        //设置是否任意一个已定义的Job会覆盖现在的Job。默认为false，即已定义的Job不会覆盖现有的Job。
        bean.setOverwriteExistingJobs(true);
        // 注册定时触发器
        bean.setTriggers(cronJobTrigger);
        bean.setAutoStartup(false);
        //单线程调度
        bean.setTaskExecutor(Executors.newSingleThreadExecutor());
        return bean;
    }

}
