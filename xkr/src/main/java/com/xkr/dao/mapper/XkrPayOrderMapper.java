package com.xkr.dao.mapper;

import com.xkr.domain.entity.XkrPayOrder;
import com.xkr.util.CustomerMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface XkrPayOrderMapper extends CustomerMapper<XkrPayOrder> {

    XkrPayOrder getOrderByOrderId(Map<String,Object> params);

    Integer payOrderStatusByOrderId(Map<String,Object> params);
}