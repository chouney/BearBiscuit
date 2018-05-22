package com.xkr.dao.mapper;

import com.xkr.domain.entity.XkrLoginToken;
import com.xkr.util.CustomerMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface XkrLoginTokenMapper extends CustomerMapper<XkrLoginToken> {
    List<XkrLoginToken> getLoginTokensByIds(List<Long> list);
}