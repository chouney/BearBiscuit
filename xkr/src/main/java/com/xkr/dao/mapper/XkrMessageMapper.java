package com.xkr.dao.mapper;

import com.xkr.domain.entity.XkrMessage;
import com.xkr.util.CustomerMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface XkrMessageMapper extends CustomerMapper<XkrMessage> {

    List<XkrMessage> getMessagesByFromSource(Map<String,Object> params);

    List<XkrMessage> getMessagesByToSource(Map<String,Object> params);

    Integer updateMessageStatus(Map<String,Object> params);

}