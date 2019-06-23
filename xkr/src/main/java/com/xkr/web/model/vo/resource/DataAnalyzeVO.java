package com.xkr.web.model.vo.resource;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/25
 */
public class DataAnalyzeVO implements Serializable {


    private static final long serialVersionUID = -1843167928400550015L;
    private Long id;

    private String title;

    private Integer calCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCalCount() {
        return calCount;
    }

    public void setCalCount(Integer calCount) {
        this.calCount = calCount;
    }
}
