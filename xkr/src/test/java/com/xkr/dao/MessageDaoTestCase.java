package com.xkr.dao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.xkr.BaseDaoTest;
import com.xkr.common.LoginEnum;
import com.xkr.dao.mapper.XkrMessageMapper;
import com.xkr.domain.XkrMessageAgent;
import com.xkr.domain.dto.message.MessageStatusEnum;
import com.xkr.domain.entity.XkrMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
public class MessageDaoTestCase extends BaseDaoTest{

    @Autowired
    private XkrMessageMapper xkrMessageMapper;


    @Before
    public void init(){
        XkrMessage xkrMessage = new XkrMessage();
        xkrMessage.setId(1L);
        xkrMessage.setFromTypeCode(Byte.valueOf(LoginEnum.CUSTOMER.getType()));
        xkrMessage.setFromId(2L);
        xkrMessage.setToTypeCode(Byte.valueOf(LoginEnum.CUSTOMER.getType()));
        xkrMessage.setToId(3L);
        xkrMessage.setContent("2给3发了一个消息");
        xkrMessage.setStatus((byte) 1);
        xkrMessageMapper.insert(xkrMessage);
        xkrMessage = new XkrMessage();
        xkrMessage.setId(2L);
        xkrMessage.setFromTypeCode(Byte.valueOf(LoginEnum.CUSTOMER.getType()));
        xkrMessage.setFromId(4L);
        xkrMessage.setToTypeCode(Byte.valueOf(LoginEnum.CUSTOMER.getType()));
        xkrMessage.setToId(3L);
        xkrMessage.setContent("4给3发了一个消息");
        xkrMessage.setStatus((byte) 1);
        xkrMessageMapper.insert(xkrMessage);
    }

    @Test
    public void testInsert(){
        long messageId = 4124124124124L;
        XkrMessage xkrMessage = new XkrMessage();
        xkrMessage.setId(messageId);
        xkrMessage.setFromTypeCode(Byte.valueOf(LoginEnum.CUSTOMER.getType()));
        xkrMessage.setFromId(412412412L);
        xkrMessage.setToTypeCode(Byte.valueOf(LoginEnum.ADMIN.getType()));
        xkrMessage.setToId(12312312312L);
        xkrMessage.setContent("阿斯顿好的后期我和哦IQ王洪迪请和我还欠我");
        xkrMessage.setStatus((byte) 1);
        Assert.assertEquals(1,xkrMessageMapper.insert(xkrMessage));
    }

    @Test
    public void testUpdateMessageStatus(){
        List<Long> list = ImmutableList.of(
                1L,2L
        );
        xkrMessageMapper.updateMessageStatus(ImmutableMap.of(
                "status",2,
                "messageIds",list
        ));
        List<XkrMessage> result = xkrMessageMapper.selectAll();
        result.forEach(xkrMessage -> {
            System.out.println(xkrMessage.getStatus());
            Assert.assertEquals(xkrMessage.getStatus(),Byte.valueOf("2"));
        });
    }

    @Test
    public void testGetUnRead(){
        Map<String, Object> params = ImmutableMap.of(
                "toTypeCode", Integer.valueOf(LoginEnum.CUSTOMER.getType()),
                "toId", 3L,
                "status", MessageStatusEnum.MESSAGE_STATUS_UNREAD.getCode()
        );
        List<XkrMessage> list = xkrMessageMapper.getMessagesByToSource(params);
        Assert.assertEquals(1,list.size());
    }

    @Test
    public void testGetALL(){
        Map<String, Object> params = ImmutableMap.of(
                "toTypeCode", Integer.valueOf(LoginEnum.CUSTOMER.getType()),
                "toId", 3L
        );
        List<XkrMessage> list = xkrMessageMapper.getMessagesByToSource(params);
        Assert.assertEquals(2,list.size());
    }


}
