package com.xkr.domain;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.xkr.common.ErrorStatus;
import com.xkr.core.IdGenerator;
import com.xkr.dao.mapper.XkrResourceCommentMapper;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.search.CommentIndexDTO;
import com.xkr.domain.entity.XkrResource;
import com.xkr.domain.entity.XkrResourceComment;
import com.xkr.domain.entity.XkrUser;
import com.xkr.service.api.SearchApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@Service
public class XkrResourceCommentAgent {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XkrResourceCommentMapper xkrResourceCommentMapper;

    @Autowired
    private SearchApiService searchApiService;

    @Autowired
    private XkrResourceAgent xkrResourceAgent;

    @Autowired
    private IdGenerator idGenerator;

    public static final int STATUS_NORMAL = 1;

    public static final int STATUS_TOVERIFY = 2;

    public static final int STATUS_DELETED = 3;


    public List<XkrResourceComment> getCommentsByResourceId(Long resourceId, int status) {
        if (Objects.isNull(resourceId)) {
            logger.error("XkrResourceCommentAgent getCommentsByResourceIds empty resourceId");
            return Lists.newArrayList();
        }
        Map<String, Object> params = ImmutableMap.of(
                "status", status,
                "resourceId", resourceId
        );
        return xkrResourceCommentMapper.getCommentsByResourceId(params);
    }

    public ResponseDTO<XkrResourceComment> saveNewResourceComment(String clientIp, XkrUser user, Long resourceId,
                                        String content) {
        if (Objects.isNull(user) || Objects.isNull(resourceId)) {
            logger.error("XkrResourceUserAgent getResourceByUserId empty userId:{} , resourceId:{}", JSON.toJSONString(user),resourceId);
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        XkrResource xkrResource = xkrResourceAgent.getResourceById(resourceId);
        if(Objects.isNull(xkrResource)){
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        Long id = idGenerator.generateId();
        XkrResourceComment resourceComment = new XkrResourceComment();
        resourceComment.setClientIp(clientIp);
        resourceComment.setContent(content);
        resourceComment.setId(id);
        resourceComment.setParentCommentId(id);
        resourceComment.setRootCommentId(id);
        resourceComment.setResourceId(resourceId);
        resourceComment.setUserId(user.getId());
        resourceComment.setStatus((byte)STATUS_TOVERIFY);

        if(xkrResourceCommentMapper.insert(resourceComment) == 1){
            CommentIndexDTO commentIndexDTO = new CommentIndexDTO();
            buildCommentIndexDTO(commentIndexDTO,user,resourceComment,xkrResource);
            if(!searchApiService.upsertIndex(commentIndexDTO)){
                logger.error("XkrResourceCommentAgent saveNewResourceComment create commentIndexDTO index failed ,commentId:{}",id);
            }
            return new ResponseDTO<>(resourceComment);
        }
        return new ResponseDTO<>(ErrorStatus.ERROR);
    }

    private void buildCommentIndexDTO(CommentIndexDTO commentIndexDTO,XkrUser user,
                                      XkrResourceComment resourceComment,XkrResource resource){
        commentIndexDTO.setClientIp(resourceComment.getClientIp());
        commentIndexDTO.setCommentId(resourceComment.getId());
        commentIndexDTO.setContent(resourceComment.getContent());
        commentIndexDTO.setResourceId(resource.getId());
        commentIndexDTO.setStatus(resourceComment.getStatus());
        commentIndexDTO.setTitle(resource.getTitle());
        commentIndexDTO.setUserId(user.getId());
        commentIndexDTO.setUserName(user.getUserName());
        commentIndexDTO.setUpdateTime(resourceComment.getUpdateTime());
    }

}
