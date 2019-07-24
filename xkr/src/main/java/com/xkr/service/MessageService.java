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
import java.util.Map;

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

    public static final String RESOURCE_TEMPLATE = "您的资源%s,状态已变更为%s";

    public static final String REMARK_TEMPLATE = "您的留言已收到回复:%s";

    public static final String ORDER_PAY_TEMPLATE = "您已完成充值,充值金额:%s";

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

    @Async
    public void batchSaveMessageToUser(LoginEnum fromUserType, long fromUserId, Map<Long,String> userIdsContentMapperList) {
        if(CollectionUtils.isEmpty(userIdsContentMapperList)){
            return;
        }
        messageAgent.batchSaveToUserMessage(fromUserType, fromUserId, LoginEnum.CUSTOMER, userIdsContentMapperList);
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
        if(!CollectionUtils.isEmpty(list)) {
            list.forEach(xkrMessage -> {
                MessageDTO dto = new MessageDTO();
                dto.setId(xkrMessage.getId());
                dto.setDate(xkrMessage.getCreateTime());
                dto.setMsg(xkrMessage.getContent());
                if(MessageStatusEnum.MESSAGE_STATUS_READ.getCode() == xkrMessage.getStatus()){
                    dto.setHasRead(true);
                }else if(MessageStatusEnum.MESSAGE_STATUS_UNREAD.getCode() == xkrMessage.getStatus()){
                    dto.setHasRead(false);
                }
                result.getMsgList().add(dto);
            });
        }

        result.setTotalCount((int) page.getTotal());

        return result;
    }
}
