package com.xkr.web.controller.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xkr.common.ErrorStatus;
import com.xkr.common.annotation.valid.IsNumberic;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.admin.account.ListAdminAccountDTO;
import com.xkr.domain.dto.admin.role.ListAdminRoleDTO;
import com.xkr.domain.dto.admin.role.RoleDetailDTO;
import com.xkr.service.AdminService;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.admin.account.ListAdminAccountVO;
import com.xkr.web.model.vo.admin.role.AdminRoleVO;
import com.xkr.web.model.vo.admin.role.ListAdminRoleVO;
import com.xkr.web.model.vo.admin.role.RoleDetailVO;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/29
 */
@RequestMapping("/api/admin/role")
@Controller
public class RoleController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private AdminService adminService;


    @RequestMapping(value = "/list", method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult getRoleList(
            @RequestParam(name = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        try {
            ListAdminRoleDTO listAdminRoleDTO = adminService.getListAdminRoleDTO(pageNum,size);

            if(!ErrorStatus.OK.equals(listAdminRoleDTO.getStatus())){
                return new BasicResult(listAdminRoleDTO.getStatus());
            }

            ListAdminRoleVO listAdminRoleVO = new ListAdminRoleVO();

            buildListAdminRoleVO(listAdminRoleVO,listAdminRoleDTO);

            return new BasicResult<>(listAdminRoleVO);
        } catch (Exception e) {
            logger.error("后台获取角色列表异常 ,pageNum:{},size:{}", pageNum,size, e);
        }
        return new BasicResult(ErrorStatus.ERROR);
    }

    /**
     * 角色详情
     *
     * @return
     */
    @RequestMapping(value = "/detail", method = {RequestMethod.GET})
    @ResponseBody
    @MethodValidate
    public BasicResult roleDetail(
            @IsNumberic
            @RequestParam(name = "roleId") String roleId,
            ValidResult result) {
        if (result.hasErrors()) {
            return new BasicResult(result);
        }
        try {
            RoleDetailDTO roleDetailDTO = adminService.getRoleDetailByRoleId(Integer.valueOf(roleId));

            if (!ErrorStatus.OK.equals(roleDetailDTO.getStatus())) {
                return new BasicResult<>(roleDetailDTO.getStatus());
            }

            RoleDetailVO roleDetailVO = new RoleDetailVO();

            buildRoleDetailVO(roleDetailVO, roleDetailDTO);

            return new BasicResult<>(roleDetailVO);
        } catch (Exception e) {
            logger.error("后台获取角色详情,roleId:{}", roleId, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 角色添加
     *
     * @return
     */
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    @ResponseBody
    @MethodValidate
    public BasicResult addRole(
            @NotBlank
            @RequestParam(name = "roleName") String roleName,
            @NotBlank
            @RequestParam(name = "roleDetail") String roleDetail,
            @RequestParam(name = "permissionIds[]") String[] permissionIds,
            ValidResult result) {
        if (result.hasErrors()) {
            return new BasicResult(result);
        }
        try {
            List<Integer> ids = Arrays.stream(permissionIds).map(Integer::valueOf).collect(Collectors.toList());

            ResponseDTO<Integer> responseDTO = adminService.saveNewRole(roleName, roleDetail, ids);

            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult<>(responseDTO.getStatus());
            }

            JSONObject output = new JSONObject();

            output.put("roleId", String.valueOf(responseDTO.getData()));

            return new BasicResult<>(output);
        } catch (Exception e) {
            logger.error("后台添加角色,roleName:{},roleDetail:{},permissionIds:{}", roleName, roleDetail, JSON.toJSONString(permissionIds), e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 角色更改
     *
     * @return
     */
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    @ResponseBody
    @MethodValidate
    public BasicResult updateRole(
            @IsNumberic
            @RequestParam(name = "roleId") String roleId,
            @NotBlank
            @RequestParam(name = "roleName") String roleName,
            @NotBlank
            @RequestParam(name = "roleDetail") String roleDetail,
            @RequestParam(name = "permissionIds[]") String[] permissionIds,
            ValidResult result) {
        if (result.hasErrors()) {
            return new BasicResult(result);
        }
        try {
            List<Integer> ids = Arrays.stream(permissionIds).map(Integer::valueOf).collect(Collectors.toList());

            ResponseDTO<Boolean> responseDTO = adminService.updateRoleById(Integer.valueOf(roleId),roleName, roleDetail, ids);

            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult<>(responseDTO.getStatus());
            }

            return new BasicResult<>(responseDTO.getData());
        } catch (Exception e) {
            logger.error("后台更改角色,roleId:{},roleName:{},roleDetail:{},permissionIds:{}", roleId,roleName, roleDetail, JSON.toJSONString(permissionIds), e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 角色删除
     *
     * @return
     */
    @RequestMapping(value = "/del", method = {RequestMethod.POST})
    @ResponseBody
    public BasicResult batchDelRole(
            @RequestParam(name = "roleIds[]") String[] roleIds) {
        try {
            List<Integer> ids = Arrays.stream(roleIds).map(Integer::valueOf).collect(Collectors.toList());

            ResponseDTO<Boolean> responseDTO = adminService.batchDeleteRoleById(ids);

            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult<>(responseDTO.getStatus());
            }

            return new BasicResult<>(responseDTO.getData());
        } catch (Exception e) {
            logger.error("后台删除角色,roleIds:{}", JSON.toJSONString(roleIds), e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    private void buildRoleDetailVO(RoleDetailVO roleDetailVO, RoleDetailDTO roleDetailDTO) {
        roleDetailVO.setPermissionIds(roleDetailDTO.getPermissionIds());
        roleDetailVO.setRoleDetail(roleDetailDTO.getRoleDetail());
        roleDetailVO.setRoleName(roleDetailDTO.getRoleName());
    }

    private void buildListAdminRoleVO(ListAdminRoleVO listAdminRoleVO,ListAdminRoleDTO listAdminRoleDTO){
        listAdminRoleDTO.getList().forEach(adminRoleDTO -> {
            AdminRoleVO adminRoleVO = new AdminRoleVO();
            adminRoleDTO.setRoleName(adminRoleDTO.getRoleDetail());
            adminRoleDTO.setRoleId(adminRoleDTO.getRoleId());
            adminRoleDTO.setRoleDetail(adminRoleDTO.getRoleName());
            listAdminRoleVO.getList().add(adminRoleVO);
        });
        listAdminRoleVO.setTotalCount(listAdminRoleDTO.getTotalCount());
    }

}
