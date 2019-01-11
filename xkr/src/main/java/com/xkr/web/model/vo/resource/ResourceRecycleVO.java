package com.xkr.web.model.vo.resource;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.util.DateUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/11
 */
public class ResourceRecycleVO implements Serializable{

    /**
     * 资源id
     */
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long resId;

    /**
     * 资源标题
     */
    private String resourceTitle;

    /**
     * 分类id
     */
    private String className;

    /**
     * 分类id
     */
    private String userName;

    /**
     * 操作员id
     */
    private String optName;

    /**
     * 更新时间
     */
    private String date;

    public Long getResId() {
        return resId;
    }

    public void setResId(Long resId) {
        this.resId = resId;
    }

    public String getResourceTitle() {
        return resourceTitle;
    }

    public void setResourceTitle(String resourceTitle) {
        this.resourceTitle = resourceTitle;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOptName() {
        return optName;
    }

    public void setOptName(String optName) {
        this.optName = optName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(Date date) {
        if(Objects.isNull(date)){
            return;
        }
        this.date = DateUtil.yyyyMMddHHmmss.format(date);
    }
}
