package com.xkr.web.controller.admin;

import com.xkr.common.ErrorStatus;
import com.xkr.domain.dto.admin.AdminIndexDTO;
import com.xkr.domain.dto.admin.permission.ListPermissionDTO;
import com.xkr.service.AdminService;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.admin.AdminIndexVO;
import com.xkr.web.model.vo.admin.permission.ListPermissionVO;
import com.xkr.web.model.vo.admin.permission.PermissionVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/29
 */
@RequestMapping("/api/admin/permission")
@Controller
public class PermissionController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private AdminService adminService;

    /**
     * 管理员登出
     *
     * @return
     */
    @RequestMapping(value = "/list", method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult permissionList() {
        try {
            ListPermissionDTO listPermissionDTO =  adminService.getListPermissionDTO();

            if (!ErrorStatus.OK.equals(listPermissionDTO.getStatus())) {
                return new BasicResult<>(listPermissionDTO.getStatus());
            }

            ListPermissionVO listPermissionVO = new ListPermissionVO();

            buildListPermissionVO(listPermissionVO,listPermissionDTO);

            return new BasicResult<>(listPermissionVO);
        } catch (Exception e) {
            logger.error("后台获取权限列表异常", e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    private void buildListPermissionVO(ListPermissionVO listPermissionVO, ListPermissionDTO listPermissionDTO){
        listPermissionDTO.getList().forEach(permissionDTO -> {
            PermissionVO permissionVO = new PermissionVO();
            permissionVO.setPermissionName(permissionDTO.getPermissionName());
            permissionVO.setPermissionId(permissionDTO.getPermissionId());
            listPermissionVO.getList().add(permissionVO);
        });
        listPermissionVO.setTotalCount(listPermissionDTO.getTotalCount());
    }


}
