package com.xkr.web.controller.admin;

import com.xkr.common.ErrorStatus;
import com.xkr.common.PermissionEnum;
import com.xkr.domain.dto.resource.ListDataAnalyzeDTO;
import com.xkr.service.DataAnalyzeService;
import com.xkr.util.DateUtil;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.resource.DataAnalyzeVO;
import com.xkr.web.model.vo.resource.ListDataAnalyzeVO;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/29
 */
@RequestMapping("/api/admin/data")
@Controller
public class AdminDataAnyController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private DataAnalyzeService dataAnalyzeService;

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

    /**
     * 数据分析权限验证
     *
     * @return
     */
    @RequestMapping(value = "/list", method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult dataAnaList(@RequestParam(name = "startDate", required = false) String startDate,
                                   @RequestParam(name = "endDate", required = false) String endDate) {
        try {

            ListDataAnalyzeVO listDataAnalyzeVO = new ListDataAnalyzeVO();

            Date start = null;
            if (!StringUtils.isEmpty(startDate)) {
                start = DateUtil.yyyyMMdd.parse(startDate);
            }
            Date end = null;
            if (!StringUtils.isEmpty(endDate)) {
                end = DateUtil.yyyyMMdd.parse(endDate);
            }
            ListDataAnalyzeDTO listDataAnalyzeDTO = dataAnalyzeService.getByRange(start, end);
            buildListDataAnalyzeVO(listDataAnalyzeVO,listDataAnalyzeDTO);

            return new BasicResult<>(listDataAnalyzeVO);
        } catch (Exception e) {
            logger.error("数据分析列表失败", e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    private void buildListDataAnalyzeVO(ListDataAnalyzeVO results, ListDataAnalyzeDTO list) {
        list.getResList().forEach(xkrDataAnalyze -> {
            DataAnalyzeVO dataAnalyzeDTO = new DataAnalyzeVO();
            dataAnalyzeDTO.setId(xkrDataAnalyze.getId());
            dataAnalyzeDTO.setTitle(xkrDataAnalyze.getTitle());
            dataAnalyzeDTO.setCalCount(xkrDataAnalyze.getCalCount());
            results.getList().add(dataAnalyzeDTO);
        });
        results.setTotalCount(list.getTotalCount());
    }
}
