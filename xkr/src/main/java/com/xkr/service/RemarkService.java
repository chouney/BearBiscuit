package com.xkr.service;

import com.xkr.common.ErrorStatus;
import com.xkr.common.LoginEnum;
import com.xkr.domain.XkrRemarkAgent;
import com.xkr.domain.XkrUserAgent;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.entity.XkrAboutRemark;
import com.xkr.domain.entity.XkrUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@Service
public class RemarkService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XkrRemarkAgent remarkAgent;

    public ResponseDTO<Long> submitRemark(XkrUser user,
                                          String qq, String phone,
                                          String content) {
        if (Objects.isNull(user) ||
                StringUtils.isEmpty(qq) || StringUtils.isEmpty(phone) ||
                StringUtils.isEmpty(content)) {
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }

        if (XkrUserAgent.USER_STATUS_NORMAL != user.getStatus()) {
            return new ResponseDTO<>(ErrorStatus.REMARK_USER_NOT_PRILIVEGED);
        }

        XkrAboutRemark remark = remarkAgent.saveNewRemark(null, LoginEnum.CUSTOMER,
                user.getId(),qq,phone,content);
        if(Objects.isNull(remark)){
            return new ResponseDTO<>(ErrorStatus.ERROR);
        }
        return new ResponseDTO<>(remark.getId());
    }

}
