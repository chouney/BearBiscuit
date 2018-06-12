package com.xkr.web.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.xkr.common.ErrorStatus;
import com.xkr.common.annotation.valid.IsNumberic;
import com.xkr.core.shiro.LoginAuthenticationToken;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.admin.AdminIndexDTO;
import com.xkr.domain.dto.admin.account.AdminAccountDetailDTO;
import com.xkr.domain.dto.admin.account.ListAdminAccountDTO;
import com.xkr.service.AdminService;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.admin.AdminIndexVO;
import com.xkr.web.model.vo.admin.account.AdminAccountDetailVO;
import com.xkr.web.model.vo.admin.account.AdminAccountVO;
import com.xkr.web.model.vo.admin.account.ListAdminAccountVO;
import org.chris.redbud.validator.annotation.MethodValidate;
import org.chris.redbud.validator.result.ValidResult;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/29
 */
@RequestMapping("/api/admin")
@Controller
public class AdminIndexController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private AdminService adminService;

    /**
     * 管理员登出
     *
     * @return
     */
    @RequestMapping(value = "/index", method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult adminIndex() {
        try {
            AdminIndexDTO adminIndexDTO =  adminService.getAdminIndexDTO();

            if (!ErrorStatus.OK.equals(adminIndexDTO.getStatus())) {
                return new BasicResult<>(adminIndexDTO.getStatus());
            }

            AdminIndexVO adminIndexVO =  new AdminIndexVO();

            buildAdminIndexVO(adminIndexVO,adminIndexDTO);

            return new BasicResult<>(adminIndexVO);
        } catch (Exception e) {
            logger.error("后台管理员首页异常", e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    private void buildAdminIndexVO(AdminIndexVO adminIndexVO, AdminIndexDTO adminIndexDTO){
        adminIndexVO.setAccountName(adminIndexDTO.getAccountName());
        adminIndexVO.setDownloadCount(adminIndexDTO.getDownloadCount());
        adminIndexVO.setLastLoginDate(adminIndexDTO.getLastLoginDate());
        adminIndexVO.setLoginCount(adminIndexDTO.getLoginCount());
        adminIndexVO.setRegCount(adminIndexDTO.getRegCount());
        adminIndexVO.setRoleName(adminIndexDTO.getRoleName());
        adminIndexVO.setUploadCount(adminIndexDTO.getUploadCount());
        adminIndexVO.setUserTotalCount(adminIndexDTO.getUserTotalCount());
    }

}
