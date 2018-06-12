package com.xkr.web.controller.test;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.hengyi.dzfilter.utils.TextUtils;
import com.xkr.common.ErrorStatus;
import com.xkr.domain.dto.search.ResourceIndexDTO;
import com.xkr.domain.dto.search.SearchResultListDTO;
import com.xkr.domain.dto.search.UserIndexDTO;
import com.xkr.service.api.SearchApiService;
import com.xkr.web.model.BasicResult;
import main.java.com.upyun.UpException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/6/11
 */
@Controller
@RequestMapping("/test")
public class IndexTestController {

    @Autowired
    private SearchApiService searchApiService;

    @RequestMapping(value = "/saveIndex", method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult index3(Model model) throws URISyntaxException, IOException, UpException {
        UserIndexDTO r1 = new UserIndexDTO();
        r1.setCreateTime(Date.from(LocalDateTime.now().minusDays(1).toInstant(ZoneOffset.UTC)));
        r1.setEmail(TextUtils.filter("为此你妈逼，他也被免去天津国际工程咨询公司党委书记、委员、总经理，天津市政府投资项目评审中心主任职务"));
        r1.setStatus(1);
        r1.setUserId(114124124124L);
        r1.setUserName("　天津市长刮骨疗毒：市侨办原党组书记、原主任被问责与周路一同被问责的，还有天津市政府投资项目评审中心原主任陈黎明。");
        UserIndexDTO r2 = new UserIndexDTO();
        r2.setCreateTime(Date.from(LocalDateTime.now().minusDays(2).toInstant(ZoneOffset.UTC)));
        r2.setEmail(TextUtils.filter("公开资料显示草他妈，周路最后一次公开亮相是上月4日，他带领相关部门负责同志，与河西区区长李学义、副区长刘惠杰及区侨办、区投促办负责同志，同区内侨资企业代表举行座谈"));
        r2.setStatus(0);
        r2.setUserId(412412123L);
        r2.setUserName("　然而在此次的问责通报中提到，周路作为党政主要负责人，领导素养不高，驾驭能力较弱，不善于抓班子带队伍，作风不民主;业务能力不强，工作思路不够清晰，对侨务工作研究得不够深入，破解所属单位遇到的困难招法不多;工作方法简单生硬，做干部思想政治工作不到位，本单位干部队伍思想较乱，群众认可度不高");
        UserIndexDTO r3 = new UserIndexDTO();
        r3.setCreateTime(new Date());
        r3.setEmail("今日出版的《天津日报》在第2版公布了天津市委组织部对5名市管干部的问责通报。排在第一位的，是天津市人民政府侨务办公室原党组书记、原主任周路");
        r3.setStatus(0);
        r3.setUserId(5125121L);
        r3.setUserName("天津市长“刮骨疗毒”,厅官就地免职) “对问题的严肃处理,表明市委向怠政懒政、只会说‘不行’的部门和领导干部开刀问斩的坚定决心和鲜明");
        UserIndexDTO r4 = new UserIndexDTO();
        r4.setCreateTime(Date.from(LocalDateTime.now().minusDays(4).toInstant(ZoneOffset.UTC)));
        r4.setEmail("其中包括天津市科学技术委员会体制改革处(安全保卫处)处长王育忠因执行市委文件要求打折扣、存在违规收取礼金问题等一系列问题而受到党内严重警告处分");
        r4.setStatus(1);
        r4.setUserId(512412412L);
        r4.setUserName("近段时间来，天津市连续通报了多起类似情况，在全市范围深入开展不作为不担当问题专项治理三年行动中祭出重拳。");
        searchApiService.upsertIndex(r1);
        searchApiService.upsertIndex(r2);
        searchApiService.upsertIndex(r3);
        searchApiService.upsertIndex(r4);
        return new BasicResult(ErrorStatus.OK);
    }


    @RequestMapping(value = "/searchIndex", method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult index3(String keyword) throws URISyntaxException, IOException, UpException {
        SearchResultListDTO hits = searchApiService.searchByKeyWordInField(UserIndexDTO.class,
                keyword, ImmutableMap.of("userName",1F,"email",1F),null,new ImmutablePair<>(null,new Date()),"createTime",null,
                ImmutableSet.of("userName","email","userId"),0,10);
        ImmutableMap result = ImmutableMap.of("searchHit", hits);
        return new BasicResult<>(result);
    }

    @RequestMapping(value = "/bulkUpdateIndex", method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult<Boolean> TestBulkUpdateIndex(String email) throws URISyntaxException, IOException, UpException {
        return new BasicResult<>(searchApiService.bulkUpdateIndex("user", ImmutableList.of(5125121L, 412412123L),
                ImmutableMap.of(
                        "email", email
                )));
    }

    @RequestMapping(value = "/bulkUpdateIndexStatus", method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult<Boolean> TestBulkUpdateIndexStatus(Integer status) throws URISyntaxException, IOException, UpException {
        return new BasicResult<>(searchApiService.bulkUpdateIndexStatus("user", ImmutableList.of(5125121L, 412412123L),status));
    }

    @RequestMapping(value = "/getAndBuildIndexDTOByIndexId", method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult<String> TestGetAndBuildIndexDTOByIndexId(Integer docId) throws URISyntaxException, IOException, UpException {
        UserIndexDTO userIndexDTO = new UserIndexDTO();
        searchApiService.getAndBuildIndexDTOByIndexId(userIndexDTO,String.valueOf(docId));
        return new BasicResult<>(JSON.toJSONString(userIndexDTO));
    }

    @RequestMapping(value = "/searchByFilterField", method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult<String> TestSearchByFilterField(Integer status,Integer offset,Integer size) throws URISyntaxException, IOException, UpException {
        SearchResultListDTO searchResultListDTO = searchApiService.searchByFilterField(UserIndexDTO.class,ImmutableMap.of("status",status),null,null,null,offset,size);
        return new BasicResult<>(JSON.toJSONString(searchResultListDTO));
    }

}