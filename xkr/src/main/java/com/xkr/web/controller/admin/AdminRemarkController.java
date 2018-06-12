package com.xkr.web.controller.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xkr.common.ErrorStatus;
import com.xkr.common.LoginEnum;
import com.xkr.common.annotation.valid.IsNumberic;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.remark.ListRemarkDTO;
import com.xkr.domain.dto.remark.RemarkDetailDTO;
import com.xkr.domain.entity.XkrAdminAccount;
import com.xkr.service.RemarkService;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.remark.ListRemarkVO;
import com.xkr.web.model.vo.remark.RemarkDetailVO;
import com.xkr.web.model.vo.remark.RemarkVO;
import org.apache.shiro.SecurityUtils;
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
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@Controller
@RequestMapping("/api/admin/remark")
public class AdminRemarkController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RemarkService remarkService;

    @RequestMapping(value = "/list", method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult getAllRemarkList(
            @RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        try {

            ListRemarkDTO listRemarkDTO = remarkService.getAllRemarkDTO(pageNum,size);

            if (!ErrorStatus.OK.equals(listRemarkDTO.getStatus())) {
                return new BasicResult(listRemarkDTO.getStatus());
            }

            ListRemarkVO listRemarkVO = new ListRemarkVO();

            buildListRemarkVO(listRemarkVO,listRemarkDTO);

            return new BasicResult<>(listRemarkVO);
        } catch (Exception e) {
            logger.error("RemarkController getAllRemarkList error , pageNum:{},size:{}",pageNum,size, e);
        }
        return new BasicResult(ErrorStatus.ERROR);
    }

    @RequestMapping(value = "/detail", method = {RequestMethod.GET})
    @ResponseBody
    @MethodValidate
    public BasicResult getDetailRemark(
            @IsNumberic
            @RequestParam(name = "remarkId") String  remarkId,
            ValidResult result){
        if(result.hasErrors()){
            return new BasicResult(result);
        }
        try {

            ResponseDTO<RemarkDetailDTO> responseDTO = remarkService.getRemarkDetailById(Long.valueOf(remarkId));

            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult(responseDTO.getStatus());
            }

            RemarkDetailVO remarkDetailVO = new RemarkDetailVO();

            buildRemarkDetailVO(remarkDetailVO,responseDTO.getData());

            return new BasicResult<>(remarkDetailVO);
        } catch (Exception e) {
            logger.error("RemarkController getDetailRemark error , remarkId:{}",remarkId, e);
        }
        return new BasicResult(ErrorStatus.ERROR);
    }

    @RequestMapping(value = "/del", method = {RequestMethod.POST})
    @ResponseBody
    public BasicResult batchDeleteRemarks(
            @RequestParam(name = "remarkIds[]") String[] remarkIds){
        try {

            List<Long> ids = Arrays.stream(remarkIds).map(Long::valueOf).collect(Collectors.toList());

            ResponseDTO<Boolean> responseDTO = remarkService.batchDeleteRemarkByIds(ids);

            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult(responseDTO.getStatus());
            }

            return new BasicResult<>(responseDTO);
        } catch (Exception e) {
            logger.error("RemarkController batchDeleteRemarks error , remarkIds:{}", JSON.toJSONString(remarkIds), e);
        }
        return new BasicResult(ErrorStatus.ERROR);
    }

    @RequestMapping(value = "/reply", method = {RequestMethod.POST})
    @ResponseBody
    @MethodValidate
    public BasicResult replyRemark(
            @IsNumberic
            @RequestParam(name = "remarkId") String  remarkId,
            @NotBlank
            @RequestParam(name = "content") String  content,
            ValidResult result){
        if(result.hasErrors()){
            return new BasicResult(result);
        }
        try {
            XkrAdminAccount adminAccount = (XkrAdminAccount) SecurityUtils.getSubject().getPrincipal();

            ResponseDTO<Long> responseDTO = remarkService.replyRemark(adminAccount.getId(), LoginEnum.ADMIN,content,Long.valueOf(remarkId));

            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult(responseDTO.getStatus());
            }

            JSONObject output = new JSONObject();
            output.put("remarkId",String.valueOf(responseDTO.getData()));

            return new BasicResult<>(output);
        } catch (Exception e) {
            logger.error("RemarkController replyRemark error , remarkId:{},content:{}",remarkId,content, e);
        }
        return new BasicResult(ErrorStatus.ERROR);
    }

    private void buildRemarkDetailVO(RemarkDetailVO remarkDetailVO, RemarkDetailDTO remarkDetailDTO){
        remarkDetailVO.setContent(remarkDetailDTO.getContent());
        remarkDetailVO.setRemarkId(remarkDetailDTO.getRemarkId());
        remarkDetailVO.setSubmitDate(remarkDetailDTO.getSubmitDate());
        if(Objects.nonNull(remarkDetailDTO.getpRemark())){
            remarkDetailVO.setpRemark(new RemarkDetailVO());
            buildRemarkDetailVO(remarkDetailVO.getpRemark(),remarkDetailDTO.getpRemark());
        }
    }

    private void buildListRemarkVO(ListRemarkVO listRemarkVO,ListRemarkDTO listRemarkDTO){
        listRemarkDTO.getList().forEach(remarkDTO -> {
            RemarkVO remarkVO = new RemarkVO();
            remarkVO.setContent(remarkDTO.getContent());
            remarkVO.setPhone(remarkDTO.getPhone());
            remarkVO.setQq(remarkDTO.getQq());
            remarkVO.setRemarkId(remarkDTO.getRemarkId());
            remarkVO.setSubmitDate(remarkDTO.getSubmitDate());
            remarkVO.setUserName(remarkDTO.getUserName());
            listRemarkVO.getList().add(remarkVO);
        });
        listRemarkVO.setTotalCount(listRemarkDTO.getTotalCount());
    }

}
