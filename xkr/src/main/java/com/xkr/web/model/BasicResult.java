package com.xkr.web.model;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.xkr.common.ErrorStatus;
import org.assertj.core.util.Lists;
import org.chris.redbud.validator.result.ValidError;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/7
 */
public class BasicResult implements Serializable{

    private static final long serialVersionUID = -5107846354144554659L;

    private int code = ErrorStatus.OK.getCode();

    private String msg = ErrorStatus.OK.getDesc();

    private Map<String,Object> data;

    private Map<String,Object> ext;

    public BasicResult(ErrorStatus status) {
        this.code = status.getCode();
        this.msg = status.getDesc();
        this.data = ImmutableMap.of();
        this.ext = Collections.EMPTY_MAP;
    }

    public BasicResult(List<ValidError> errors) {
        this.code = ErrorStatus.PARAM_ERROR.getCode();
        this.msg = ErrorStatus.PARAM_ERROR.getDesc();
        this.data = ImmutableMap.of("error", errors.stream().map(ValidError::toString).collect(Collectors.toList()));
        this.ext = Collections.EMPTY_MAP;
    }

    public BasicResult(Map<String,Object> data) {
        this.data = data;
    }

    public BasicResult(Map<String,Object> data, Map<String,Object> ext) {
        this.data = data;
        this.ext = ext;
    }

    public BasicResult(ErrorStatus status, Map<String,Object> data, Map<String,Object> ext) {
        this.code = status.getCode();
        this.msg = status.getDesc();
        this.data = data;
        this.ext = ext;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String,Object> getData() {
        return data;
    }

    public void setData(Map<String,Object> data) {
        this.data = data;
    }

    public Map<String,Object> getExt() {
        return ext;
    }

    public void setExt(Map<String,Object> ext) {
        this.ext = ext;
    }


}
