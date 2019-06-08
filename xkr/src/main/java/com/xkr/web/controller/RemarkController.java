package com.xkr.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xkr.common.CaptchaEnum;
import com.xkr.common.ErrorStatus;
import com.xkr.common.PermissionEnum;
import com.xkr.common.annotation.valid.Captcha;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.message.ListMessageDTO;
import com.xkr.domain.entity.XkrUser;
import com.xkr.service.RemarkService;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.message.ListMessageVO;
import com.xkr.web.model.vo.message.MessageVO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.chris.redbud.validator.annotation.MethodValidate;
import org.chris.redbud.validator.result.ValidResult;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@Controller
@RequestMapping("/api/remark")
public class RemarkController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RemarkService remarkService;

    @ResponseBody
    @MethodValidate
    @RequestMapping("/submit")
    public BasicResult submitRemark(
            @NotBlank
            @RequestParam(name = "content") String content,
            @NotBlank
            @RequestParam(name = "qq") String qq,
            @NotBlank
            @RequestParam(name = "phone") String phone,
            @Captcha(CaptchaEnum.REMARK_TYPE)
            @RequestParam(name = "captcha") String captcha,
            ValidResult result) {
        if (result.hasErrors()) {
            return new BasicResult(result);
        }
        try {
            XkrUser user = (XkrUser) SecurityUtils.getSubject().getPrincipal();
            ResponseDTO<Long> responseDTO = remarkService.submitRemark(user, qq, phone, content);

            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult(responseDTO.getStatus());
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("remarkId", String.valueOf(responseDTO.getData()));
            return new BasicResult<>(jsonObject);
        } catch (Exception e) {
            logger.error("RemarkController submitRemark error ,content:{},qq:{},phone:{},captcha:{}", content, qq, phone, captcha, e);
        }
        return new BasicResult(ErrorStatus.ERROR);
    }

}
