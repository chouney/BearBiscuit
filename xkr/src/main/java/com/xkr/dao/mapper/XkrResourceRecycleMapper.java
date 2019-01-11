package com.xkr.dao.mapper;

import com.xkr.domain.entity.XkrResource;
import com.xkr.domain.entity.XkrResourceRecycle;
import com.xkr.util.CustomerMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface XkrResourceRecycleMapper extends CustomerMapper<XkrResourceRecycle> {

    Integer batchDeleteResourceRecycleByIds(List<Long> resourceIds);

}