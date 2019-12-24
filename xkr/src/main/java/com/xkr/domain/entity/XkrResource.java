package com.xkr.domain.entity;

import org.apache.commons.compress.utils.CharsetNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Date;
import javax.persistence.*;

@Table(name = "xkr_resource")
public class XkrResource extends BaseEntity{
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 资源id
     */
    @Id
    private Long id;

    /**
     * 栏目id
     */
    @Column(name = "class_id")
    private Long classId;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 资源积分
     */
    private Integer cost;

    /**
     * 资源状态:1为正常,2为未审核,3为冻结,4为删除
     */
    private Byte status;

    /**
     * 举报状态：0为正常,1为被举报
     */
    private Byte report;

    /**
     * 文件大小
     */
    @Column(name = "file_size")
    private String fileSize;

    /**
     * 文件标题,80字内
     */
    private String title;

    /**
     * 资源uri,表示资源根目录(md5加密后文件名(带后缀的))
     */
    @Column(name = "resource_url")
    private String resourceUrl;

    /**
     * 下载量
     */
    @Column(name = "download_count")
    private Integer downloadCount;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 扩展字段，存储(md5加密后文件名(不带后缀的))
     */
    private byte[] ext;

    /**
     * 资源详情，5000字内
     */
    private String detail;

    /**
     * 获取资源id
     *
     * @return id - 资源id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置资源id
     *
     * @param id 资源id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取栏目id
     *
     * @return class_id - 栏目id
     */
    public Long getClassId() {
        return classId;
    }

    /**
     * 设置栏目id
     *
     * @param classId 栏目id
     */
    public void setClassId(Long classId) {
        this.classId = classId;
    }

    /**
     * 获取用户id
     *
     * @return user_id - 用户id
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置用户id
     *
     * @param userId 用户id
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取资源积分
     *
     * @return cost - 资源积分
     */
    public Integer getCost() {
        return cost;
    }

    /**
     * 设置资源积分
     *
     * @param cost 资源积分
     */
    public void setCost(Integer cost) {
        this.cost = cost;
    }

    /**
     * 获取资源状态:1为正常,2为未审核,3为冻结,4为删除
     *
     * @return status - 资源状态:1为正常,2为未审核,3为冻结,4为删除
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置资源状态:1为正常,2为未审核,3为冻结,4为删除
     *
     * @param status 资源状态:1为正常,2为未审核,3为冻结,4为删除
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * 获取举报状态：0为正常,1为被举报
     *
     * @return report - 举报状态：0为正常,1为被举报
     */
    public Byte getReport() {
        return report;
    }

    /**
     * 设置举报状态：0为正常,1为被举报
     *
     * @param report 举报状态：0为正常,1为被举报
     */
    public void setReport(Byte report) {
        this.report = report;
    }

    /**
     * 获取文件大小
     *
     * @return file_size - 文件大小
     */
    public String getFileSize() {
        return fileSize;
    }

    /**
     * 设置文件大小
     *
     * @param fileSize 文件大小
     */
    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * 获取文件标题,80字内
     *
     * @return title - 文件标题,80字内
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置文件标题,80字内
     *
     * @param title 文件标题,80字内
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取资源url
     *
     * @return resource_url - 资源url
     */
    public String getResourceUrl() {
        return resourceUrl;
    }

    /**
     * 设置资源url
     *
     * @param resourceUrl 资源url
     */
    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    /**
     * 获取下载量
     *
     * @return download_count - 下载量
     */
    public Integer getDownloadCount() {
        return downloadCount;
    }

    /**
     * 设置下载量
     *
     * @param downloadCount 下载量
     */
    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取更新时间
     *
     * @return update_time - 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取扩展字段，存储内容简要等,file_size等
     *
     * @return ext - 扩展字段，存储内容简要等,file_size等
     */
    public String getExt() {
        try {
            return new String(ext, CharsetNames.UTF_8);
        } catch (UnsupportedEncodingException e) {
            logger.error("Resource getExt UTF8转义,错误 ",e);
        }
        return new String(ext);
    }

    /**
     * 设置扩展字段，存储内容简要等,file_size等
     *
     * @param ext 扩展字段，存储内容简要等,file_size等
     */
    public void setExt(String ext) {
        this.ext = ext.getBytes();
    }

    /**
     * 获取资源详情，5000字内
     *
     * @return detail - 资源详情，5000字内
     */
    public String getDetail() {
        return detail;
    }

    /**
     * 设置资源详情，5000字内
     *
     * @param detail 资源详情，5000字内
     */
    public void setDetail(String detail) {
        this.detail = detail;
    }
}