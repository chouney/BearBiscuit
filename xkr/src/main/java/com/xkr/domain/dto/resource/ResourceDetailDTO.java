package com.xkr.domain.dto.resource;

import com.google.common.collect.Maps;
import com.xkr.domain.XkrClassAgent;
import com.xkr.domain.dto.BaseDTO;
import com.xkr.domain.entity.XkrClass;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/17
 */
public class ResourceDetailDTO extends BaseDTO implements Serializable{

    private static final long serialVersionUID = -6709344003733973179L;

    private Long resourceId;

    private Long userId;

    private String userName;

    private ParentClass pClass;

    private String title;

    private String detail;

    private String fileSize;

    private String downloadCount;

    private Integer cost;

    private Date updateTime;

    private Byte resStatus;

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ParentClass getpClass() {
        return pClass;
    }

    public void setpClass(ParentClass pClass) {
        this.pClass = pClass;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(String downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public void buildParentClass(XkrClass currentClass,List<XkrClass> classList){
        this.pClass = new ParentClass();
        this.pClass.setClassId(currentClass.getId());
        this.pClass.setClassName(currentClass.getClassName());
        Map<Long, XkrClass> tmpMap = Maps.newHashMap();
        classList.forEach(xkrClass -> tmpMap.put(xkrClass.getId(), xkrClass));
        XkrClass parent = currentClass;
        ParentClass currentIndex = this.pClass;
        while((parent = tmpMap.get(parent.getParentClassId())).getId().intValue() != XkrClassAgent.ROOT_CLASS_ID){
            ParentClass parentClass = new ParentClass();
            parentClass.setClassName(parent.getClassName());
            parentClass.setClassId(parent.getId());

            currentIndex.setpClass(parentClass);
            currentIndex = parentClass;

        }
    }

    public Byte getResStatus() {
        return resStatus;
    }

    public void setResStatus(Byte resStatus) {
        this.resStatus = resStatus;
    }
}
