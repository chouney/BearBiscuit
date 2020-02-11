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
import org.springframework.util.StringUtils;

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
    public void saveOptLog(Subject subject,OptLogModuleEnum moduleEnum,String detail){
//        Subject subject = SecurityUtils.getSubject();
        XkrAdminAccount adminAccount = (XkrAdminAccount) subject.getPrincipal();
        if(Objects.isNull(adminAccount)){
            logger.error("OptLogService saveOptLog can not get adminAccount,param,moduleEnum:{},detail:{}", JSON.toJSONString(moduleEnum),detail);
            return;
        }
        Session session = subject.getSession();
        xkrAdminOptLogAgent.saveNewOptLog(adminAccount,moduleEnum,session.getHost(),detail);
    }

    @Async
    public void saveOptLogByAuto(OptLogModuleEnum moduleEnum,String detail){
//        Subject subject = SecurityUtils.getSubject();
        xkrAdminOptLogAgent.saveNewOptLog(999999L,moduleEnum,"127.0.0.1",detail);
    }

    public ListOptLogDTO getOptLogList(String accountName,int pageNum,int size){
        ListOptLogDTO result = new ListOptLogDTO();
        pageNum = pageNum < 1 ? 1 : pageNum;
        size = size  < 1 ? 10 : size;
        Long adminAccountId = null;
        if(!StringUtils.isEmpty(accountName)) {
            XkrAdminAccount adminAccount = xkrAdminAccountAgent.getAdminAccountByName(accountName);
            if(Objects.nonNull(adminAccount)){
                adminAccountId = adminAccount.getId();
            }
        }
        Page page = PageHelper.startPage(pageNum,size,"update_time desc");
        List<XkrAdminOptLog> list = xkrAdminOptLogAgent.getAllList(adminAccountId);
        result.setTotalCount((int) page.getTotal());

        List<Long> adminAccountIds = list.stream().map(XkrAdminOptLog::getAdminAccountId).
                distinct().collect(Collectors.toList());

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
            XkrAdminAccount xkrAdminAccount = adminAccounts.stream().filter(xkrAdminAccount1 -> xkrAdminAccount1.getId().equals(xkrAdminOptLog.getAdminAccountId())).findAny().orElse(null);
            OptLogDTO optLogDTO = new OptLogDTO();
            if(Objects.nonNull(xkrAdminAccount)) {
                optLogDTO.setAccountName(xkrAdminAccount.getAccountName());
                optLogDTO.setAdminAccountId(xkrAdminAccount.getId());
            }else{
                optLogDTO.setAccountName("账号不存在或被删除");
            }
            optLogDTO.setClientIp(xkrAdminOptLog.getClientIp());
            optLogDTO.setDate(xkrAdminOptLog.getUpdateTime());
            optLogDTO.setOptDetail(xkrAdminOptLog.getOptDetail());
            OptLogModuleEnum moduleEnum = OptLogModuleEnum.getByCode(xkrAdminOptLog.getOptModule());
            optLogDTO.setOptModule(moduleEnum == null ? "unknown" : moduleEnum.getDesc());
            optLogDTO.setOptlogId(xkrAdminOptLog.getId());
            listOptLogDTO.getList().add(optLogDTO);
        });
    }
}
