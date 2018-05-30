package com.xkr.dao.mapper;

import com.xkr.domain.entity.XkrDatabaseBackup;
import com.xkr.util.CustomerMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface XkrDatabaseBackupMapper extends CustomerMapper<XkrDatabaseBackup> {

    List<XkrDatabaseBackup> getListByIds(List<Long> list);

    List<XkrDatabaseBackup> selectList();

    Integer batchDeleteBackUpByIds(List<Long> list);

    XkrDatabaseBackup getBackUpById(Long id);
}