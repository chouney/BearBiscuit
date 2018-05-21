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

    private final static int STATUS_NORMAL = 1;

    private final static int STATUS_DELETE = 2;

    public XkrAboutRemark saveNewRemark(Long parentRemarkId,
                                        LoginEnum loginEnum, Long userId,
                                        String qq, String phone,
                                        String content){
        if(Objects.isNull(loginEnum) ||
                Objects.isNull(userId) ||
                StringUtils.isEmpty(qq) || StringUtils.isEmpty(phone) ||
                StringUtils.isEmpty(content)){
            return null;
        }
        Long id = idGenerator.generateId();
        parentRemarkId = parentRemarkId == null ? id : parentRemarkId;
        XkrAboutRemark xkrAboutRemark = new XkrAboutRemark();
        xkrAboutRemark.setContent(content);
        xkrAboutRemark.setParentRemarkId(parentRemarkId);
        xkrAboutRemark.setId(id);
        JSONObject ext = new JSONObject();
        ext.put("qq",qq);
        ext.put("phone",phone);
        xkrAboutRemark.setExt(ext.toJSONString());
        xkrAboutRemark.setUserId(userId);
        xkrAboutRemark.setUserTypeCode(Byte.valueOf(loginEnum.getType()));
        xkrAboutRemark.setStatus((byte)STATUS_NORMAL);
        return xkrAboutRemarkMapper.insert(xkrAboutRemark) == 1 ? xkrAboutRemark : null;
    }

}
