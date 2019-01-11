package com.xkr.web.controller.admin;

import com.xkr.common.ErrorStatus;
import com.xkr.common.PermissionEnum;
import com.xkr.domain.dto.admin.permission.ListPermissionDTO;
import com.xkr.service.AdminService;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.admin.permission.ListPermissionVO;
import com.xkr.web.model.vo.admin.permission.PermissionVO;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
@RequestMapping("/api/admin/data")
@Controller
public class AdminDataAnyController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 数据分析权限验证
     *
     * @return
     */
    @RequiresPermissions(PermissionEnum.Constant.DATAANY_PERM)
    @RequestMapping(value = "/verify", method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult dataAnaVerify() {
        try {

            return new BasicResult<>(ErrorStatus.OK);
        } catch (Exception e) {
            logger.error("数据分析权限验证失败", e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

}
