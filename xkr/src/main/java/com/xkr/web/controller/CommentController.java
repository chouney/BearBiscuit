package com.xkr.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xkr.common.ErrorStatus;
import com.xkr.common.annotation.CSRFValid;
import com.xkr.common.annotation.valid.IsNumberic;
import com.xkr.common.annotation.valid.UserCheck;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.comment.ListCommentDTO;
import com.xkr.domain.dto.message.ListMessageDTO;
import com.xkr.domain.entity.XkrUser;
import com.xkr.service.CommentService;
import com.xkr.service.MessageService;
import com.xkr.util.IpUtil;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.comment.CommentVO;
import com.xkr.web.model.vo.comment.ListCommentVO;
import com.xkr.web.model.vo.message.ListMessageVO;
import org.apache.shiro.SecurityUtils;
import org.chris.redbud.validator.annotation.MethodValidate;
import org.chris.redbud.validator.result.ValidResult;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@Controller
@RequestMapping("/api/comment")
public class CommentController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private CommentService commentService;

    @RequestMapping(value = "/list",method = {RequestMethod.GET})
    @ResponseBody
    @MethodValidate
    public BasicResult getCommentByResourceId(
            @IsNumberic
            @RequestParam(name = "resId") String resId,
            @RequestParam(name = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            ValidResult result) {
        if(result.hasErrors()){
            return new BasicResult(result);
        }
        try {

            ListCommentDTO listCommentDTO = commentService.getCommentListByResourceId(Long.valueOf(resId),pageNum,size);

            if(!ErrorStatus.OK.equals(listCommentDTO.getStatus())){
                return new BasicResult(listCommentDTO.getStatus());
            }

            ListCommentVO listCommentVO = new ListCommentVO();

            buildListCommentVO(listCommentVO,listCommentDTO);

            return new BasicResult<>(listCommentVO);
        } catch (Exception e) {
            logger.error("CommentController getCommentByResourceId error ,resId:{}", resId, e);
        }
        return new BasicResult(ErrorStatus.ERROR);
    }

    @CSRFValid
    @RequestMapping(value = "/submit",method = {RequestMethod.POST})
    @ResponseBody
    @MethodValidate
    @UserCheck
    public BasicResult<JSONObject> submitUserComment(
            @IsNumberic
            @RequestParam(name = "resId") String resId,
            @NotBlank
            @RequestParam(name = "comment") String comment,
            HttpServletRequest request,
            ValidResult result) {
        if(result.hasErrors()){
            return new BasicResult<>(result);
        }
        try {
            XkrUser user = (XkrUser) SecurityUtils.getSubject().getPrincipal();

            ResponseDTO<Long> responseDTO = commentService.submitComment(user,Long.valueOf(resId),comment ,IpUtil.getIpAddr(request));

            if(!ErrorStatus.OK.equals(responseDTO.getStatus())){
                return new BasicResult<>(responseDTO.getStatus());
            }
            JSONObject output = new JSONObject();
            output.put("commentId",String.valueOf(responseDTO.getData()));

            return new BasicResult<>(output);
        } catch (Exception e) {
            logger.error("CommentController submitUserComment error ,resId:{},comment:{}",resId,comment, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    private void buildListCommentVO(ListCommentVO listCommentVO,ListCommentDTO listCommentDTO){
        listCommentDTO.getComments().forEach(commentDTO -> {
            CommentVO commentVO = new CommentVO();
            commentVO.setCommentDate(commentDTO.getCommentDate());
            commentVO.setContent(commentDTO.getContent());
            commentVO.setUserName(commentDTO.getUserName());
            commentVO.setCommentId(commentDTO.getCommentId());
            commentVO.setStatus(commentDTO.getStatus());
            listCommentVO.getComments().add(commentVO);
        });
        listCommentVO.setTotalCount(listCommentDTO.getTotalCount());
    }

}
