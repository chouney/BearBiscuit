package com.xkr.dao.mapper;

import com.xkr.domain.entity.XkrDataAnalyze;
import com.xkr.domain.entity.XkrUser;
import com.xkr.util.CustomerMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface XkrDataAnalyzeMapper extends CustomerMapper<XkrDataAnalyze> {

    List<XkrDataAnalyze> selectByRange(Map<String,Object> params);

    XkrDataAnalyze selectByTitle(String title);
}