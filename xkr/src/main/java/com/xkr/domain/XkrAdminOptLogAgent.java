package com.xkr.domain;

import com.google.common.collect.ImmutableMap;
import com.xkr.common.OptLogModuleEnum;
import com.xkr.core.IdGenerator;
import com.xkr.dao.mapper.XkrAdminOptLogMapper;
import com.xkr.domain.entity.XkrAdminAccount;
import com.xkr.domain.entity.XkrAdminOptLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/2
 */
@Service
public class XkrAdminOptLogAgent {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XkrAdminOptLogMapper xkrAdminOptLogMapper;

    @Autowired
    private IdGenerator idGenerator;

    public final static int STATUS_NORMAL = 1;

    public final static int STATUS_DELETE = 2;

    public boolean saveNewOptLog(XkrAdminAccount xkrAdminAccount, OptLogModuleEnum logModuleEnum,
                                 String clientIp,String detail){
        if(Objects.isNull(xkrAdminAccount) || Objects.isNull(logModuleEnum) ||
                StringUtils.isEmpty(clientIp) || StringUtils.isEmpty(detail)){
            return false;
        }
        XkrAdminOptLog log = new XkrAdminOptLog();
        log.setId(idGenerator.generateId());
        log.setAdminAccountId(xkrAdminAccount.getId());
        log.setClientIp(clientIp);
        log.setOptDetail(detail);
        log.setOptModule((byte)logModuleEnum.getCode());
        log.setStatus((byte)STATUS_NORMAL);
        return xkrAdminOptLogMapper.insertSelective(log) == 1;
    }

    public boolean saveNewOptLog(Long systemId, OptLogModuleEnum logModuleEnum,
                                 String clientIp,String detail){
        if(Objects.isNull(systemId) || Objects.isNull(logModuleEnum) ||
                StringUtils.isEmpty(clientIp) || StringUtils.isEmpty(detail)){
            return false;
        }
        XkrAdminOptLog log = new XkrAdminOptLog();
        log.setId(idGenerator.generateId());
        log.setAdminAccountId(systemId);
        log.setClientIp(clientIp);
        log.setOptDetail(detail);
        log.setOptModule((byte)logModuleEnum.getCode());
        log.setStatus((byte)STATUS_NORMAL);
        return xkrAdminOptLogMapper.insertSelective(log) == 1;
    }

    public List<XkrAdminOptLog> getAllList(Long adminAccountId){
        return xkrAdminOptLogMapper.getAllOptLogByAdminAccount(adminAccountId);
    }

    public boolean batchUpdateOptLogByIds(List<Long> ids){
        if(CollectionUtils.isEmpty(ids)){
            return false;
        }
        return xkrAdminOptLogMapper.batchUpdateOptLogByIds(ImmutableMap.of(
                "list",ids,"status",STATUS_DELETE
        )) > 0;
    }

}
