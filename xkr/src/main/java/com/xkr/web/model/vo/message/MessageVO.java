package com.xkr.web.model.vo.message;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
public class MessageVO implements Serializable{


    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final long serialVersionUID = -633513867681979271L;

    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long id;

    private String msg;

    private String date;

    private boolean hasRead;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDate(Date date) {
        if(Objects.isNull(date)){
            return;
        }
        this.date = DATE_FORMAT.format(date);
    }

    public boolean isHasRead() {
        return hasRead;
    }

    public void setHasRead(boolean hasRead) {
        this.hasRead = hasRead;
    }
}
