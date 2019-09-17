package com.xkr.web.model.vo;

import com.xkr.common.ErrorStatus;
import com.xkr.web.model.BasicResult;

import java.io.Serializable;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/18
 */
public class FileUploadReturnVO implements Serializable {

    private static final long serialVersionUID = -4633516381238394022L;

    private String task_id;

    private String msg;

    public FileUploadReturnVO(String task_id, String msg) {
        this.task_id = task_id;
        this.msg = msg;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
