package com.xkr.service;

import com.xkr.domain.XkrDataAnalyzeAgent;
import com.xkr.domain.dto.resource.DataAnalyzeDTO;
import com.xkr.domain.dto.resource.ListDataAnalyzeDTO;
import com.xkr.domain.entity.XkrDataAnalyze;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@Service
public class DataAnalyzeService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XkrDataAnalyzeAgent xkrDataAnalyzeAgent;


    /**
     * 记录数据
     * @param title
     */
    @Async
    public void upsertData(String title) {
        logger.debug("DataAnalyzeService upsertData 记录数据 title:{}",title);
        try {
            xkrDataAnalyzeAgent.upsertData(title);
        } catch (Exception e) {
            logger.error("DataAnalyzeService upsertData 失败 , title:{} ", title, e);
        }
    }

    /**
     * 获取数据统计列表
     * @param startTime
     * @param endTime
     * @return
     */
    public ListDataAnalyzeDTO getByRange(Date startTime, Date endTime) {
        ListDataAnalyzeDTO result = new ListDataAnalyzeDTO();
        if (Objects.isNull(endTime)) {
            endTime = new Date();
        }
        List<XkrDataAnalyze> dataAnalyzes = xkrDataAnalyzeAgent.selectByRange(startTime, endTime);

        if (CollectionUtils.isEmpty(dataAnalyzes)) {
            return result;
        }
        buildListDataAnalyzeDTO(result, dataAnalyzes);

        return result;
    }

    private void buildListDataAnalyzeDTO(ListDataAnalyzeDTO results, List<XkrDataAnalyze> list) {
        list.forEach(xkrDataAnalyze -> {
            DataAnalyzeDTO dataAnalyzeDTO = new DataAnalyzeDTO();
            dataAnalyzeDTO.setId(xkrDataAnalyze.getId());
            dataAnalyzeDTO.setTitle(xkrDataAnalyze.getTitle());
            dataAnalyzeDTO.setCalType(Integer.valueOf(xkrDataAnalyze.getCalType()));
            dataAnalyzeDTO.setCalCount(xkrDataAnalyze.getCalCount());
            dataAnalyzeDTO.setCreateTime(xkrDataAnalyze.getCreateTime());
            dataAnalyzeDTO.setUpdateTime(xkrDataAnalyze.getUpdateTime());
            dataAnalyzeDTO.setExt(xkrDataAnalyze.getExt());
            results.getResList().add(dataAnalyzeDTO);
        });
        results.setTotalCount(list.size());
    }

}
