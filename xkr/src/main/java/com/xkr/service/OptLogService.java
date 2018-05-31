package com.xkr.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xkr.common.ErrorStatus;
import com.xkr.common.OptEnum;
import com.xkr.common.OptLogModuleEnum;
import com.xkr.common.annotation.OptLog;
import com.xkr.domain.XkrAdminAccountAgent;
import com.xkr.domain.XkrAdminOptLogAgent;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.optlog.ListOptLogDTO;
import com.xkr.domain.dto.optlog.OptLogDTO;
import com.xkr.domain.entity.XkrAdminAccount;
import com.xkr.domain.entity.XkrAdminOptLog;
import org.apache.ibatis.jdbc.RuntimeSqlException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/24
 */
@Service
public class OptLogService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XkrAdminOptLogAgent xkrAdminOptLogAgent;

    @Autowired
    private XkrAdminAccountAgent xkrAdminAccountAgent;

    @Async
    public void saveOptLog(OptLogModuleEnum moduleEnum,String detail){
        Subject subject = SecurityUtils.getSubject();
        XkrAdminAccount adminAccount = (XkrAdminAccount) subject.getPrincipal();
        if(Objects.isNull(adminAccount)){
            logger.error("OptLogService saveOptLog can not get adminAccount,param,moduleEnum:{},detail:{}", JSON.toJSONString(moduleEnum),detail);
            return;
        }
        Session session = subject.getSession();
        xkrAdminOptLogAgent.saveNewOptLog(adminAccount,moduleEnum,session.getHost(),detail);
    }

    public ListOptLogDTO getOptLogList(Long adminAccountId,int pageNum,int size){
        ListOptLogDTO result = new ListOptLogDTO();
        pageNum = pageNum < 1 ? 1 : pageNum;
        size = size  < 1 ? 10 : size;
        Page page = PageHelper.startPage(pageNum,size,true);
        List<XkrAdminOptLog> list = xkrAdminOptLogAgent.getAllList(adminAccountId);
        result.setTotalCount((int) page.getTotal());

        List<Long> adminAccountIds = list.stream().map(XkrAdminOptLog::getAdminAccountId).collect(Collectors.toList());

        List<XkrAdminAccount> adminAccounts = xkrAdminAccountAgent.getListByIds(adminAccountIds);

        if(CollectionUtils.isEmpty(adminAccountIds)){
            result.setStatus(ErrorStatus.ERROR);
            return result;
        }


        buildListOptLogDTO(result,adminAccounts,list);

        return result;
    }

    /**
     * 批量更新日志
     * @param ids
     * @return
     */
    @OptLog(moduleEnum = OptLogModuleEnum.OPT_LOG,optEnum = OptEnum.DELETE)
    public ResponseDTO<Boolean> batchDelOptLogByIds(List<Long> ids){
        if(CollectionUtils.isEmpty(ids)){
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        return new ResponseDTO<>(xkrAdminOptLogAgent.batchUpdateOptLogByIds(ids));
    }

    private void buildListOptLogDTO(ListOptLogDTO listOptLogDTO,List<XkrAdminAccount> adminAccounts,List<XkrAdminOptLog> list){
        list.forEach(xkrAdminOptLog -> {
            XkrAdminAccount xkrAdminAccount = adminAccounts.stream().filter(xkrAdminAccount1 -> xkrAdminAccount1.getId().equals(xkrAdminOptLog.getAdminAccountId())).findAny().orElseThrow(RuntimeSqlException::new);
            OptLogDTO optLogDTO = new OptLogDTO();
            optLogDTO.setAccountName(xkrAdminAccount.getAccountName());
            optLogDTO.setAdminAccountId(xkrAdminAccount.getId());
            optLogDTO.setClientIp(xkrAdminOptLog.getClientIp());
            optLogDTO.setDate(xkrAdminOptLog.getUpdateTime());
            optLogDTO.setOptDetail(xkrAdminOptLog.getOptDetail());
            OptLogModuleEnum moduleEnum = OptLogModuleEnum.getByCode(xkrAdminOptLog.getOptModule());
            optLogDTO.setOptModule(moduleEnum == null ? "unknown" : moduleEnum.getDesc());
            optLogDTO.setOptlogId(xkrAdminOptLog.getId());
        });
    }
}
