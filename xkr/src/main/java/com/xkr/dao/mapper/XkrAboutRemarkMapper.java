package com.xkr.dao.mapper;

import com.xkr.domain.entity.XkrAboutRemark;
import com.xkr.util.CustomerMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface XkrAboutRemarkMapper extends CustomerMapper<XkrAboutRemark> {
    List<XkrAboutRemark> getAllList();

    XkrAboutRemark getRemarkById(Long id);

    Integer batchUpdateRemarkByIds(Map<String,Object> params);
}