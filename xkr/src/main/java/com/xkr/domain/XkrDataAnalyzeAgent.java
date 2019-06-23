package com.xkr.domain;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xkr.common.DataAnalyzeEnum;
import com.xkr.core.IdGenerator;
import com.xkr.dao.mapper.XkrDataAnalyzeMapper;
import com.xkr.domain.dto.search.UserIndexDTO;
import com.xkr.domain.dto.user.UserStatusEnum;
import com.xkr.domain.entity.XkrDataAnalyze;
import com.xkr.domain.entity.XkrUser;
import com.xkr.service.api.SearchApiService;
import com.xkr.util.IdUtil;
import com.xkr.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/2
 */
@Service
public class XkrDataAnalyzeAgent {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XkrDataAnalyzeMapper xkrDataAnalyzeMapper;

    @Autowired
    private IdGenerator idGenerator;


    public void upsertData(String title){
        if(StringUtils.isEmpty(title)){
            return;
        }
        XkrDataAnalyze xkrDataAnalyze = xkrDataAnalyzeMapper.selectByTitle(title);
        if(xkrDataAnalyze == null){
            xkrDataAnalyze = new XkrDataAnalyze();
            xkrDataAnalyze.setTitle(title);
            xkrDataAnalyze.setId(idGenerator.generateId());
            xkrDataAnalyze.setStatus((byte) 1);
            xkrDataAnalyze.setCalCount(0);
            xkrDataAnalyze.setCalType(Byte.valueOf(DataAnalyzeEnum.SEARCH.getCode()));
            xkrDataAnalyzeMapper.insertSelective(xkrDataAnalyze);
            return;
        }
        xkrDataAnalyze.setCalCount(xkrDataAnalyze.getCalCount()+1);
        xkrDataAnalyzeMapper.updateByPrimaryKeySelective(xkrDataAnalyze);

    }

    public List<XkrDataAnalyze> selectByRange(Date start,Date end){

        Map<String,Object> params = Maps.newHashMap();
        params.put("startTime",start);
        params.put("endTime",end);
        return xkrDataAnalyzeMapper.selectByRange(params);

    }

}
