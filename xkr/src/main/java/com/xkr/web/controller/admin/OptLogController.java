package com.xkr.web.controller.admin;

import com.alibaba.fastjson.JSON;
import com.xkr.common.ErrorStatus;
import com.xkr.common.PermissionEnum;
import com.xkr.common.annotation.valid.IsNumberic;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.clazz.ClassMenuDTO;
import com.xkr.domain.dto.optlog.ListOptLogDTO;
import com.xkr.service.ClassService;
import com.xkr.service.OptLogService;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.clazz.ClassMenuVO;
import com.xkr.web.model.vo.optlog.ListOptLogVO;
import com.xkr.web.model.vo.optlog.OptLogVO;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
 * @date 2018/5/16
 */
@Controller
@RequestMapping("/api/admin/optlog")
public class OptLogController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OptLogService optLogService;

    @RequiresPermissions(value = {PermissionEnum.Constant.LOG_PERM})
    @RequestMapping(value = "/list", method = {RequestMethod.GET})
    @ResponseBody
    @MethodValidate
    public BasicResult getOptLogList(
            @IsNumberic
            @RequestParam(name = "adminAccountId",required = false, defaultValue = "") String adminAccountId,
            @RequestParam(name = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            ValidResult result) {
        if(result.hasErrors()){
            return new BasicResult(result);
        }
        try {
            ListOptLogDTO listOptLogDTO = optLogService.getOptLogList(Long.valueOf(adminAccountId),pageNum,size);

            if (!ErrorStatus.OK.equals(listOptLogDTO.getStatus())) {
                return new BasicResult(ErrorStatus.ERROR);
            }
            ListOptLogVO listOptLogVO = new ListOptLogVO();

            buildListOptLogVO(listOptLogVO,listOptLogDTO);

            return new BasicResult<>(listOptLogVO);
        } catch (Exception e) {
            logger.error("OptLogController getOptLogList error ,adminAccountId:{},pageNum:{},size:{}", adminAccountId,pageNum,size, e);
        }
        return new BasicResult(ErrorStatus.ERROR);
    }

    @RequiresPermissions(value = {PermissionEnum.Constant.LOG_PERM})
    @RequestMapping(value = "/del", method = {RequestMethod.POST})
    @ResponseBody
    public BasicResult batchDelateOptLog(
            @RequestParam(name = "optlogIds[]") String[] optlogIds){
        try {
            List<Long> ids = Arrays.stream(optlogIds).map(Long::valueOf).collect(Collectors.toList());

            ResponseDTO<Boolean> responseDTO = optLogService.batchDelOptLogByIds(ids);

            if (!ErrorStatus.OK.equals(responseDTO.getStatus()) || Boolean.FALSE.equals(responseDTO.getData())) {
                return new BasicResult(ErrorStatus.ERROR);
            }
            return new BasicResult<>(responseDTO.getData());
        } catch (Exception e) {
            logger.error("OptLogController batchDelateOptLog error ,optlogIds:{}",JSON.toJSONString(optlogIds), e);
        }
        return new BasicResult(ErrorStatus.ERROR);
    }

    private void buildListOptLogVO(ListOptLogVO listOptLogVO, ListOptLogDTO listOptLogDTO) {
        listOptLogDTO.getList().forEach(optLogDTO -> {
            OptLogVO optLogVO = new OptLogVO();
            optLogVO.setAccountName(optLogDTO.getAccountName());
            optLogVO.setAdminAccountId(optLogDTO.getAdminAccountId());
            optLogVO.setClientIp(optLogDTO.getClientIp());
            optLogVO.setDate(optLogDTO.getDate());
            optLogVO.setOptlogId(optLogDTO.getOptlogId());
            optLogVO.setOptModule(optLogDTO.getOptModule());
            listOptLogVO.getList().add(optLogVO);
        });
        listOptLogVO.setTotalCount(listOptLogDTO.getTotalCount());
    }

}
