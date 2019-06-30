package com.xkr.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.SqlUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.xkr.common.ErrorStatus;
import com.xkr.common.OptEnum;
import com.xkr.common.OptLogModuleEnum;
import com.xkr.common.annotation.OptLog;
import com.xkr.domain.XkrResourceAgent;
import com.xkr.domain.XkrResourceCommentAgent;
import com.xkr.domain.XkrResourceUserAgent;
import com.xkr.domain.XkrUserAgent;
import com.xkr.domain.dto.*;
import com.xkr.domain.dto.comment.*;
import com.xkr.domain.dto.search.CommentIndexDTO;
import com.xkr.domain.dto.search.ResourceIndexDTO;
import com.xkr.domain.dto.search.SearchResultListDTO;
import com.xkr.domain.dto.user.UserStatusEnum;
import com.xkr.domain.entity.XkrResource;
import com.xkr.domain.entity.XkrResourceComment;
import com.xkr.domain.entity.XkrResourceUser;
import com.xkr.domain.entity.XkrUser;
import com.xkr.service.api.SearchApiService;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
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
    private XkrResourceUserAgent resourceUserAgent;

    @Autowired
    private XkrResourceAgent xkrResourceAgent;

    @Autowired
    private SearchApiService searchApiService;

    @Autowired
    private XkrUserAgent xkrUserAgent;

    /**
     * ------------------- 管理员服务 ----------------------
     */
    /**
     * 后台用户搜索
     *
     * @param keyword
     * @param updateTime
     * @param status
     * @param pageNum
     * @param size
     * @return
     */
    public ListCommentDetailDTO searchCommentByKeyWord(String keyword,String userName, Date updateTime,
                                                    CommentStatusEnum status, int pageNum, int size) {
        ListCommentDetailDTO result = new ListCommentDetailDTO();
        if (StringUtils.isEmpty(keyword) && Objects.isNull(updateTime) &&
                Objects.isNull(status)) {
            result.setStatus(ErrorStatus.PARAM_ERROR);
            return result;
        }
        int offset = pageNum - 1 < 0 ? 0 : (pageNum - 1) * size;

        //走db查询逻辑
        size = size <= 0 ? 10 : size;
        String sortKey = "update_time" ;
        Page page = PageHelper.startPage(pageNum, size, sortKey + " desc");
        List<XkrResourceComment> resourceComments = resourceCommentAgent.searchByFilter(keyword,userName,updateTime,status.getCode());

        result.setTotalCount((int) page.getTotal());

        SqlUtil.clearLocalPage();

        List<Long> resIds = resourceComments.stream().map(XkrResourceComment::getResourceId).collect(Collectors.toList());
        List<XkrResource> resources = xkrResourceAgent.getResourceListByIds(resIds);

        List<Long> userIds = resourceComments.stream().map(XkrResourceComment::getUserId).collect(Collectors.toList());
        List<XkrUser> users = xkrUserAgent.getUserByIds(userIds);

        buildListCommentDetailDTO(result,resourceComments,resources,users);

        /** es 搜索逻辑**/
//        SearchResultListDTO<CommentIndexDTO> searchResultListDTO = null;
//        if(StringUtils.isEmpty(keyword) && StringUtils.isEmpty(userName)){
//            searchResultListDTO = searchApiService.searchByFilterField(CommentIndexDTO.class,ImmutableMap.of("status", status.getCode()),Pair.of(updateTime, null),
//                    "updateTime",null,offset,size);
//        }else {
//            searchResultListDTO = searchApiService.searchByKeyWordInField(
//                    CommentIndexDTO.class, keyword, ImmutableMap.of("title", 0.5F, "content", 1.5F, "userName", 0.5F),
//                    ImmutableMap.of("status", status.getCode()), Pair.of(updateTime, null), "updateTime",
//                    null, null, offset, size
//            );
//        }

//        result.setTotalCount((int) searchResultListDTO.getTotalCount());
//
//        buildListCommentDetailDTO(result, searchResultListDTO);

        return result;
    }

    /**
     * 评论批量操作
     *
     * @param commentIds
     * @param status
     * @return
     */
    @OptLog(moduleEnum = OptLogModuleEnum.COMMENT,optEnum = OptEnum.UPDATE)
    public ResponseDTO<Boolean> batchUpdateCommentByIds(List<Long> commentIds, CommentStatusEnum status) {
        if (CollectionUtils.isEmpty(commentIds) || Objects.isNull(status)) {
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        Boolean success = resourceCommentAgent.batchUpdateCommentByIds(commentIds, status);
        return new ResponseDTO<>(success);
    }

    /**
     * 获取评论内容
     *
     * @param commentId
     * @return
     */
    public ResponseDTO<CommentDTO> getCommentContentById(Long commentId) {
        if (Objects.isNull(commentId)) {
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        XkrResourceComment xkrResourceComment = resourceCommentAgent.getCommentById(commentId);
        if (Objects.isNull(xkrResourceComment)) {
            return new ResponseDTO<>(ErrorStatus.ERROR);
        }
        CommentDTO commentDTO = new CommentDTO();
        XkrUser user = xkrUserAgent.getUserById(xkrResourceComment.getUserId());
        buildCommentDTO(commentDTO, xkrResourceComment, user);
        return new ResponseDTO<>(commentDTO);
    }

    /**
     * 修改评论内容
     *
     * @param commentId
     * @param content
     * @return
     */
    @OptLog(moduleEnum = OptLogModuleEnum.COMMENT,optEnum = OptEnum.UPDATE)
    public ResponseDTO<Boolean> updateCommentContent(Long commentId,String content) {
        if (Objects.isNull(commentId) || StringUtils.isEmpty(content)) {
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        Boolean success = resourceCommentAgent.updateCommentById(commentId, content);
        return new ResponseDTO<>(success);
    }

    private void buildListCommentDetailDTO(ListCommentDetailDTO result, SearchResultListDTO<CommentIndexDTO> resultListDTO) {
        resultListDTO.getSearchResultDTO().forEach(commentIndexDTO -> {
            CommentDetailDTO detailDTO = new CommentDetailDTO();
            detailDTO.setClientIp(commentIndexDTO.getClientIp());
            detailDTO.setCommentId(commentIndexDTO.getCommentId());
            detailDTO.setContent(commentIndexDTO.getContent());
            detailDTO.setResourceId(commentIndexDTO.getResourceId());
            detailDTO.setStatus(commentIndexDTO.getStatus());
            detailDTO.setTitle(commentIndexDTO.getTitle());
            detailDTO.setUpdateTime(commentIndexDTO.getUpdateTime());
            detailDTO.setUserName(commentIndexDTO.getUserName());
            result.getList().add(detailDTO);
        });
    }

    private void buildListCommentDetailDTO(ListCommentDetailDTO result, List<XkrResourceComment> resultListDTO,
                                           List<XkrResource> xkrResources,List<XkrUser> xkrUsers) {
        resultListDTO.forEach(commentIndexDTO -> {
            CommentDetailDTO detailDTO = new CommentDetailDTO();
            detailDTO.setClientIp(commentIndexDTO.getClientIp());
            detailDTO.setCommentId(commentIndexDTO.getId());
            detailDTO.setContent(commentIndexDTO.getContent());
            detailDTO.setResourceId(commentIndexDTO.getResourceId());
            detailDTO.setStatus((int)commentIndexDTO.getStatus());
            XkrResource xkrResource = xkrResources.stream().filter(res -> commentIndexDTO.getResourceId().equals(res.getClassId())).findFirst().orElse(null);
            if(Objects.nonNull(xkrResource)){
                detailDTO.setTitle(xkrResource.getTitle());
            }
            detailDTO.setUpdateTime(commentIndexDTO.getUpdateTime());
            XkrUser xkrUser = xkrUsers.stream().filter(user -> commentIndexDTO.getUserId().equals(user.getId())).findFirst().orElse(null);
            if(Objects.nonNull(xkrUser)) {
                detailDTO.setUserName(xkrUser.getUserName());
            }
            result.getList().add(detailDTO);
        });
    }

    /**
     * ------------------- 用户服务 ----------------------
     */
    /**
     * 获取评论列表
     *
     * @param resourceId
     * @param pageNum
     * @param size
     * @return
     */
    public ListCommentDTO getCommentListByResourceId(Long resourceId, int pageNum, int size) {
        ListCommentDTO result = new ListCommentDTO();
        if (Objects.isNull(resourceId)) {
            result.setStatus(ErrorStatus.PARAM_ERROR);
            return result;
        }
        pageNum = pageNum <= 0 ? 1 : pageNum;
        size = size <= 0 ? 10 : size;
        Page page = PageHelper.startPage(pageNum, size, true);
        List<XkrResourceComment> resourceComments = resourceCommentAgent.getCommentsByResourceId(resourceId, ImmutableList.of(CommentStatusEnum.STATUS_NORMAL.getCode()));
        result.setTotalCount((int) page.getTotal());

        //批量获取评论用户
        List<XkrUser> commentUsers = xkrUserAgent.getUserByIds(resourceComments.stream().map(XkrResourceComment::getUserId).collect(Collectors.toList()));

        resourceComments.forEach(resourceComment -> {
            XkrUser user = commentUsers.stream().
                    filter(user1 -> resourceComment.getUserId().equals(user1.getId())).
                    findAny().orElse(null);
            CommentDTO commentDTO = new CommentDTO();
            buildCommentDTO(commentDTO, resourceComment, user);
            result.getComments().add(commentDTO);
        });

        return result;
    }

    /**
     * 提交评论
     *
     * @param user
     * @param resourceId
     * @param content
     * @param clientIp
     * @return
     */
    public ResponseDTO<Long> submitComment(XkrUser user, Long resourceId, String content, String clientIp) {
        if (Objects.isNull(user) || Objects.isNull(resourceId) ||
                StringUtils.isEmpty(content) || StringUtils.isEmpty(clientIp)) {
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        //用户无权限
        if (UserStatusEnum.USER_STATUS_NORMAL.getCode() != user.getStatus()) {
            return new ResponseDTO<>(ErrorStatus.COMMENT_USER_NOT_PRILIVEGED);
        }
        //查询用户是否下载过该评论
        List<XkrResourceUser> xkrResourceUsers = resourceUserAgent.getResourceByResAndUserId(user.getId(),resourceId,XkrResourceUserAgent.STATUS_PAYED);
        if(CollectionUtils.isEmpty(xkrResourceUsers)){
            return new ResponseDTO<>(ErrorStatus.COMMENT_USER_NOT_PRILIVEGED);
        }

        ResponseDTO<XkrResourceComment> result = resourceCommentAgent.saveNewResourceComment(clientIp, user, resourceId, content);
        if (ErrorStatus.OK.equals(result.getStatus())) {
            return new ResponseDTO<>(result.getData().getId());
        }
        return new ResponseDTO<>(result.getStatus());
    }

    private void buildCommentDTO(CommentDTO commentDTO, XkrResourceComment resourceComment, XkrUser commentUser) {
        commentDTO.setCommentDate(resourceComment.getCreateTime());
        commentDTO.setContent(resourceComment.getContent());
        commentDTO.setCommentId(resourceComment.getId());
        commentDTO.setStatus(Integer.valueOf(resourceComment.getStatus()));
        if (Objects.isNull(commentUser)) {
            commentUser = new XkrUser();
            commentDTO.setUserName("未知用户");
        }
        commentDTO.setUserName(commentUser.getUserName());
    }
}
