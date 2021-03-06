package com.xkr.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.xkr.common.CaptchaEnum;
import com.xkr.common.ErrorStatus;
import com.xkr.common.annotation.CSRFGen;
import com.xkr.common.annotation.CSRFValid;
import com.xkr.common.annotation.valid.Captcha;
import com.xkr.common.annotation.valid.IsNumberic;
import com.xkr.common.annotation.valid.UserCheck;
import com.xkr.domain.XkrClassAgent;
import com.xkr.domain.XkrResourceAgent;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.file.FileDownloadResponseDTO;
import com.xkr.domain.dto.file.FileUploadStatusDTO;
import com.xkr.domain.dto.resource.ListResourceDTO;
import com.xkr.domain.dto.resource.ListResourceFolderDTO;
import com.xkr.domain.dto.resource.ResourceDetailDTO;
import com.xkr.domain.dto.resource.ResourceFolderDTO;
import com.xkr.domain.entity.XkrClass;
import com.xkr.domain.entity.XkrResource;
import com.xkr.domain.entity.XkrUser;
import com.xkr.service.ClassService;
import com.xkr.service.DataAnalyzeService;
import com.xkr.service.ResourceService;
import com.xkr.service.api.UpLoadApiService;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.resource.*;
import org.apache.shiro.SecurityUtils;
import org.chris.redbud.validator.annotation.MethodValidate;
import org.chris.redbud.validator.result.ValidError;
import org.chris.redbud.validator.result.ValidResult;
import org.chris.redbud.validator.validate.annotation.ContainsInt;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Objects;

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
    private XkrResourceAgent xkrResourceAgent;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private DataAnalyzeService analyzeService;

    @Autowired
    private XkrClassAgent xkrClassAgent;

    @Autowired
    private UpLoadApiService upLoadApiService;


    @RequestMapping(value = "/res_count", method = {RequestMethod.POST})
    @ResponseBody
    public BasicResult<JSONObject> resourceUpload(){
        try{
            int count = xkrResourceAgent.getResourceTotalCache();
            JSONObject output = new JSONObject();
            output.put("count", count);
            return new BasicResult<>(output);
        } catch (Exception e) {
            logger.info("获取数据异常");
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

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

            buildListResourceVO4User(resourceVOs, resourceDTOs);

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

            //数据统计
            analyzeService.upsertData(keyWord);

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
            XkrResource xkrResource = xkrResourceAgent.getResourceById(Long.valueOf(resourceId));
            if(Objects.isNull(xkrResource)){
                return new BasicResult<>(ErrorStatus.RESOURCE_NOT_FOUND);
            }
            String ext = xkrResource.getExt();
            JSONObject extJson = JSON.parseObject(ext);
            ListResourceFolderDTO listResourceFolderDTO;
            if(extJson == null ||
                    (listResourceFolderDTO = extJson.getObject(ResourceService.EXT_FILE_MENU_KEY,ListResourceFolderDTO.class))  == null){
                logger.debug("=========获取资源解压缩目录失败，尝试重新获取");
                listResourceFolderDTO = resourceService.saveNewFileMenu(resourceId);
            }

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
//     * @param captcha
     * @param result
     * @return
     */
//    @CSRFValid
    @RequestMapping(value = "/res_upload", method = {RequestMethod.POST})
    @ResponseBody
    @MethodValidate
    @UserCheck
    public BasicResult<JSONObject> resourceUpload(
            @NotBlank
            @Length(max = 50,message = "标题长度大于25")
            @RequestParam(name = "resTitle") String resTitle,
            @NotNull
            @RequestParam(name = "resCost") Integer resCost,
            @IsNumberic
            @RequestParam(name = "classId") String classId,
            @NotBlank
//            @Length(max = 11000,message = "内容长度不能大于10000字")
            @RequestParam(name = "detail") String detail,
            @NotBlank
            @RequestParam(name = "cp") String cfu,
            @NotBlank
            @RequestParam(name = "up",required = false) String ufu,
//            @Captcha(CaptchaEnum.UPLOAD_RES_TYPE)
//            @RequestParam(name = "captcha") String captcha,
            ValidResult result) {
        if(result.hasErrors()){
            return new BasicResult<>(result);
        }
        JSONObject output = new JSONObject();
        try {

            XkrClass xkrClass = xkrClassAgent.getClassById(Long.valueOf(classId));
            if(Objects.isNull(xkrClass)){
                result = new ValidResult();
                result.getErrors().add(new ValidError("","分类参数异常",classId));
                return new BasicResult<>(result);
            }
            //todo 资源根据分类进行内容长度限制

            XkrUser user = (XkrUser)SecurityUtils.getSubject().getPrincipal();

            ResponseDTO<Long> resId = resourceService.saveNewResource(resTitle,detail,resCost,Long.valueOf(classId),user.getId(),cfu,ufu);

            if(!ErrorStatus.OK.equals(resId.getStatus())){
                return new BasicResult<>(resId.getStatus());
            }

            //直接解压缩文件
            resourceService.unCompressFile(String.valueOf(resId.getData()),cfu);


            output.put("resId",String.valueOf(resId.getData()));

            return new BasicResult<>(output);
        } catch (Exception e) {
            logger.error("资源上传异常,resTitle:{},resCost:{},detail:{},classId:{},cfu:{}", resTitle,resCost,detail,classId,cfu, e);
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
    @UserCheck
    public BasicResult<JSONObject> resourceDownLoad(
            @IsNumberic
            @RequestParam(name = "resId") String resourceId,
            HttpServletRequest request,
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
    @UserCheck
    public BasicResult<JSONObject> reportResource(
            @IsNumberic
            @RequestParam(name = "resId") String resourceId,
            ValidResult result) {
        if(result.hasErrors()){
            return new BasicResult<>(result);
        }
        try {

            ResponseDTO responseDTO = resourceService.reportResource(Long.valueOf(resourceId));
            if(ErrorStatus.OK.equals(responseDTO.getStatus())){
                return new BasicResult<>(ErrorStatus.OK);
            }
            return new BasicResult<>(responseDTO.getStatus());
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
        resourceFolderVO.setDate(resourceFolderDTO.getDate());
        resourceFolderVO.setSize(resourceFolderDTO.getSize());
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
        resourceDetailVO.setStatus(resourceDetailDTO.getResStatus());
    }

    private void buildListResourceVO(ListResourceVO resourceVOs, ListResourceDTO resourceDTOs) {
        resourceDTOs.getResList().forEach(resourceDTO -> {
            ResourceVO resourceVO = new ResourceVO();
            resourceVO.setClassId(resourceDTO.getClassId());
            resourceVO.setClassName(resourceDTO.getClassName());

            //返回内容精简
            if(!StringUtils.isEmpty(resourceDTO.getContent())){
                int length =resourceDTO.getContent().length() > 300 ? 300 : resourceDTO.getContent().length();
                String subStr = resourceDTO.getContent().substring(0,length);
                String tagStr = new String("</em>");
                length = subStr.lastIndexOf(tagStr) + 5;
                resourceVO.setContent(subStr.substring(0,length));
            }

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
            resourceVO.setStatus(resourceDTO.getStatus());
            resourceVOs.getResList().add(resourceVO);
        });
        resourceVOs.setTotalCount(resourceDTOs.getTotalCount());
    }

    private void buildListResourceVO4User(ListResourceVO resourceVOs, ListResourceDTO resourceDTOs) {
        resourceDTOs.getResList().forEach(resourceDTO -> {
            ResourceVO resourceVO = new ResourceVO();
            resourceVO.setClassId(resourceDTO.getClassId());
            resourceVO.setClassName(resourceDTO.getClassName());
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
            resourceVO.setStatus(resourceDTO.getStatus());
            resourceVOs.getResList().add(resourceVO);
        });
        resourceVOs.setTotalCount(resourceDTOs.getTotalCount());
    }
}
