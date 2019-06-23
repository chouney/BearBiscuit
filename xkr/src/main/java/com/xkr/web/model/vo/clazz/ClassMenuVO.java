package com.xkr.web.model.vo.clazz;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
public class ClassMenuVO implements Serializable{

    private static final long serialVersionUID = 8235865000438668222L;

    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long classId;

    private String className;

    private int count;

    private List<ClassMenuVO> child = Lists.newArrayList();

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

    public List<ClassMenuVO> getChild() {
        return child;
    }

    public void setChild(List<ClassMenuVO> child) {
        this.child = child;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
