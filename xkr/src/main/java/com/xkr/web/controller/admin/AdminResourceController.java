package com.xkr.web.controller.admin;

import com.alibaba.fastjson.JSON;
import com.xkr.common.ErrorMsgConst;
import com.xkr.common.ErrorStatus;
import com.xkr.common.PermissionEnum;
import com.xkr.domain.XkrClassAgent;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.resource.ListResourceDTO;
import com.xkr.domain.dto.resource.ListResourceRecycleDTO;
import com.xkr.domain.dto.resource.ResourceStatusEnum;
import com.xkr.service.ResourceService;
import com.xkr.util.DateUtil;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.admin.resource.AdminResourceVO;
import com.xkr.web.model.vo.admin.resource.ListAdminResourceVO;
import com.xkr.web.model.vo.resource.ListResourceRecycleVO;
import com.xkr.web.model.vo.resource.ListResourceVO;
import com.xkr.web.model.vo.resource.ResourceRecycleVO;
import com.xkr.web.model.vo.resource.ResourceVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.chris.redbud.validator.annotation.MethodValidate;
import org.chris.redbud.validator.result.ValidResult;
import org.chris.redbud.validator.validate.annotation.ContainsInt;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/27
 */
@Controller
@RequestMapping("/api/admin/res")
public class AdminResourceController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ResourceService resourceService;

    /**
     * 资源搜索
     *
     * @return
     */
    @RequiresPermissions(value = {PermissionEnum.Constant.RESOURCE_PERM, PermissionEnum.Constant.DESIGN_PERM}, logical = Logical.OR)
    @RequestMapping(value = "/search_list", method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult<ListAdminResourceVO> getResourceListBySearchWord(
            @RequestParam(name = "startDate", required = false, defaultValue = "") String startDate,
            @RequestParam(name = "keyWord", required = false, defaultValue = "") String keyWord,
            @RequestParam(name = "userName", required = false, defaultValue = "") String userName,
            @RequestParam(name = "type") Integer type,
            @RequestParam(name = "status", required = false, defaultValue = "1") Integer status,
            @RequestParam(name = "report", required = false, defaultValue = "2") Integer report,
            @RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {

        ListAdminResourceVO resourceVOs = new ListAdminResourceVO();
        try {
            //权限校验
            Subject subject = SecurityUtils.getSubject();
            if (XkrClassAgent.ROOT_DESIGN_CLASS_ID == type) {
                //如果是毕设,则校验毕设权限
                if (!subject.isPermitted(PermissionEnum.Constant.DESIGN_PERM)) {
                    return new BasicResult<>(ErrorStatus.UNAUTHORIZED);
                }
            } else if (XkrClassAgent.ROOT_RESOURCE_CLASS_ID == type) {
                //如果是资源,则校验资源权限
                if (!subject.isPermitted(PermissionEnum.Constant.RESOURCE_PERM)) {
                    return new BasicResult<>(ErrorStatus.UNAUTHORIZED);
                }

            } else {
                //参数错误
                return new BasicResult<>(ErrorStatus.PARAM_ERROR);
            }

//            Date sDate = Date.from(LocalDateTime.now().minusWeeks(1).toInstant(ZoneOffset.UTC));
            Date sDate = null;
            if (StringUtils.isNotEmpty(startDate)) {
                sDate = DateUtil.stringToDate(startDate, "yyyy-MM-dd");
            }
            if (report == 2) {
                //举报默认是2全部,搜索不进行过滤
                report = null;
            }

            ListResourceDTO resourceDTOs = resourceService.getResourceSearchByAdmin(keyWord, sDate,
                    null, type,userName, ResourceStatusEnum.getByCode(status), report, pageNum, size);

            if (!ErrorStatus.OK.equals(resourceDTOs.getStatus())) {
                return new BasicResult<>(resourceDTOs.getStatus());
            }

            buildListResourceVO(resourceVOs, resourceDTOs);

            return new BasicResult<>(resourceVOs);
        } catch (Exception e) {
            logger.error("后台获取搜索获取资源异常,startDate:{},keyWord:{},type:{},status:{},report:{},pageNum:{},size:{}", startDate, keyWord, type, status, report, pageNum, size, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }


    /**
     * 批量操作资源
     *
     * @param result
     * @return
     */
    @RequiresPermissions(value = {PermissionEnum.Constant.RESOURCE_PERM,PermissionEnum.Constant.DESIGN_PERM}, logical = Logical.OR)
    @RequestMapping(value = "/opt", method = {RequestMethod.POST})
    @ResponseBody
    @MethodValidate
    public BasicResult batchOptResource(
            @NotEmpty(message = ErrorMsgConst.PARAM_NOT_NULL)
            @RequestParam(name = "resIds[]") String[] resourceIds,
            @ContainsInt(value = {1, 3, 4}) @RequestParam(name = "optType") Integer optType,
            ValidResult result) {
        if (result.hasErrors()) {
            return new BasicResult<>(result);
        }
        try {
            List<Long> ids = Arrays.stream(resourceIds).map(Long::valueOf).collect(Collectors.toList());
            ResourceStatusEnum resourceStatusEnum = ResourceStatusEnum.getByCode(optType);
            ResponseDTO<Boolean> responseDTO;
            if (ResourceStatusEnum.STATUS_DELETED.equals(resourceStatusEnum)) {
                responseDTO = resourceService.batchDeleteResource(ids);
            } else {
                responseDTO = resourceService.batchUpdateResourceStatus(ids, resourceStatusEnum);
            }
            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult<>(responseDTO.getStatus());
            }

            return new BasicResult<>(responseDTO.getData());
        } catch (Exception e) {
            logger.error("后台操作资源异常,resIds:{},optType:{}", JSON.toJSONString(resourceIds), optType, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /** ———————————————————————————回收站——————————————————————**/

    /**
     * 查询回收站列表
     *
     * @return
     */
    @RequiresPermissions(value = {
            PermissionEnum.Constant.RESOURCE_REMOVE_PERM})
    @RequestMapping(value = "/recycle/list", method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult batchOptResourceRecycle(
            @RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        try {
            ListResourceRecycleDTO listResourceRecycleDTO = resourceService.getRecycleResourceList(pageNum,size);
            if (!ErrorStatus.OK.equals(listResourceRecycleDTO.getStatus())) {
                return new BasicResult<>(listResourceRecycleDTO.getStatus());
            }
            ListResourceRecycleVO listResourceRecycleVO = new ListResourceRecycleVO();
            buildListResourceRecycleVO(listResourceRecycleVO,listResourceRecycleDTO);
            return new BasicResult<>(listResourceRecycleVO);
        } catch (Exception e) {
            logger.error("后台查询回收站列表资源异常,pageNum:{},size:{}", pageNum, size, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 批量操作回收站资源
     *
     * @param result
     * @return
     */
    @RequiresPermissions(value = {
            PermissionEnum.Constant.RESOURCE_REMOVE_PERM})
    @RequestMapping(value = "/recycle/opt", method = {RequestMethod.POST})
    @ResponseBody
    @MethodValidate
    public BasicResult batchOptResourceRecycle(
            @NotEmpty(message = ErrorMsgConst.PARAM_NOT_NULL)
            @RequestParam(name = "resIds[]") String[] resIds,
            @ContainsInt(value = {1, 4}) @RequestParam(name = "optType") Integer optType,
            ValidResult result) {
        if (result.hasErrors()) {
            return new BasicResult<>(result);
        }
        try {
            List<Long> ids = Arrays.stream(resIds).map(Long::valueOf).collect(Collectors.toList());
            ResourceStatusEnum resourceStatusEnum = ResourceStatusEnum.getByCode(optType);
            ResponseDTO<Boolean> responseDTO = new ResponseDTO<>(ErrorStatus.ERROR);
            if (ResourceStatusEnum.STATUS_DELETED.equals(resourceStatusEnum)) {
                responseDTO = resourceService.batchClearRecycle(ids);
            } else if (ResourceStatusEnum.STATUS_NORMAL.equals(resourceStatusEnum)) {
                responseDTO = resourceService.batchRenewRecycle(ids);
            }
            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult<>(responseDTO.getStatus());
            }

            return new BasicResult<>(responseDTO.getData());
        } catch (Exception e) {
            logger.error("后台批量操作回收站资源异常,resIds:{},optType:{}", JSON.toJSONString(resIds), optType, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }


    private void buildListResourceVO(ListAdminResourceVO resourceVOs, ListResourceDTO resourceDTOs) {
        resourceDTOs.getResList().forEach(resourceDTO -> {
            AdminResourceVO resourceVO = new AdminResourceVO();
            resourceVO.setClassId(resourceDTO.getClassId());
            resourceVO.setClassName(resourceDTO.getClassName());
            resourceVO.setCost(resourceDTO.getCost());
            resourceVO.setDownloadCount(resourceDTO.getDownloadCount());
            resourceVO.setReport(resourceDTO.getReport());
            resourceVO.setResourceId(resourceDTO.getResourceId());
            resourceVO.setTitle(resourceDTO.getTitle());
            resourceVO.setUpdateTime(resourceDTO.getUpdateTime());
            resourceVO.setUserId(resourceDTO.getUserId());
            resourceVO.setUserName(resourceDTO.getUserName());
            resourceVO.setStatus(resourceDTO.getStatus());
            resourceVOs.getResList().add(resourceVO);
        });
        resourceVOs.setTotalCount(resourceDTOs.getTotalCount());
    }

    private void buildListResourceRecycleVO(ListResourceRecycleVO resourceVOs, ListResourceRecycleDTO resourceDTOs) {
        resourceDTOs.getList().forEach(resourceDTO -> {
            ResourceRecycleVO resourceVO = new ResourceRecycleVO();
            resourceVO.setClassName(resourceDTO.getClassName());
            resourceVO.setOptName(resourceDTO.getOptName());
            resourceVO.setResourceTitle(resourceDTO.getResourceTitle());
            resourceVO.setResId(resourceDTO.getResourceId());
            resourceVO.setDate(resourceDTO.getUpdateTime());
            resourceVO.setUserName(resourceDTO.getUserName());
            resourceVOs.getList().add(resourceVO);
        });
        resourceVOs.setTotalCount(resourceDTOs.getTotalCount());
    }
}
