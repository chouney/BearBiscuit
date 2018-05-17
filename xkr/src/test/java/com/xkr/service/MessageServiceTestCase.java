package com.xkr.service;

import com.alibaba.fastjson.JSON;
import com.vividsolutions.jts.util.Assert;
import com.xkr.MockServiceTest;
import com.xkr.common.LoginEnum;
import com.xkr.domain.XkrMessageAgent;
import com.xkr.domain.entity.XkrMessage;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Date;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
public class MessageServiceTestCase extends MockServiceTest {

    @InjectMocks
    private MessageService messageService = new MessageService();

    @Mock
    private XkrMessageAgent messageAgent ;

    @Test
    public void testGetToUserMessage() {
        List<XkrMessage> messages = Lists.newArrayList();
        XkrMessage xkrMessage = new XkrMessage();
        xkrMessage.setId(1L);
        xkrMessage.setFromTypeCode(Byte.valueOf(LoginEnum.CUSTOMER.getType()));
        xkrMessage.setFromId(2L);
        xkrMessage.setToTypeCode(Byte.valueOf(LoginEnum.CUSTOMER.getType()));
        xkrMessage.setToId(3L);
        xkrMessage.setContent("2给3发了一个消息");
        xkrMessage.setStatus((byte) 1);
        xkrMessage.setUpdateTime(new Date());
        XkrMessage xkrMessage2 = new XkrMessage();
        xkrMessage2.setId(2L);
        xkrMessage2.setFromTypeCode(Byte.valueOf(LoginEnum.CUSTOMER.getType()));
        xkrMessage2.setFromId(4L);
        xkrMessage2.setToTypeCode(Byte.valueOf(LoginEnum.CUSTOMER.getType()));
        xkrMessage2.setToId(3L);
        xkrMessage2.setContent("4给3发了一个消息");
        xkrMessage2.setStatus((byte) 2);
        xkrMessage2.setUpdateTime(new Date());
        messages.add(xkrMessage);
        messages.add(xkrMessage2);
        List<XkrMessage> messages2 = Lists.newArrayList();
        messages2.add(xkrMessage);
        Mockito.when(messageAgent.getAllToUserMessage(Mockito.anyLong())).thenReturn(messages);
        Mockito.when(messageAgent.getUnReadToUserMessage(Mockito.anyLong())).thenReturn(messages2);

        System.out.println(JSON.toJSONString(messageService.getToUserMessage(MessageService.MESSAGE_FRONT_TYPE_ALL,1L,1,10)));
        Assert.equals(messageService.getToUserMessage(MessageService.MESSAGE_FRONT_TYPE_ALL,1L,1,10).getTotalCount(),2);
        Assert.equals(messageService.getToUserMessage(MessageService.MESSAGE_FRONT_TYPE_UNREAD,1L,1,10).getTotalCount(),1);
    }

}
