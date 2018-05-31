package com.xkr.web.controller.admin;

import com.alibaba.fastjson.JSON;
import com.xkr.common.ErrorStatus;
import com.xkr.common.annotation.valid.IsNumberic;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.comment.CommentDTO;
import com.xkr.domain.dto.comment.CommentDetailDTO;
import com.xkr.domain.dto.comment.CommentStatusEnum;
import com.xkr.domain.dto.comment.ListCommentDetailDTO;
import com.xkr.domain.dto.resource.ListResourceDTO;
import com.xkr.domain.dto.resource.ResourceStatusEnum;
import com.xkr.service.CommentService;
import com.xkr.service.ResourceService;
import com.xkr.util.DateUtil;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.comment.CommentDetailVO;
import com.xkr.web.model.vo.comment.CommentVO;
import com.xkr.web.model.vo.comment.ListCommentDetailVO;
import com.xkr.web.model.vo.resource.ListResourceVO;
import com.xkr.web.model.vo.resource.ResourceVO;
import org.apache.commons.lang3.StringUtils;
import org.chris.redbud.validator.annotation.HttpValidate;
import org.chris.redbud.validator.result.ValidResult;
import org.chris.redbud.validator.validate.annotation.ContainsInt;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/27
 */
@Controller
@RequestMapping("/api/admin/comment")
public class AdminCommentController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private CommentService commentService;

    /**
     * 评论搜索
     * @return
     */
    @RequestMapping(value = "/search", method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult<ListCommentDetailVO> searchCommentByKeyWord(
            @RequestParam(name = "updateDate",required = false,defaultValue = "") String updateDate,
            @RequestParam(name = "keyWord",required = false,defaultValue = "") String keyWord,
            @RequestParam(name = "status",required = false,defaultValue = "1") Integer status,
            @RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size){

        ListCommentDetailVO commentDetailVO = new ListCommentDetailVO();
        try {
            Date sDate = Date.from(LocalDateTime.now().minusWeeks(1).toInstant(ZoneOffset.UTC));
            if(StringUtils.isNotEmpty(updateDate)){
                sDate = DateUtil.stringToDate(updateDate,"yyyy-MM-dd");
            }
            ListCommentDetailDTO listCommentDetailDTO = commentService.searchCommentByKeyWord(keyWord,sDate, CommentStatusEnum.getByCode(status),pageNum,size);

            if(!ErrorStatus.OK.equals(listCommentDetailDTO.getStatus())){
                return new BasicResult<>(listCommentDetailDTO.getStatus());
            }

            buildListCommentDetailVO(commentDetailVO, listCommentDetailDTO);

            return new BasicResult<>(commentDetailVO);
        } catch (Exception e) {
            logger.error("后台获取评论异常,updateDate:{},keyWord:{},status:{},pageNum:{},size:{}", updateDate,keyWord,status,pageNum,size, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 获取资源评论详情
     * @param result
     * @return
     */
    @RequestMapping(value = "/detail", method = {RequestMethod.GET})
    @ResponseBody
    @HttpValidate
    public BasicResult getCommentDetail(
            @IsNumberic
            @RequestParam(name = "commentId") String commentId,
            ValidResult result) {
        if(result.hasErrors()){
            return new BasicResult<>(result);
        }
        try {

            ResponseDTO<CommentDTO> responseDTO = commentService.getCommentContentById(Long.valueOf(commentId));

            if(!ErrorStatus.OK.equals(responseDTO.getStatus())){
                return new BasicResult<>(responseDTO.getStatus());
            }
            CommentVO commentVO = new CommentVO();

            CommentDTO commentDTO = responseDTO.getData();

            buildCommentVO(commentVO,commentDTO);

            return new BasicResult<>(commentVO);
        } catch (Exception e) {
            logger.error("后台操作评论异常,commentId:{}", commentId, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 批量操作评论
     * @param result
     * @return
     */
    @RequestMapping(value = "/opt", method = {RequestMethod.POST})
    @ResponseBody
    @HttpValidate
    public BasicResult batchOptResource(
            @RequestParam(name = "commentIds[]") String[] commentIds,
            @ContainsInt(value = {1,3}) @RequestParam(name = "optType") Integer optType,
            ValidResult result) {
        if(result.hasErrors()){
            return new BasicResult<>(result);
        }
        try {
            List<Long> ids = Arrays.stream(commentIds).map(Long::valueOf).collect(Collectors.toList());

            ResponseDTO<Boolean> responseDTO = commentService.batchUpdateCommentByIds(ids, CommentStatusEnum.getByCode(optType));

            if(!ErrorStatus.OK.equals(responseDTO.getStatus())){
                return new BasicResult<>(responseDTO.getStatus());
            }

            return new BasicResult<>(responseDTO.getData());
        } catch (Exception e) {
            logger.error("后台操作评论异常,commentIds:{},optType:{}", JSON.toJSONString(commentIds),optType, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 评论内容修改
     * @param result
     * @return
     */
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    @ResponseBody
    @HttpValidate
    public BasicResult batchOptResource(
            @IsNumberic
            @RequestParam(name = "commentId") String commentId,
            @NotBlank
            @RequestParam(name = "content") String content,
            ValidResult result) {
        if(result.hasErrors()){
            return new BasicResult<>(result);
        }
        try {
            ResponseDTO<Boolean> responseDTO = commentService.updateCommentContent(Long.valueOf(commentId),content);

            if(!ErrorStatus.OK.equals(responseDTO.getStatus())){
                return new BasicResult<>(responseDTO.getStatus());
            }

            return new BasicResult<>(responseDTO.getData());
        } catch (Exception e) {
            logger.error("后台修改评论内容异常,commentId:{},content:{}", commentId,content, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    private void buildCommentVO(CommentVO commentVO, CommentDTO commentDTO){
        commentVO.setStatus(commentDTO.getStatus());
        commentVO.setCommentId(commentDTO.getCommentId());
        commentVO.setUserName(commentDTO.getUserName());
        commentVO.setContent(commentDTO.getContent());
        commentVO.setCommentDate(commentDTO.getCommentDate());
    }


    private void buildListCommentDetailVO(ListCommentDetailVO listCommentDetailVO, ListCommentDetailDTO listCommentDetailDTO) {
        listCommentDetailDTO.getList().forEach(commentDetailDTO -> {
            CommentDetailVO commentDetailVO = new CommentDetailVO();
            commentDetailVO.setClientIp(commentDetailDTO.getClientIp());
            commentDetailVO.setCommentId(commentDetailDTO.getCommentId());
            commentDetailVO.setContent(commentDetailDTO.getContent());
            commentDetailVO.setResourceId(commentDetailDTO.getResourceId());
            commentDetailVO.setStatus(commentDetailDTO.getStatus());
            commentDetailVO.setTitle(commentDetailDTO.getTitle());
            commentDetailVO.setUpdateTime(commentDetailDTO.getUpdateTime());
            commentDetailVO.setUserName(commentDetailDTO.getUserName());
            listCommentDetailVO.getList().add(commentDetailVO);
        });
        listCommentDetailVO.setTotalCount(listCommentDetailDTO.getTotalCount());
    }
}
