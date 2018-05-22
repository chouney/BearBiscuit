package com.xkr.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xkr.common.ErrorStatus;
import com.xkr.common.LoginEnum;
import com.xkr.domain.XkrMessageAgent;
import com.xkr.domain.dto.message.ListMessageDTO;
import com.xkr.domain.dto.message.MessageDTO;
import com.xkr.domain.dto.message.MessageStatusEnum;
import com.xkr.domain.entity.XkrMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@Service
public class MessageService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XkrMessageAgent messageAgent;

    //未读消息
    public static final int MESSAGE_FRONT_TYPE_UNREAD = 1;

    //全量消息
    public static final int MESSAGE_FRONT_TYPE_ALL = 2;

    public boolean markedMessageById(List<Long> messageIds) {
        if (CollectionUtils.isEmpty(messageIds)) {
            logger.info("MessageService markedMessageById do not execute , messageId:{}", JSON.toJSONString(messageIds));
            return false;
        }
        return messageAgent.updateMessageStatus(MessageStatusEnum.MESSAGE_STATUS_READ.getCode(),messageIds);
    }

    @Async
    public void saveMessageToUser(LoginEnum fromUserType, long fromUserId, long userId, String content) {
        messageAgent.saveToUserMessage(fromUserType, fromUserId, LoginEnum.CUSTOMER, userId, content);
    }

    public ListMessageDTO getToUserMessage(int frontType, long userId,int pageNum,int size) {
        ListMessageDTO result = new ListMessageDTO();
        if (userId < 0L) {
            result.setStatus(ErrorStatus.PARAM_ERROR);
            return result;
        }
        List<XkrMessage> list;
        Page page = PageHelper.startPage(pageNum,size,true);

        if (MESSAGE_FRONT_TYPE_UNREAD == frontType) {
            list = messageAgent.getUnReadToUserMessage(userId);
        } else {
            list = messageAgent.getAllToUserMessage(userId);
        }
        list.forEach(xkrMessage -> {
            MessageDTO dto = new MessageDTO();
            dto.setDate(xkrMessage.getUpdateTime());
            dto.setMsg(xkrMessage.getContent());
            result.getMsgList().add(dto);
        });

        result.setTotalCount((int) page.getTotal());

        return result;
    }
}
