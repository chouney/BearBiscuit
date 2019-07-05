package com.xkr.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.xkr.common.ErrorStatus;
import com.xkr.common.LoginEnum;
import com.xkr.core.IdGenerator;
import com.xkr.dao.mapper.XkrAboutRemarkMapper;
import com.xkr.dao.mapper.XkrResourceCommentMapper;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.remark.RemarkStatusEnum;
import com.xkr.domain.dto.search.CommentIndexDTO;
import com.xkr.domain.entity.XkrAboutRemark;
import com.xkr.domain.entity.XkrResource;
import com.xkr.domain.entity.XkrResourceComment;
import com.xkr.domain.entity.XkrUser;
import com.xkr.service.api.SearchApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@Service
public class XkrRemarkAgent {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private XkrAboutRemarkMapper xkrAboutRemarkMapper;

    public final static long DEFAULT_PARENT_REMARK_ID = 0L;


    public XkrAboutRemark saveNewRemark(Long parentRemarkId,
                                        LoginEnum loginEnum, Long userId,
                                        String qq, String phone,
                                        String content){
        if(Objects.isNull(loginEnum) ||
                Objects.isNull(userId) ||
                StringUtils.isEmpty(content)){
            return null;
        }
        Long id = idGenerator.generateId();
        XkrAboutRemark xkrAboutRemark = new XkrAboutRemark();
        xkrAboutRemark.setStatus((byte) RemarkStatusEnum.STATUS_NORMAL_USER_REMARK.getCode());
        xkrAboutRemark.setParentRemarkId(DEFAULT_PARENT_REMARK_ID);
        if(Objects.nonNull(parentRemarkId)){
            xkrAboutRemark.setParentRemarkId(parentRemarkId);
        }
        JSONObject ext = new JSONObject();
        if(!StringUtils.isEmpty(qq)) {
            ext.put("qq", qq);
        }
        if(!StringUtils.isEmpty(phone)){
            ext.put("phone", phone);
        }
        if(!ext.isEmpty()) {
            xkrAboutRemark.setExt(ext.toJSONString());
        }

        xkrAboutRemark.setContent(content);
        xkrAboutRemark.setId(id);
        xkrAboutRemark.setUserId(userId);
        xkrAboutRemark.setUserTypeCode(Byte.valueOf(loginEnum.getType()));

        return xkrAboutRemarkMapper.insertSelective(xkrAboutRemark) == 1 ? xkrAboutRemark : null;
    }

    public XkrAboutRemark getRemarkById(Long id){
        if(Objects.isNull(id)){
            return null;
        }
        return xkrAboutRemarkMapper.getRemarkById(id);
    }

    public List<XkrAboutRemark> getAllRemarkList(){
        return xkrAboutRemarkMapper.getAllList();
    }

    public boolean batchUpdateRemarkStatus(List<Long> ids,Integer status){
        if(CollectionUtils.isEmpty(ids) || Objects.isNull(status)){
            return false;
        }
        return xkrAboutRemarkMapper.batchUpdateRemarkByIds(ImmutableMap.of(
                "status",status,"list",ids
        )) > 0;
    }

}
