package com.xkr.dao.mapper;

import com.xkr.domain.entity.XkrLoginToken;
import com.xkr.util.CustomerMapper;
import org.springframework.stereotype.Service;

@Service
public interface XkrLoginTokenMapper extends CustomerMapper<XkrLoginToken> {
}