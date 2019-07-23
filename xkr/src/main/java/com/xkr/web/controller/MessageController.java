package com.xkr.web.controller;

import com.alibaba.fastjson.JSON;
import com.xkr.common.ErrorStatus;
import com.xkr.domain.dto.message.ListMessageDTO;
import com.xkr.service.MessageService;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.message.ListMessageVO;
import com.xkr.web.model.vo.message.MessageVO;
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

            if(!ErrorStatus.OK.equals(result.getStatus())){
                return new BasicResult(result.getStatus());
            }

            ListMessageVO vo = new ListMessageVO();

            buildListMessageVO(vo,result);

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

    private void buildListMessageVO(ListMessageVO listMessageVO,ListMessageDTO listMessageDTO){
        listMessageDTO.getMsgList().forEach(messageDTO -> {
            MessageVO messageVO = new MessageVO();
            messageVO.setId(messageDTO.getId());
            messageVO.setMsg(messageDTO.getMsg());
            messageVO.setDate(messageDTO.getDate());
            messageVO.setHasRead(messageDTO.isHasRead());
            listMessageVO.getMsgList().add(messageVO);
        });
        listMessageVO.setTotalCount(listMessageDTO.getTotalCount());
    }

}
