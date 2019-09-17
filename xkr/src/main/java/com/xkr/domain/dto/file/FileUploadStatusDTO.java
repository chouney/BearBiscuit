package com.xkr.domain.dto.file;

import com.xkr.common.ErrorStatus;
import com.xkr.domain.dto.BaseDTO;

import java.io.Serializable;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/18
 */
public class FileUploadStatusDTO extends BaseDTO implements Serializable{


    private static final long serialVersionUID = -8195039060058091257L;

    private String taskId;

    private String resultMsg;

    public FileUploadStatusDTO(String taskId, String resultMsg) {
        this.taskId = taskId;
        this.resultMsg = resultMsg;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public FileUploadStatusDTO(ErrorStatus status) {
        super(status);
    }

}
