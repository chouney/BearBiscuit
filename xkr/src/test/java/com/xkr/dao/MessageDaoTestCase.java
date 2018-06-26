package com.xkr.dao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.xkr.BaseDaoTest;
import com.xkr.common.LoginEnum;
import com.xkr.dao.mapper.XkrMessageMapper;
import com.xkr.domain.dto.message.MessageStatusEnum;
import com.xkr.domain.entity.XkrMessage;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MessageDaoTestCase extends BaseDaoTest{

    @Autowired
    private XkrMessageMapper xkrMessageMapper;


    @Test
    public void testAInsert(){
        long messageId = 3L;
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
    public void testABatchInsert(){
        long messageId = 4L;
        XkrMessage xkrMessage = new XkrMessage();
        xkrMessage.setId(messageId);
        xkrMessage.setFromTypeCode(Byte.valueOf(LoginEnum.CUSTOMER.getType()));
        xkrMessage.setFromId(412412412L);
        xkrMessage.setToTypeCode(Byte.valueOf(LoginEnum.ADMIN.getType()));
        xkrMessage.setToId(12312312312L);
        xkrMessage.setContent("阿斯顿好的后期我和哦IQ王洪迪请和我还欠我");
        xkrMessage.setStatus((byte) 1);
        List<XkrMessage> list = Lists.newArrayList();
        list.add(xkrMessage);
        XkrMessage xkrMessage1 = new XkrMessage();
        xkrMessage1.setId(5L);
        xkrMessage1.setFromTypeCode(Byte.valueOf(LoginEnum.CUSTOMER.getType()));
        xkrMessage1.setFromId(412412412L);
        xkrMessage1.setToTypeCode(Byte.valueOf(LoginEnum.ADMIN.getType()));
        xkrMessage1.setToId(12312312312L);
        xkrMessage1.setContent("阿斯顿好的后期我和哦IQ王洪迪请和我还欠我");
        xkrMessage1.setStatus((byte) 1);
        list.add(xkrMessage1);
        Assert.assertEquals(2,xkrMessageMapper.insertMessageList(list).intValue());
    }

    @Test
    public void testGetFromSource(){
        Map<String, Object> params = ImmutableMap.of(
                "fromTypeCode", Integer.valueOf(LoginEnum.CUSTOMER.getType()),
                "fromId", 4L,
                "statuses", ImmutableList.of(MessageStatusEnum.MESSAGE_STATUS_UNREAD.getCode())
        );
        List<XkrMessage> list = xkrMessageMapper.getMessagesByFromSource(params);
        Assert.assertEquals(1,list.size());
    }

    @Test
    public void testGetFromALLSource(){
        Map<String, Object> params = ImmutableMap.of(
                "fromTypeCode", Integer.valueOf(LoginEnum.CUSTOMER.getType()),
                "fromId", 2L
        );
        List<XkrMessage> list = xkrMessageMapper.getMessagesByFromSource(params);
        Assert.assertEquals(1,list.size());
    }

    @Test
    public void testGetUnRead(){
        Map<String, Object> params = ImmutableMap.of(
                "toTypeCode", Integer.valueOf(LoginEnum.CUSTOMER.getType()),
                "toId", 3L,
                "statuses", ImmutableList.of(MessageStatusEnum.MESSAGE_STATUS_UNREAD.getCode())
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

    @Test
    public void testUpdateMessageStatus(){
        List<Long> list = ImmutableList.of(
                1L,2L,3L
        );
        xkrMessageMapper.updateMessageStatus(ImmutableMap.of(
                "status",2,
                "messageIds",list
        ));
        List<XkrMessage> result = xkrMessageMapper.selectAll();
        result.forEach(xkrMessage -> {
            Assert.assertEquals(xkrMessage.getStatus(),Byte.valueOf("2"));
        });
    }


}
