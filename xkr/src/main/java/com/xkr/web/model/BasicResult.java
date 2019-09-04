package com.xkr.web.model;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.xkr.common.ErrorStatus;
import org.chris.redbud.validator.result.ValidResult;

import java.io.Serializable;
import java.util.Map;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/7
 */
public class BasicResult<T extends Serializable> implements Serializable{

    private static final long serialVersionUID = -5107846354144554659L;

    private int code = ErrorStatus.OK.getCode();

    private String msg = ErrorStatus.OK.getDesc();

    private T data ;

    private Map<String,Object> ext;


    public BasicResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = (T) new JSONObject();
        this.ext = Maps.newHashMap();
    }

    public BasicResult(ErrorStatus status) {
        this.code = status.getCode();
        this.msg = status.getDesc();
        this.data = (T) new JSONObject();
        this.ext = Maps.newHashMap();
    }

    public BasicResult(ValidResult errors) {
        this.code = ErrorStatus.PARAM_ERROR.getCode();
        this.msg = errors.toString();
        this.data = (T) new JSONObject();
        this.ext = Maps.newHashMap();
    }

    public BasicResult(T data) {
        this.data = data;
    }

    public BasicResult(T data, Map<String,Object> ext) {
        this.data = data;
        this.ext = ext;
    }

    public BasicResult(ErrorStatus status, T data, Map<String,Object> ext) {
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Map<String,Object> getExt() {
        return ext;
    }

    public void setExt(Map<String,Object> ext) {
        this.ext = ext;
    }


}
