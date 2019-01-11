package com.xkr.web.controller.admin;

import com.google.common.collect.ImmutableList;
import com.xkr.common.ErrorStatus;
import com.xkr.domain.dto.admin.index.AdminIndexDTO;
import com.xkr.domain.dto.admin.index.ResourceAccountDTO;
import com.xkr.domain.dto.admin.index.UserAccountDTO;
import com.xkr.domain.dto.resource.ResourceStatusEnum;
import com.xkr.domain.dto.user.UserStatusEnum;
import com.xkr.service.AdminService;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.admin.index.AdminIndexVO;
import com.xkr.web.model.vo.admin.index.ResourceAccountVO;
import com.xkr.web.model.vo.admin.index.UserAccountVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

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
        UserAccountVO userAccountVO = new UserAccountVO();
        ResourceAccountVO resourceAccountVO = new ResourceAccountVO();
        ResourceAccountVO designAccountVO = new ResourceAccountVO();

        if(Objects.nonNull(adminIndexDTO.getUserAccountDTO())) {
            buildUserAccountVO(userAccountVO, adminIndexDTO.getUserAccountDTO());
        }
        if(Objects.nonNull(adminIndexDTO.getDesignAccountDTO())) {
            buildResourceAccountVO(designAccountVO, adminIndexDTO.getDesignAccountDTO());
        }
        if(Objects.nonNull(adminIndexDTO.getResoureAccountDTO())) {
            buildResourceAccountVO(resourceAccountVO, adminIndexDTO.getResoureAccountDTO());
        }
        adminIndexVO.setLastLoginDate(adminIndexDTO.getLastLoginDate());
        adminIndexVO.setAccountName(adminIndexDTO.getAccountName());

        adminIndexVO.setUser(userAccountVO);
        adminIndexVO.setResource(resourceAccountVO);
        adminIndexVO.setDesign(designAccountVO);
    }

    private void buildUserAccountVO(UserAccountVO userAccountVO,UserAccountDTO userAccountDTO){
        userAccountVO.setLoginCount(userAccountDTO.getLoginCount());
        userAccountVO.setRegCount(userAccountDTO.getRegCount());
        userAccountVO.setUserActiveTotalCount(userAccountDTO.getUserActiveTotalCount());
        userAccountVO.setUserTotalCount(userAccountDTO.getUserTotalCount());
    }

    private void buildResourceAccountVO(ResourceAccountVO resourceAccountVO, ResourceAccountDTO resourceAccountDTO){
        resourceAccountVO.setDownloadCount(resourceAccountDTO.getDownloadCount());
        resourceAccountVO.setUploadCount(resourceAccountDTO.getUploadCount());
        resourceAccountVO.setTotalCount(resourceAccountDTO.getTotalCount());
        resourceAccountVO.setUnverifiedCount(resourceAccountDTO.getUnverifiedCount());
    }

}
