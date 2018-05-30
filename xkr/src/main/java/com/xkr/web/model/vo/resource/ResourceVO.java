package com.xkr.web.model.vo.resource;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.xkr.util.DateUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/11
 */
public class ResourceVO implements Serializable{


    private static final long serialVersionUID = -9157357201766834210L;
    /**
     * "title":"标题",
     "content":"内容简要",
     "resourceId":"资源id",
     "classId":"栏目id",
     "className":"栏目",
     "rootClassId":"根栏目id",
     "rootClassName":"根栏目",
     "userName":"会员账号",
     "userId":"会员id",
     "updateTime":"更新时间",
     "downloadCount":"下载量",
     "cost":"花费",
     "status":"状态",
     "report":"举报状态"
     */

    //资源id
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long resourceId;

    //标题
    private String title;

    private String content;

    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long classId;

    private String className;

    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long rootClassId;

    private String rootClassName;

    private String userName;

    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long userId;

    private Integer downloadCount;

    private Integer cost;

    private Integer status;

    private Integer report;

    private String updateTime;

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

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

    public Long getRootClassId() {
        return rootClassId;
    }

    public void setRootClassId(Long rootClassId) {
        this.rootClassId = rootClassId;
    }

    public String getRootClassName() {
        return rootClassName;
    }

    public void setRootClassName(String rootClassName) {
        this.rootClassName = rootClassName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getReport() {
        return report;
    }

    public void setReport(Integer report) {
        this.report = report;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = DateUtil.yyyyMMdd.format(updateTime);
    }
}
