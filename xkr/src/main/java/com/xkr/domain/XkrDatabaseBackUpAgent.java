package com.xkr.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.xkr.core.IdGenerator;
import com.xkr.dao.mapper.XkrClassMapper;
import com.xkr.dao.mapper.XkrDatabaseBackupMapper;
import com.xkr.domain.entity.XkrClass;
import com.xkr.domain.entity.XkrDatabaseBackup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@Service
public class XkrDatabaseBackUpAgent {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XkrDatabaseBackupMapper xkrDatabaseBackupMapper;

    @Autowired
    private IdGenerator idGenerator;

    public final static String EXT_YUN_FILEPATH_KEY = "upyun_file_path";

    public final static String EXT_LOCAL_FILEPATH_KEY = "local_file_path";


    public XkrDatabaseBackup getBackUpById(Long id){
        if(Objects.isNull(id)){
            logger.info("XkrDatabaseBackUpAgent getBackUpById param id invalid null");
            return null;
        }
        return xkrDatabaseBackupMapper.getBackUpById(id);
    }

    public List<XkrDatabaseBackup> getList(){
        return xkrDatabaseBackupMapper.selectList();
    }

    public Long saveNewBackUpDate(String backUpName,String localFilePath,
                                  String upyunFilePath,Long adminAccountId){
        XkrDatabaseBackup backup = new XkrDatabaseBackup();
        Long id = idGenerator.generateId();
        backup.setAdminAccountId(adminAccountId);
        backup.setBackupName(backUpName);
        JSONObject ext = new JSONObject();
        ext.put(EXT_LOCAL_FILEPATH_KEY,localFilePath);
        ext.put(EXT_YUN_FILEPATH_KEY,upyunFilePath);
        backup.setStatus((byte)1);
        backup.setExt(JSON.toJSONString(ext));
        backup.setId(id);
        if(xkrDatabaseBackupMapper.insert(backup) == 1){
            return id;
        }
        return null;
    }

    public boolean batchDeleteBackUpByIds(List<Long> list){
        if(CollectionUtils.isEmpty(list)){
            logger.info("XkrDatabaseBackUpAgent batchDeleteBackUpByIds param list invalid null");
            return false;
        }
        return xkrDatabaseBackupMapper.batchDeleteBackUpByIds(list) == 1;
    }


}
