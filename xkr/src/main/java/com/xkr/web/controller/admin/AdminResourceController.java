package com.xkr.web.controller.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.xkr.common.ErrorStatus;
import com.xkr.common.annotation.CSRFGen;
import com.xkr.common.annotation.CSRFValid;
import com.xkr.common.annotation.valid.Captcha;
import com.xkr.common.annotation.valid.IsNumberic;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.file.FileDownloadResponseDTO;
import com.xkr.domain.dto.resource.*;
import com.xkr.domain.entity.XkrUser;
import com.xkr.service.ResourceService;
import com.xkr.util.DateUtil;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.resource.*;
import org.apache.commons.lang3.StringUtils;
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
     * @return
     */
    @RequestMapping(value = "/search_list", method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult<ListResourceVO> getResourceListBySearchWord(
            @RequestParam(name = "startDate",required = false,defaultValue = "") String startDate,
            @RequestParam(name = "keyWord",required = false,defaultValue = "") String keyWord,
            @RequestParam(name = "type",required = false,defaultValue = "0") Integer type,
            @RequestParam(name = "status",required = false,defaultValue = "1") Integer status,
            @RequestParam(name = "report",required = false,defaultValue = "2") Integer report,
            @RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size){

        ListResourceVO resourceVOs = new ListResourceVO();
        try {
            Date sDate = Date.from(LocalDateTime.now().minusWeeks(1).toInstant(ZoneOffset.UTC));
            if(StringUtils.isNotEmpty(startDate)){
                sDate = DateUtil.stringToDate(startDate,"yyyy-MM-dd");
            }
            if(type == 0){
                //资源类型为0表示获取全部资源,搜索是不进行过滤
                type = null;
            }
            if(report == 2){
                //举报默认是2全部,搜索不进行过滤
                report = null;
            }

            ListResourceDTO resourceDTOs = resourceService.getResourceSearchByAdmin(keyWord,sDate,
                    null,type, ResourceStatusEnum.getByCode(status),report,pageNum,size);

            if(!ErrorStatus.OK.equals(resourceDTOs.getStatus())){
                return new BasicResult<>(resourceDTOs.getStatus());
            }

            buildListResourceVO(resourceVOs, resourceDTOs);

            return new BasicResult<>(resourceVOs);
        } catch (Exception e) {
            logger.error("后台获取搜索获取资源异常,startDate:{},keyWord:{},type:{},status:{},report:{},pageNum:{},size:{}", startDate,keyWord,type,status,report,pageNum,size, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }


    /**
     * 批量操作资源
     * @param result
     * @return
     */
    @RequestMapping(value = "/opt", method = {RequestMethod.POST})
    @ResponseBody
    @MethodValidate
    public BasicResult batchOptResource(
            @RequestParam(name = "resIds[]") String[] resourceIds,
            @ContainsInt(value = {1,3,4,-1}) @RequestParam(name = "optType") Integer optType,
            ValidResult result) {
        if(result.hasErrors()){
            return new BasicResult<>(result);
        }
        try {
            List<Long> ids = Arrays.stream(resourceIds).map(Long::valueOf).collect(Collectors.toList());

            ResponseDTO<Boolean> responseDTO = resourceService.batchUpdateResourceStatus(ids, ResourceStatusEnum.getByCode(optType));

            if(!ErrorStatus.OK.equals(responseDTO.getStatus())){
                return new BasicResult<>(responseDTO.getStatus());
            }

            return new BasicResult<>(responseDTO.getData());
        } catch (Exception e) {
            logger.error("后台操作资源异常,resIds:{},optType:{}", JSON.toJSONString(resourceIds),optType, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
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
