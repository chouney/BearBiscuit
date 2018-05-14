package com.xkr.domain.dto.search;

import java.util.Date;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/11
 */
public class ResourceIndexDTO extends BaseIndexDTO{

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
    private Long resourceId;

    //标题
    private String title;

    private String content;

    private String classId;

    private String className;

    private String rootClassId;

    private String rootClassName;

    private String userName;

    private Long userId;

    private Integer downloadCount;

    private Integer cost;

    private Integer status;

    private Integer report;

    private Date updateTime;

    public ResourceIndexDTO() {
        super("xkr", "resource");
    }

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

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getRootClassId() {
        return rootClassId;
    }

    public void setRootClassId(String rootClassId) {
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

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getReport() {
        return report;
    }

    public void setReport(int report) {
        this.report = report;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String getIndexKey() {
        return String.valueOf(this.resourceId);
    }
}
