package com.xkr.web.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.xkr.common.ErrorStatus;
import com.xkr.common.annotation.valid.IsNumberic;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.backup.ListBackUpDTO;
import com.xkr.domain.entity.XkrAdminAccount;
import com.xkr.service.BackUpService;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.backup.BackUpVO;
import com.xkr.web.model.vo.backup.ListBackUpVO;
import org.apache.shiro.SecurityUtils;
import org.chris.redbud.validator.annotation.HttpValidate;
import org.chris.redbud.validator.result.ValidResult;
import org.chris.redbud.validator.validate.annotation.ContainsInt;
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
@RequestMapping("/api/admin/db")
@Controller
public class DbBackUpController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private BackUpService backUpService;

    /**
     * 获取备份列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult list() {
        try {

            ListBackUpDTO listBackUpDTO = backUpService.getBackUpList();
            if (!ErrorStatus.OK.equals(listBackUpDTO.getStatus())) {
                return new BasicResult(listBackUpDTO.getStatus());
            }

            ListBackUpVO listBackUpVO = new ListBackUpVO();

            buildListBackUpVO(listBackUpVO,listBackUpDTO);

            return new BasicResult<>(listBackUpVO);
        } catch (Exception e) {
            logger.error("获取db备份列表异常", e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 获取自动备份详情
     *
     * @return
     */
    @RequestMapping(value = "/auto_detail", method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult autoDetail() {
        try {

            ResponseDTO<Integer> responseDTO = backUpService.getCrontabType();
            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult(responseDTO.getStatus());
            }

            JSONObject output = new JSONObject();
            output.put("periodType", responseDTO.getData());

            return new BasicResult<>(output);
        } catch (Exception e) {
            logger.error("获取db自动备份详情异常", e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 设置自动备份
     *
     * @return
     */
    @RequestMapping(value = "/auto_opt", method = {RequestMethod.POST})
    @ResponseBody
    @HttpValidate
    public BasicResult autoOpt(
            @ContainsInt({1, 2, 3, -1})
            @RequestParam(name = "type") Integer type,
            ValidResult result) {
        if(result.hasErrors()){
            return new BasicResult(result);
        }
        try {

            ResponseDTO<Boolean> responseDTO = backUpService.autoCrontab(type);
            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult(responseDTO.getStatus());
            }

            return new BasicResult<>(responseDTO.getData());
        } catch (Exception e) {
            logger.error("db自动备份异常", e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 批量删除备份
     * @param backUpIds
     * @return
     */
    @RequestMapping(value = "/del", method = {RequestMethod.POST})
    @ResponseBody
    public BasicResult batchDelDb(
            @RequestParam(name = "backUpIds[]") String[] backUpIds) {
        try {
            List<Long> ids = Arrays.stream(backUpIds).map(Long::valueOf).collect(Collectors.toList());
            ResponseDTO<Boolean> responseDTO = backUpService.batchDelBackUp(ids);

            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult(responseDTO.getStatus());
            }

            return new BasicResult<>(responseDTO.getData());
        } catch (Exception e) {
            logger.error("批量删除备份异常", e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 备份恢复
     * @param backUpId
     * @return
     */
    @RequestMapping(value = "/restore", method = {RequestMethod.POST})
    @ResponseBody
    @HttpValidate
    public BasicResult restoreDB(
            @IsNumberic
            @RequestParam(name = "backUpId") String backUpId,
            ValidResult result) {
        if(result.hasErrors()){
            return new BasicResult(result);
        }
        try {
            ResponseDTO<Boolean> responseDTO = backUpService.restore(Long.valueOf(backUpId));

            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult(responseDTO.getStatus());
            }

            return new BasicResult<>(responseDTO.getData());
        } catch (Exception e) {
            logger.error("恢复备份异常", e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    @RequestMapping(value = "/opt", method = {RequestMethod.POST})
    @ResponseBody
    public BasicResult backup() {
        try {

            XkrAdminAccount xkrAdminAccount = (XkrAdminAccount) SecurityUtils.getSubject().getPrincipal();

            ResponseDTO<Long> responseDTO = backUpService.backup(xkrAdminAccount.getId());

            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult(responseDTO.getStatus());
            }

            JSONObject output = new JSONObject();
            output.put("backUpId",String.valueOf(responseDTO.getData()));

            return new BasicResult<>(output);
        } catch (Exception e) {
            logger.error("手动备份异常", e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    private void buildListBackUpVO(ListBackUpVO listBackUpVO,ListBackUpDTO listBackUpDTO){
        listBackUpDTO.getList().forEach(backUpDTO -> {
            BackUpVO  backUpVO = new BackUpVO();
            backUpVO.setAccountName(backUpDTO.getAccountName());
            backUpVO.setBackUpId(backUpDTO.getBackUpId());
            backUpVO.setBackUpName(backUpDTO.getBackUpName());
            backUpVO.setDate(backUpDTO.getDate());
            listBackUpVO.getList().add(backUpVO);
        });
        listBackUpVO.setTotalCount(listBackUpDTO.getTotalCount());
    }

}
