package com.xkr.web.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.ImmutableMap;
import com.xkr.common.ErrorStatus;
import com.xkr.domain.dto.ListMessageDTO;
import com.xkr.domain.dto.MessageDTO;
import com.xkr.service.MessageService;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.ListMessageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@Controller
@RequestMapping("/api/msg")
public class MessageController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private MessageService messageService;

    @RequestMapping(value = "/list",method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult getUserMessage(@RequestParam(name = "userId") String userId,
                                      @RequestParam(name = "type") int type,
                                      @RequestParam(name = "pageNum", required = false, defaultValue = "1") int pageNum,
                                      @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        try {
            ListMessageDTO result = messageService.getToUserMessage(type, Long.valueOf(userId), pageNum, size);

            ListMessageVO vo = new ListMessageVO();

            BeanUtils.copyProperties(result,vo);

            return new BasicResult<>(vo);
        } catch (Exception e) {
            logger.error("MessageController getUserMessage error ,userId:{}", userId, e);
        }
        return new BasicResult(ErrorStatus.ERROR);
    }

    @RequestMapping(value = "/mark",method = {RequestMethod.POST})
    @ResponseBody
    public BasicResult markUserMessage(@RequestParam(name = "messageIds") String[] messageIds) {
        try {
            boolean result = messageService.markedMessageById(Arrays.stream(messageIds).map(Long::valueOf).collect(Collectors.toList()));
            return new BasicResult(result ? ErrorStatus.OK : ErrorStatus.ERROR);
        } catch (Exception e) {
            logger.error("MessageController getUserMessage error ,messageIds:{}", JSON.toJSONString(messageIds), e);
        }
        return new BasicResult(ErrorStatus.ERROR);
    }

}
