package com.xkr.dao.mapper;

import com.xkr.domain.entity.XkrResourceComment;
import com.xkr.util.CustomerMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface XkrResourceCommentMapper extends CustomerMapper<XkrResourceComment> {

    XkrResourceComment getCommentById(Map<String,Object> params);

    List<XkrResourceComment> getCommentsByIds(Map<String,Object> params);

    Integer batchUpdateCommentByIds(Map<String,Object> params);

    List<XkrResourceComment> getCommentsByResourceId(Map<String, Object> params);
}