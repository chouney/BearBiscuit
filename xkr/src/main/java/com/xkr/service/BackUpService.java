package com.xkr.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.xkr.common.ErrorStatus;
import com.xkr.domain.XkrDatabaseBackUpAgent;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.entity.XkrDatabaseBackup;
import com.xkr.service.api.DbBackUpApiService;
import com.xkr.service.api.UpLoadApiService;
import com.xkr.util.DateUtil;
import com.xkr.util.IdUtil;
import main.java.com.upyun.UpException;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/26
 */
@Service
@EnableScheduling
public class BackUpService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 定时表达式映射
     */
    private static final Map<Integer, String> PERIOD_CRONTAB_MAP = ImmutableMap.of(
            0, "0/1 * * * * ?", 1, "0 0 3 * * ?", 2, "0 0 3 ? * 1", 3, "0 0 3 1 * ?"
    );
    /**
     * 取消自动备份
     */
    private static final int PERIOD_SHUTDOWN_TYPE = -1;

    /**
     * 当前自动备份状态
     */
    private static AtomicInteger crontabType = new AtomicInteger(PERIOD_SHUTDOWN_TYPE);

    /**
     * 自动备份管理员Id
     */
    private static final long AUTO_BACKUP_ADMIN_ACCOUNT_ID = 0L;

    @Resource(name = "jobDetail")
    private JobDetail jobDetail;

    @Resource(name = "jobTrigger")
    private CronTrigger cronTrigger;

    @Resource(name = "scheduler")
    private Scheduler scheduler;

    @Resource
    private DbBackUpApiService backUpApiService;

    @Resource
    private UpLoadApiService upLoadApiService;

    @Resource
    private XkrDatabaseBackUpAgent xkrDatabaseBackUpAgent;

    /**
     * 备份服务，备份两份,本地备份以及云盘备份
     *
     * @param adminAccountId
     * @return
     */
    public ResponseDTO<Long> backup(Long adminAccountId) {
        adminAccountId = Objects.isNull(adminAccountId) ? AUTO_BACKUP_ADMIN_ACCOUNT_ID : adminAccountId;
        String backUpName = "dbbackup-" + DateUtil.getCurrentDate() + "-" + IdUtil.randomAlphanumeric(10);
        String localFilePath = backUpApiService.backup(backUpName, null);
        if (!StringUtils.isEmpty(localFilePath)) {
            try {
                String upyunFilePath = upLoadApiService.extFileUpload(backUpName, new File(localFilePath));
                if (!StringUtils.isEmpty(upyunFilePath)) {
                    return new ResponseDTO<>(xkrDatabaseBackUpAgent.saveNewBackUpDate(backUpName, localFilePath,
                            upyunFilePath, adminAccountId));
                }
            } catch (UpException | IOException e) {
                logger.error("backup service save backup failed adminAccountId:{},error:{}", adminAccountId, e);
            }
        }
        return new ResponseDTO<>(ErrorStatus.ERROR);
    }

    /**
     * 备份恢复
     * @param backUpId
     * @return
     */
    public ResponseDTO<Boolean> restore(Long backUpId) {
        if (Objects.isNull(backUpId)) {
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        try {

            XkrDatabaseBackup backup = xkrDatabaseBackUpAgent.getBackUpById(backUpId);
            if (Objects.nonNull(backup)) {
                JSONObject ext = JSON.parseObject(backup.getExt());
                String localFilePath = ext.getString(XkrDatabaseBackUpAgent.EXT_LOCAL_FILEPATH_KEY);
                String upyunFilePath = ext.getString(XkrDatabaseBackUpAgent.EXT_YUN_FILEPATH_KEY);
                File localFile = new File(localFilePath);
                if (localFile.exists() || (localFile.createNewFile() && upLoadApiService.downLoadFile(upyunFilePath,localFile))) {
                    return new ResponseDTO<>(backUpApiService.restore(localFilePath));
                }
            }
        } catch (IOException | UpException e) {
            logger.error("backup service restore backup failed backUpId:{},error:{}", backUpId, e);
        }
        return new ResponseDTO<>(ErrorStatus.ERROR);
    }

    /**
     * 获取自动定时信息
     *
     * @return
     */
    public ResponseDTO<Integer> getCrontabType() {
        return new ResponseDTO<>(crontabType.get());
    }

    /**
     * 设置自动定时
     *
     * @param periodType
     * @return
     */
    public ResponseDTO<Boolean> autoCrontab(int periodType) {
        if (!PERIOD_CRONTAB_MAP.keySet().contains(periodType) && PERIOD_SHUTDOWN_TYPE != periodType) {
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        try {
            if (!crontabType.compareAndSet(crontabType.get(), periodType)) {
                return new ResponseDTO<>(ErrorStatus.FREQUENCY_OPT);
            }
            if (PERIOD_SHUTDOWN_TYPE == periodType) {
                scheduler.shutdown(true);
                return new ResponseDTO<>(true);
            }
            String expression = PERIOD_CRONTAB_MAP.get(periodType);
            //执行响应表达式
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(expression);
            // 按新的cronExpression表达式重新构建trigger
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(cronTrigger.getKey());
            trigger = trigger.getTriggerBuilder().withIdentity(cronTrigger.getKey())
                    .withSchedule(scheduleBuilder).build();
            // 按新的trigger重新设置job执行
            scheduler.rescheduleJob(cronTrigger.getKey(), trigger);
            scheduler.startDelayed(5);
            return new ResponseDTO<>(true);
        } catch (SchedulerException e) {
            logger.error("backup service set schedule failed,periodType:{},error:", periodType, e);
        }
        return new ResponseDTO<>(ErrorStatus.ERROR);
    }
}
