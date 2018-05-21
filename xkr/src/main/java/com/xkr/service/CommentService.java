package com.xkr.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xkr.common.ErrorStatus;
import com.xkr.domain.XkrClassAgent;
import com.xkr.domain.XkrResourceCommentAgent;
import com.xkr.domain.XkrUserAgent;
import com.xkr.domain.dto.ClassMenuDTO;
import com.xkr.domain.dto.CommentDTO;
import com.xkr.domain.dto.ListCommentDTO;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.entity.XkrClass;
import com.xkr.domain.entity.XkrResourceComment;
import com.xkr.domain.entity.XkrUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@Service
public class CommentService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XkrResourceCommentAgent resourceCommentAgent;

    @Autowired
    private XkrUserAgent xkrUserAgent;

    /**
     * 获取评论列表
     * @param resourceId
     * @param pageNum
     * @param size
     * @return
     */
    public ListCommentDTO getCommentListByResourceId(Long resourceId,int pageNum,int size){
        ListCommentDTO result = new ListCommentDTO();
        if(Objects.isNull(resourceId)){
            return result;
        }
        pageNum = pageNum < 0 ? 1 : pageNum;
        size = size < 0 ? 10 : size;
        Page page = PageHelper.startPage(pageNum,size,true);
        List<XkrResourceComment> resourceComments = resourceCommentAgent.getCommentsByResourceId(resourceId,XkrResourceCommentAgent.STATUS_NORMAL);
        result.setTotalCount((int)page.getTotal());

        //批量获取评论用户
        List<XkrUser> commentUsers = xkrUserAgent.getUserByIds(resourceComments.stream().map(XkrResourceComment::getUserId).collect(Collectors.toList()));

        resourceComments.forEach(resourceComment -> {
            XkrUser user = commentUsers.stream().
                    filter(user1 -> resourceComment.getUserId().equals(user1.getId())).
                    findAny().orElseThrow(RuntimeException::new);
            CommentDTO commentDTO = new CommentDTO();
            buildCommentDTO(commentDTO,resourceComment,user);
            result.getComments().add(commentDTO);
        });

        return result;
    }

    /**
     * 提交评论
     * @param user
     * @param resourceId
     * @param content
     * @param clientIp
     * @return
     */
    public ResponseDTO<Long> submitComment(XkrUser user,Long resourceId,String content,String clientIp){
        if(Objects.isNull(user) || Objects.isNull(resourceId) ||
                StringUtils.isEmpty(content) || StringUtils.isEmpty(clientIp)){
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        //用户无权限
        if(XkrUserAgent.USER_STATUS_NORMAL != user.getStatus()){
            return new ResponseDTO<>(ErrorStatus.COMMENT_USER_NOT_PRILIVEGED);
        }
        ResponseDTO<XkrResourceComment> result = resourceCommentAgent.saveNewResourceComment(clientIp, user, resourceId, content);
        if(ErrorStatus.OK.equals(result.getStatus())){
            return new ResponseDTO<>(result.getData().getId());
        }
        return new ResponseDTO<>(result.getStatus());
    }

    private void buildCommentDTO(CommentDTO commentDTO,XkrResourceComment resourceComment,XkrUser commentUser){
        commentDTO.setCommentDate(resourceComment.getCreateTime());
        commentDTO.setContent(resourceComment.getContent());
        commentDTO.setUserName(commentUser.getUserName());
    }
}
