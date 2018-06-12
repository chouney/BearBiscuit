package com.xkr.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.xkr.common.ErrorStatus;
import com.xkr.common.OptEnum;
import com.xkr.common.OptLogModuleEnum;
import com.xkr.common.annotation.OptLog;
import com.xkr.domain.XkrAdminAccountAgent;
import com.xkr.domain.XkrDatabaseBackUpAgent;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.backup.BackUpDTO;
import com.xkr.domain.dto.backup.ListBackUpDTO;
import com.xkr.domain.entity.XkrAdminAccount;
import com.xkr.domain.entity.XkrDatabaseBackup;
import com.xkr.exception.BackUpException;
import com.xkr.service.api.DbBackUpApiService;
import com.xkr.service.api.UpLoadApiService;
import com.xkr.util.DateUtil;
import com.xkr.util.IdUtil;
import main.java.com.upyun.UpException;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

    @Autowired
    private XkrAdminAccountAgent xkrAdminAccountAgent;

    /**
     * 备份服务，备份两份,本地备份以及云盘备份
     *
     * @param adminAccountId
     * @return
     */
    @OptLog(moduleEnum = OptLogModuleEnum.BACKUP,optEnum = OptEnum.INSERT)
    public ResponseDTO<Long> backup(Long adminAccountId){
        adminAccountId = Objects.isNull(adminAccountId) ? AUTO_BACKUP_ADMIN_ACCOUNT_ID : adminAccountId;
        String backUpName = "dbbackup-" + DateUtil.getCurrentDate() + "-" + IdUtil.randomAlphanumeric(10);
        String localFilePath = backUpApiService.getBackupPath() + backUpName;
        String upyunFilePath = String.format(UpLoadApiService.getExtFilePathFormat(), backUpName);
        Long id = xkrDatabaseBackUpAgent.saveNewBackUpDate(backUpName, localFilePath,
                upyunFilePath, adminAccountId);
        if (Objects.nonNull(id)) {
            try {
                localFilePath = backUpApiService.backup(localFilePath, null);
                if (StringUtils.isEmpty(localFilePath)) {
                    throw new BackUpException();
                }
                upyunFilePath = upLoadApiService.extFileUpload(upyunFilePath, new File(localFilePath));
                if (StringUtils.isEmpty(upyunFilePath)) {
                    throw new BackUpException();
                }
                return new ResponseDTO<>(id);
            } catch (BackUpException | UpException | IOException e) {
                logger.error("backup service save backup failed adminAccountId:{},error:{}", adminAccountId, e);
            }
            if (!xkrDatabaseBackUpAgent.batchDeleteBackUpByIds(ImmutableList.of(id))){
                logger.error("冗余backup记录,backUpId:{}",id);
            }
        }
        return new ResponseDTO<>(ErrorStatus.ERROR);
    }

    /**
     * 批量删除
     *
     * @return
     */
    @OptLog(moduleEnum = OptLogModuleEnum.BACKUP, optEnum = OptEnum.DELETE)
    public ResponseDTO<Boolean> batchDelBackUp(List<Long> backUpIds) {
        if (CollectionUtils.isEmpty(backUpIds)) {
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        List<XkrDatabaseBackup> backupList = xkrDatabaseBackUpAgent.getListByIds(backUpIds);

        //删除云盘和本地文件
        backupList.forEach(xkrDatabaseBackup -> {
            JSONObject ext = JSON.parseObject(xkrDatabaseBackup.getExt());
            String localFilePath = ext.getString(XkrDatabaseBackUpAgent.EXT_LOCAL_FILEPATH_KEY);
            String yunFilePath = ext.getString(XkrDatabaseBackUpAgent.EXT_YUN_FILEPATH_KEY);
            boolean isSuccess;
            try {
                File localFile = new File(localFilePath);
                isSuccess = localFile.delete();
                upLoadApiService.deleteFile(yunFilePath, false);
            } catch (UpException | IOException e) {
                logger.error("云盘删除该备份文件失败 backUpId:{},localFilePath:{},yunFilePath:{},error:{}", xkrDatabaseBackup.getId(), localFilePath, yunFilePath, e);
                isSuccess = false;
            } catch (Exception e) {
                logger.error("删除该备份文件失败 backUpId:{},localFilePath:{},yunFilePath:{},error:{}", xkrDatabaseBackup.getId(), localFilePath, yunFilePath, e);
                isSuccess = false;
            }
            if (!isSuccess) {
                logger.error("本地删除该备份文件失败 backUpId:{},localFilePath:{},yunFilePath:{},error:{}", xkrDatabaseBackup.getId(), localFilePath, yunFilePath);
            }
        });

        if (!xkrDatabaseBackUpAgent.batchDeleteBackUpByIds(backUpIds)) {
            return new ResponseDTO<>(ErrorStatus.ERROR);
        }
        return new ResponseDTO<>(true);

    }

    /**
     * 获取备份列表
     *
     * @return
     */
    public ListBackUpDTO getBackUpList() {
        List<XkrDatabaseBackup> backupList = xkrDatabaseBackUpAgent.getList();

        List<Long> adminAccountIds = backupList.stream().map(XkrDatabaseBackup::getAdminAccountId).collect(Collectors.toList());

        List<XkrAdminAccount> adminAccounts = xkrAdminAccountAgent.getListByIds(adminAccountIds);

        ListBackUpDTO listBackUpDTO = new ListBackUpDTO();

        buildListBackUpDTO(listBackUpDTO, backupList, adminAccounts);

        return listBackUpDTO;
    }

    /**
     * 备份恢复
     *
     * @param backUpId
     * @return
     */
    @OptLog(moduleEnum = OptLogModuleEnum.BACKUP, optEnum = OptEnum.UPDATE)
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
                if (localFile.exists() || (localFile.createNewFile() && upLoadApiService.downLoadFile(upyunFilePath, localFile))) {
                    boolean isSuccess = backUpApiService.restore(localFilePath);
                    return new ResponseDTO<>(isSuccess ? ErrorStatus.OK : ErrorStatus.ERROR);
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
    @OptLog(moduleEnum = OptLogModuleEnum.BACKUP, optEnum = OptEnum.UPDATE)
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

    private void buildListBackUpDTO(ListBackUpDTO listBackUpDTO, List<XkrDatabaseBackup> xkrDatabaseBackupList, List<XkrAdminAccount> adminAccounts) {
        xkrDatabaseBackupList.forEach(xkrDatabaseBackup -> {
            XkrAdminAccount adminAccount = adminAccounts.stream().filter(adminAccount1 -> adminAccount1.getId().equals(xkrDatabaseBackup.getAdminAccountId())).findFirst().orElse(null);
            BackUpDTO backUpDTO = new BackUpDTO();
            if (Objects.isNull(adminAccount)) {
                backUpDTO.setAccountName("管理员已不存在");
            } else {
                backUpDTO.setAccountName(adminAccount.getAccountName());
            }
            backUpDTO.setBackUpId(xkrDatabaseBackup.getId());
            backUpDTO.setDate(xkrDatabaseBackup.getCreateTime());
            backUpDTO.setBackUpName(xkrDatabaseBackup.getBackupName());
            listBackUpDTO.getList().add(backUpDTO);
        });
        listBackUpDTO.setTotalCount(listBackUpDTO.getList().size());
    }
}
