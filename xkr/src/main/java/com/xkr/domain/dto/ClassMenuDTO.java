package com.xkr.domain.dto;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
public class ClassMenuDTO implements Serializable{

    private static final long serialVersionUID = 9078845808780395079L;

    private Long classId;

    private String className;

    private List<ClassMenuDTO> child = Lists.newArrayList();

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

    public List<ClassMenuDTO> getChild() {
        return child;
    }

    public void setChild(List<ClassMenuDTO> child) {
        this.child = child;
    }
}
