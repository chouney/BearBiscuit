package com.xkr.domain;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.ImmutableMap;
import com.xkr.common.OptLogModuleEnum;
import com.xkr.core.IdGenerator;
import com.xkr.dao.mapper.XkrAdminOptLogMapper;
import com.xkr.dao.mapper.XkrResourceRecycleMapper;
import com.xkr.domain.entity.XkrAdminAccount;
import com.xkr.domain.entity.XkrAdminOptLog;
import com.xkr.domain.entity.XkrResource;
import com.xkr.domain.entity.XkrResourceRecycle;
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
public class XkrAdminRecycleAgent {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XkrResourceRecycleMapper xkrResourceRecycleMapper;

    @Autowired
    private IdGenerator idGenerator;

    public boolean saveNewResourceRecycle(Long resourceId, String resourceTitle,String className,
                                          String userName,String optName){
        if(Objects.isNull(resourceId) || StringUtils.isEmpty(optName) ||
                StringUtils.isEmpty(resourceTitle) || StringUtils.isEmpty(className)
                || StringUtils.isEmpty(userName)){
            return false;
        }
        XkrResourceRecycle resourceRecycle = new XkrResourceRecycle();
        resourceRecycle.setResourceTitle(resourceTitle);
        resourceRecycle.setResourceId(resourceId);
        resourceRecycle.setClassName(className);
        resourceRecycle.setUserName(userName);
        resourceRecycle.setOptName(optName);
        return xkrResourceRecycleMapper.insertSelective(resourceRecycle) == 1;
    }

    public List<XkrResourceRecycle> getAllListByPage(){
        return xkrResourceRecycleMapper.selectAll();
    }

    public boolean batchDeleteResourceRecycleByIds(List<Long> resourceIds){
        if(CollectionUtils.isEmpty(resourceIds)){
            return false;
        }
        logger.info("XkrAdminRecycleAgent batchDeleteResourceRecycleByIds params:{}", JSON.toJSONString(resourceIds));
        return xkrResourceRecycleMapper.batchDeleteResourceRecycleByIds(resourceIds) > 0;
    }

}
