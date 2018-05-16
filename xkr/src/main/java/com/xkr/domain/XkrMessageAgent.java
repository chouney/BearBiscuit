package com.xkr.domain;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.xkr.common.LoginEnum;
import com.xkr.core.IdGenerator;
import com.xkr.dao.mapper.XkrMessageMapper;
import com.xkr.domain.entity.XkrMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@Service
public class XkrMessageAgent {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    public XkrMessageMapper xkrMessageMapper;

    public static final int MESSAGE_STATUS_UNREAD = 1;

    public static final int MESSAGE_STATUS_READ = 2;

    public static final int MESSAGE_STATUS_DELETE = 3;


    public boolean updateMessageStatus(Integer status , List<Long> messageIds){
        if(Objects.isNull(status) || CollectionUtils.isEmpty(messageIds)){
            logger.info("XkrMessageAgent updateMessageStatus param invalid , status:{},messageIds:{}",status,JSON.toJSONString(messageIds));
            return false;
        }
        Map<String,Object> params = ImmutableMap.of(
                "status",status,
                "messageIds",messageIds
        );
        return xkrMessageMapper.updateMessageStatus(params) == 1;
    }

    public Long saveToUserMessage(LoginEnum sendUserType, long sendUserId,
                                     LoginEnum toUserType, long toUserId, String content) {
        long messageId = idGenerator.generateId();
        XkrMessage xkrMessage = new XkrMessage();
        xkrMessage.setId(messageId);
        xkrMessage.setFromTypeCode(Byte.valueOf(sendUserType.getType()));
        xkrMessage.setFromId(sendUserId);
        xkrMessage.setToTypeCode(Byte.valueOf(toUserType.getType()));
        xkrMessage.setToId(toUserId);
        xkrMessage.setContent(content);
        xkrMessage.setStatus((byte) 1);
        logger.info("XkrMessageAgent saveToUserMessage, params:{}", JSON.toJSONString(xkrMessage));
        if(xkrMessageMapper.insert(xkrMessage) == 1){
            return messageId;
        }
        logger.info("XkrMessageAgent saveToUserMessage failed, params:{}", JSON.toJSONString(xkrMessage));
        return null;
    }

    public List<XkrMessage> getUnReadToUserMessage(long userId) {
        Map<String, Object> params = ImmutableMap.of(
                "toTypeCode", Integer.valueOf(LoginEnum.CUSTOMER.getType()),
                "toId", userId,
                "status", XkrMessageAgent.MESSAGE_STATUS_UNREAD
        );
        return xkrMessageMapper.getMessagesByToSource(params);
    }

    public List<XkrMessage> getAllToUserMessage(long userId) {
        Map<String, Object> params = ImmutableMap.of(
                "toTypeCode", Integer.valueOf(LoginEnum.CUSTOMER.getType()),
                "toId", userId
        );
        return xkrMessageMapper.getMessagesByToSource(params);
    }

}
