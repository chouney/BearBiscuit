package com.xkr.domain;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.xkr.core.IdGenerator;
import com.xkr.dao.mapper.XkrResourceUserMapper;
import com.xkr.domain.entity.XkrResourceUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@Service
public class XkrResourceUserAgent {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XkrResourceUserMapper xkrResourceUserMapper;

    @Autowired
    private IdGenerator idGenerator;

    public static final int STATUS_PAYED = 1;

    public static final int STATUS_DELETED = 2;


    public List<XkrResourceUser> getResourceByUserId(Long userId, int status) {
        if (Objects.isNull(userId)) {
            logger.error("XkrResourceUserAgent getResourceByUserId empty userId");
            return Lists.newArrayList();
        }
        Map<String, Object> params = ImmutableMap.of(
                "status", status,
                "userId", userId
        );
        return xkrResourceUserMapper.getResourceByUserId(params);
    }

    public List<XkrResourceUser> getResourceByResAndUserId(Long userId, Long resourceId, int status) {
        if (Objects.isNull(userId) || Objects.isNull(resourceId)) {
            logger.error("XkrResourceUserAgent getResourceByResAndUserId empty userId or resourceId");
            return Lists.newArrayList();
        }
        Map<String, Object> params = ImmutableMap.of(
                "status", status,
                "userId", userId,
                "resourceId", resourceId
        );
        return xkrResourceUserMapper.getResourceByResAndUserId(params);
    }


    public boolean saveNewPayRecord(Long userId, Long resourceId) {
        if (Objects.isNull(userId) || Objects.isNull(resourceId)) {
            logger.error("XkrResourceUserAgent saveNewPayRecord empty userId:{} , resourceId:{}",userId,resourceId);
            return false;
        }
        Long id = idGenerator.generateId();
        XkrResourceUser resourceUser = new XkrResourceUser();

        resourceUser.setId(id);
        resourceUser.setStatus((byte)STATUS_PAYED);
        resourceUser.setResourceId(resourceId);
        resourceUser.setUserId(userId);
        return xkrResourceUserMapper.insertSelective(resourceUser) == 1;
    }


}
