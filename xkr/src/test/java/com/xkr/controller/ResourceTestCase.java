package com.xkr.controller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.xkr.BaseServiceTest;
import com.xkr.common.Const;
import com.xkr.common.ErrorStatus;
import com.xkr.core.aop.CSRFAspect;
import com.xkr.dao.cache.BaseRedisService;
import com.xkr.dao.mapper.XkrResourceCommentMapper;
import com.xkr.domain.dto.comment.CommentStatusEnum;
import com.xkr.util.UuidUtil;
import com.xkr.web.controller.ResourceController;
import com.xkr.web.model.BasicResult;
import org.apache.http.HttpRequest;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.chris.redbud.validator.result.ValidResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2019/6/8
 */
public class ResourceTestCase extends BaseServiceTest{
    private static Logger logger = LoggerFactory.getLogger(ResourceTestCase.class);


    @Autowired
    private BaseRedisService baseRedisService;

    @Autowired
    private CSRFAspect csrfAspect;

    @Before
    public void init(){
    }


    @Test
    public void testResourceDownload() throws IOException {
        BasicResult basicResult = new BasicResult(ErrorStatus.OK);
        genTokenAndSetReturn(basicResult);
        String token = (String) basicResult.getExt().get("token");
        valid(token);

    }

    private void genTokenAndSetReturn(BasicResult basicResult) throws IOException {
        Subject subject = SecurityUtils.getSubject();
        String uuid = UuidUtil.getUUID();
        String csrfKey = Const.CSRF_TOKEN_PREFIX+uuid;
        if(baseRedisService.compareAndSet(csrfKey,"",String.valueOf(subject.getSession().getId()))){
            basicResult.setExt(ImmutableMap.of(
                    Const.CSRF_TOKEN_PARAM,uuid
            ));
        }
    }

    private void valid(String token){
        String csrfKey = Const.CSRF_TOKEN_PREFIX+token;
        String savedSessionId = baseRedisService.get(csrfKey);
        if(Objects.nonNull(savedSessionId) && savedSessionId.equals(String.valueOf(SecurityUtils.getSubject().getSession().getId()))){
            if(baseRedisService.compareAndSet(csrfKey,savedSessionId,"")){
                logger.info("执行成功");
            }
            logger.info("请求频率太快");
        }
        logger.info("token认证异常");
    }
}
