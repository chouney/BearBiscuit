package com.xkr.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.xkr.common.CaptchaEnum;
import com.xkr.common.ErrorStatus;
import com.xkr.common.annotation.CSRFGen;
import com.xkr.common.annotation.CSRFValid;
import com.xkr.common.annotation.valid.Captcha;
import com.xkr.common.annotation.valid.IsNumberic;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.file.FileDownloadResponseDTO;
import com.xkr.domain.dto.resource.ListResourceDTO;
import com.xkr.domain.dto.resource.ListResourceFolderDTO;
import com.xkr.domain.dto.resource.ResourceDetailDTO;
import com.xkr.domain.dto.resource.ResourceFolderDTO;
import com.xkr.domain.entity.XkrUser;
import com.xkr.service.ResourceService;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.resource.*;
import org.apache.shiro.SecurityUtils;
import org.chris.redbud.validator.annotation.MethodValidate;
import org.chris.redbud.validator.result.ValidResult;
import org.chris.redbud.validator.validate.annotation.ContainsInt;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotNull;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/27
 */
@Controller
@RequestMapping("/api/res")
public class ResourceController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ResourceService resourceService;

    /**
     * 根据分类获取资源
     *
     * @param classId
     * @param orderType
     * @param pageNum
     * @param size
     * @param result
     * @return
     */
    @RequestMapping(value = "/cls/list", method = {RequestMethod.GET})
    @ResponseBody
    @MethodValidate
    public BasicResult<ListResourceVO> getResourceListByClass(
            @IsNumberic
            @RequestParam(name = "classId") String classId,
            @ContainsInt({1, 2})
            @RequestParam(name = "orderType", required = false, defaultValue = "1") Integer orderType,
            @RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
            ValidResult result) {
        if (result.hasErrors()) {
            return new BasicResult<>(result);
        }
        ListResourceVO resourceVOs = new ListResourceVO();
        try {
            ListResourceDTO resourceDTOs = resourceService.getResourcesByClassId(Long.valueOf(classId), orderType, pageNum, size);

            if(!ErrorStatus.OK.equals(resourceDTOs.getStatus())){
                return new BasicResult<>(resourceDTOs.getStatus());
            }

            buildListResourceVO(resourceVOs, resourceDTOs);

            return new BasicResult<>(resourceVOs);
        } catch (Exception e) {
            logger.error("获取资源分类获取资源异常,classId:{},orderType:{},pageNum:{},size:{}", classId, orderType, pageNum, size, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 根据用户获取资源
     *
     * @param userId
     * @param type    1上传2下载
     * @param pageNum
     * @param size
     * @param result
     * @return
     */
    @RequestMapping(value = "/user_relation/list", method = {RequestMethod.GET})
    @ResponseBody
    @MethodValidate
    public BasicResult<ListResourceVO> getResourceListByUser(
            @IsNumberic
            @RequestParam(name = "userId") String userId,
            @ContainsInt({1, 2})
            @RequestParam(name = "type") Integer type,
            @RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
            ValidResult result) {
        if (result.hasErrors()) {
            return new BasicResult<>(result);
        }
        ListResourceVO resourceVOs = new ListResourceVO();
        try {
            ListResourceDTO resourceDTOs = resourceService.getResourcesByUser(Long.valueOf(userId), type, pageNum, size);

            if(!ErrorStatus.OK.equals(resourceDTOs.getStatus())){
                return new BasicResult<>(resourceDTOs.getStatus());
            }

            buildListResourceVO(resourceVOs, resourceDTOs);

            return new BasicResult<>(resourceVOs);
        } catch (Exception e) {
            logger.error("获取用户获取资源异常,userId:{},type:{},pageNum:{},size:{}", userId, type, pageNum, size, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 资源搜索
     * @param keyWord
     * @param orderType
     * @param pageNum
     * @param size
     * @param result
     * @return
     */
    @RequestMapping(value = "/search_list", method = {RequestMethod.GET})
    @ResponseBody
    @MethodValidate
    public BasicResult<ListResourceVO> getResourceListBySearchWord(
            @NotBlank
            @RequestParam(name = "keyWord") String keyWord,
            @ContainsInt({1, 2})
            @RequestParam(name = "orderType",required = false,defaultValue = "1") Integer orderType,
            @RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
            ValidResult result) {
        if(result.hasErrors()){
            return new BasicResult<>(result);
        }
        ListResourceVO resourceVOs = new ListResourceVO();
        try {
            ListResourceDTO resourceDTOs = resourceService.getResourceBySearchKeyword(keyWord,orderType,pageNum,size);

            if(!ErrorStatus.OK.equals(resourceDTOs.getStatus())){
                return new BasicResult<>(resourceDTOs.getStatus());
            }

            buildListResourceVO(resourceVOs, resourceDTOs);

            return new BasicResult<>(resourceVOs);
        } catch (Exception e) {
            logger.error("获取搜索获取资源异常,keyWord:{},orderType:{},pageNum:{},size:{}", keyWord, orderType, pageNum, size, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 资源详情
     * @param resourceId
     * @param result
     * @return
     */
    @CSRFGen
    @RequestMapping(value = "/detail", method = {RequestMethod.GET})
    @ResponseBody
    @MethodValidate
    public BasicResult<ResourceDetailVO> getResourceDetail(
            @IsNumberic
            @RequestParam(name = "resId") String resourceId,
            ValidResult result) {
        if(result.hasErrors()){
            return new BasicResult<>(result);
        }
        ResourceDetailVO resourceVOs = new ResourceDetailVO();
        try {
            ResourceDetailDTO detailDTO = resourceService.getResourceDetailById(Long.valueOf(resourceId));

            if(!ErrorStatus.OK.equals(detailDTO.getStatus())){
                return new BasicResult<>(detailDTO.getStatus());
            }

            buildResourceDetailVO(detailDTO, resourceVOs);

            return new BasicResult<>(resourceVOs);
        } catch (Exception e) {
            logger.error("获取详情资源异常,resId:{}", resourceId, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }


    /**
     * 资源目录
     * @param resourceId
     * @param result
     * @return
     */
    @RequestMapping(value = "/res_list", method = {RequestMethod.GET})
    @ResponseBody
    @MethodValidate
    public BasicResult<ListResourceFolderVO> getResourceMenuList(
            @IsNumberic
            @RequestParam(name = "resId") String resourceId,
            ValidResult result) {
        if(result.hasErrors()){
            return new BasicResult<>(result);
        }
        ListResourceFolderVO listResourceFolderVO = new ListResourceFolderVO();
        try {
            ListResourceFolderDTO listResourceFolderDTO = resourceService.getResourceMenuList(Long.valueOf(resourceId));

            if(!ErrorStatus.OK.equals(listResourceFolderDTO.getStatus())){
                return new BasicResult<>(listResourceFolderDTO.getStatus());
            }

            buildListResourceFolderVO(listResourceFolderVO, listResourceFolderDTO);

            return new BasicResult<>(listResourceFolderVO);
        } catch (Exception e) {
            logger.error("获取资源目录异常,resId:{}", resourceId, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 资源上传
     * @param resTitle
     * @param resCost
     * @param classId
     * @param detail
     * @param compressMd5
     * @param captcha
     * @param result
     * @return
     */
    @RequestMapping(value = "/res_upload", method = {RequestMethod.POST})
    @ResponseBody
    @MethodValidate
    public BasicResult<JSONObject> resourceUpload(
            @NotBlank
            @RequestParam(name = "resTitle") String resTitle,
            @NotNull
            @RequestParam(name = "resCost") Integer resCost,
            @IsNumberic
            @RequestParam(name = "classId") String classId,
            @NotBlank
            @RequestParam(name = "detail") String detail,
            @NotBlank
            @RequestParam(name = "compressMd5") String compressMd5,
            @NotBlank
            @RequestParam(name = "fileName") String fileName,
            @Captcha(CaptchaEnum.UPLOAD_RES_TYPE)
            @RequestParam(name = "captcha") String captcha,
            ValidResult result) {
        if(result.hasErrors()){
            return new BasicResult<>(result);
        }
        JSONObject output = new JSONObject();
        try {
            XkrUser user = (XkrUser)SecurityUtils.getSubject().getPrincipal();

            ResponseDTO<Long> resId = resourceService.saveNewResource(resTitle,detail,resCost,Long.valueOf(classId),user.getId(),compressMd5,fileName);

            if(!ErrorStatus.OK.equals(resId.getStatus())){
                return new BasicResult<>(resId.getStatus());
            }

            output.put("resId",String.valueOf(resId.getData()));

            return new BasicResult<>(output);
        } catch (Exception e) {
            logger.error("资源上传异常,resTitle:{},resCost:{},detail:{},classId:{},compressMd5:{}", resTitle,resCost,detail,classId,compressMd5, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 文件下载
     * @param resourceId
     * @param result
     * @return
     */
    @CSRFValid
    @RequestMapping(value = "/res_download", method = {RequestMethod.POST})
    @ResponseBody
    @MethodValidate
    public BasicResult<JSONObject> resourceDownLoad(
            @IsNumberic
            @RequestParam(name = "resId") String resourceId,
            ValidResult result) {
        if(result.hasErrors()){
            return new BasicResult<>(result);
        }
        JSONObject output = new JSONObject();
        try {
            XkrUser user = (XkrUser)SecurityUtils.getSubject().getPrincipal();

            FileDownloadResponseDTO responseDTO = resourceService.downloadResource(user,Long.valueOf(resourceId));

            if(!ErrorStatus.OK.equals(responseDTO.getStatus())){
                return new BasicResult<>(responseDTO.getStatus());
            }

            output.put("token",responseDTO.getToken());
            output.put("downloadUrl",responseDTO.getDownloadUrl());
            output.put("date",responseDTO.getDate());

            return new BasicResult<>(output);
        } catch (Exception e) {
            logger.error("下载文件异常,resId:{}", resourceId, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 资源举报
     * @param resourceId
     * @param result
     * @return
     */
    @RequestMapping(value = "/report", method = {RequestMethod.POST})
    @ResponseBody
    @MethodValidate
    public BasicResult<JSONObject> reportResource(
            @IsNumberic
            @RequestParam(name = "resId") String resourceId,
            ValidResult result) {
        if(result.hasErrors()){
            return new BasicResult<>(result);
        }
        try {
            Boolean isSuccess = resourceService.reportResource(Long.valueOf(resourceId));
            if(isSuccess){
                return new BasicResult<>(ErrorStatus.OK);
            }
        } catch (Exception e) {
            logger.error("获取资源目录异常,resId:{}", resourceId, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    private void buildListResourceFolderVO(ListResourceFolderVO listResourceFolderVO, ListResourceFolderDTO listResourceFolderDTO){
        listResourceFolderDTO.getList().forEach(resourceFolderDTO -> {
            ResourceFolderVO resourceFolderVO = new ResourceFolderVO();
            buildResourceFolderVO(resourceFolderVO,resourceFolderDTO);
            listResourceFolderVO.getList().add(resourceFolderVO);
        });
    }

    private void buildResourceFolderVO(ResourceFolderVO resourceFolderVO, ResourceFolderDTO resourceFolderDTO){
        resourceFolderVO.setName(resourceFolderDTO.getName());
        if("d".equals(resourceFolderDTO.getFileType())){
            resourceFolderVO.setFileType("d");
            resourceFolderVO.setSubFolders(Lists.newArrayList());
            resourceFolderDTO.getSubFolders().forEach(resourceFolderDTO1 -> {
                ResourceFolderVO subVO = new ResourceFolderVO();
                buildResourceFolderVO(subVO,resourceFolderDTO1);
                resourceFolderVO.getSubFolders().add(subVO);
            });
        }else{
            resourceFolderVO.setFileType("-");
        }
    }

    private void buildResourceDetailVO(ResourceDetailDTO resourceDetailDTO, ResourceDetailVO resourceDetailVO){
        resourceDetailVO.setCost(resourceDetailDTO.getCost());
        resourceDetailVO.setDetail(resourceDetailDTO.getDetail());
        resourceDetailVO.setDownloadCount(resourceDetailDTO.getDownloadCount());
        resourceDetailVO.setFileSize(resourceDetailDTO.getFileSize());
        resourceDetailVO.setpClass(resourceDetailDTO.getpClass());
        resourceDetailVO.setTitle(resourceDetailDTO.getTitle());
        resourceDetailVO.setUpdateTime(resourceDetailDTO.getUpdateTime());
        resourceDetailVO.setUserId(resourceDetailDTO.getUserId());
        resourceDetailVO.setUserName(resourceDetailDTO.getUserName());
    }

    private void buildListResourceVO(ListResourceVO resourceVOs, ListResourceDTO resourceDTOs) {
        resourceDTOs.getResList().forEach(resourceDTO -> {
            ResourceVO resourceVO = new ResourceVO();
            resourceVO.setClassId(resourceDTO.getClassId());
            resourceVO.setClassName(resourceDTO.getClassName());
            resourceVO.setContent(resourceDTO.getContent());
            resourceVO.setCost(resourceDTO.getCost());
            resourceVO.setDownloadCount(resourceDTO.getDownloadCount());
            resourceVO.setReport(resourceDTO.getReport());
            resourceVO.setResourceId(resourceDTO.getResourceId());
            resourceVO.setRootClassId(resourceDTO.getRootClassId());
            resourceVO.setRootClassName(resourceDTO.getRootClassName());
            resourceVO.setTitle(resourceDTO.getTitle());
            resourceVO.setUpdateTime(resourceDTO.getUpdateTime());
            resourceVO.setUserId(resourceDTO.getUserId());
            resourceVO.setUserName(resourceDTO.getUserName());
            resourceVOs.getResList().add(resourceVO);
        });
        resourceVOs.setTotalCount(resourceDTOs.getTotalCount());
    }
}
