package com.xkr.web.model.vo.admin.index;

import java.io.Serializable;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/7/18
 */
public class ResourceAccountVO implements Serializable{

    private static final long serialVersionUID = 503164470725216017L;
    /**
     * "uploadCount":412, //今日上传人数
     "downloadCount":412, //今日下载人数
     "totalCount":444,//总量
     "unverifedCount":11//未审核
     */

    private Integer uploadCount;

    private Integer downloadCount;

    private Integer totalCount;

    private Integer unverifiedCount;

    public Integer getUploadCount() {
        return uploadCount;
    }

    public void setUploadCount(Integer uploadCount) {
        this.uploadCount = uploadCount;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getUnverifiedCount() {
        return unverifiedCount;
    }

    public void setUnverifiedCount(Integer unverifiedCount) {
        this.unverifiedCount = unverifiedCount;
    }
}
