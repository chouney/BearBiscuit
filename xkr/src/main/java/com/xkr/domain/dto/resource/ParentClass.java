package com.xkr.domain.dto.resource;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;

import java.io.Serializable;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class ParentClass implements Serializable{
    private static final long serialVersionUID = -7682073591218877055L;

    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long classId;

    private String className;

    private ParentClass pClass;

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public ParentClass getpClass() {
        return pClass;
    }

    public void setpClass(ParentClass pClass) {
        this.pClass = pClass;
    }
}
