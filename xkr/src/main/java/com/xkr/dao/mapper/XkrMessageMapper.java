package com.xkr.dao.mapper;

import com.xkr.domain.entity.XkrMessage;
import com.xkr.util.CustomerMapper;
import org.springframework.stereotype.Service;

@Service
public interface XkrMessageMapper extends CustomerMapper<XkrMessage> {
}