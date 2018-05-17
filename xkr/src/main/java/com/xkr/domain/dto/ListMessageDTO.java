package com.xkr.domain.dto;

import com.google.common.collect.Lists;
import com.xkr.domain.dto.MessageDTO;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class ListMessageDTO implements Serializable{

    private static final long serialVersionUID = 7780806491663427234L;

    private List<MessageDTO> msgList = Lists.newArrayList();

    private int totalCount;

    public List<MessageDTO> getMsgList() {
        return msgList;
    }

    public void setMsgList(List<MessageDTO> msgList) {
        this.msgList = msgList;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
