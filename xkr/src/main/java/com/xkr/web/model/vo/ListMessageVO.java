package com.xkr.web.model.vo;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class ListMessageVO implements Serializable{

    private static final long serialVersionUID = 173914030300233911L;

    private List<MessageVO> msgList = Lists.newArrayList();

    private int totalCount;

    public List<MessageVO> getMsgList() {
        return msgList;
    }

    public void setMsgList(List<MessageVO> msgList) {
        this.msgList = msgList;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
