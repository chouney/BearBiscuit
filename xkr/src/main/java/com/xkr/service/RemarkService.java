package com.xkr.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xkr.common.ErrorStatus;
import com.xkr.common.LoginEnum;
import com.xkr.common.OptEnum;
import com.xkr.common.OptLogModuleEnum;
import com.xkr.common.annotation.OptLog;
import com.xkr.core.shiro.admin.AdminShiroUtil;
import com.xkr.domain.XkrRemarkAgent;
import com.xkr.domain.XkrUserAgent;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.remark.ListRemarkDTO;
import com.xkr.domain.dto.remark.RemarkDTO;
import com.xkr.domain.dto.remark.RemarkDetailDTO;
import com.xkr.domain.dto.remark.RemarkStatusEnum;
import com.xkr.domain.dto.user.UserStatusEnum;
import com.xkr.domain.entity.XkrAboutRemark;
import com.xkr.domain.entity.XkrAdminAccount;
import com.xkr.domain.entity.XkrUser;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@Service
public class RemarkService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MessageService messageService;

    @Autowired
    private XkrRemarkAgent remarkAgent;

    @Autowired
    private XkrUserAgent userAgent;

    /**
     * ------------------- 管理员服务 ----------------------
     */
    /**
     * 回复留言
     * @param loginId
     * @param loginEnum
     * @param content
     * @param parentRemarkId
     * @return
     */
    @OptLog(moduleEnum = OptLogModuleEnum.REMARK,optEnum = OptEnum.INSERT)
    public ResponseDTO<Long> replyRemark(Long loginId,LoginEnum loginEnum,String content,
                                         Long parentRemarkId){
        if(Objects.isNull(loginId) || Objects.isNull(loginEnum) || StringUtils.isEmpty(content)||
                Objects.isNull(parentRemarkId)){
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        XkrAboutRemark parentRemark = remarkAgent.getRemarkById(parentRemarkId);
        if(Objects.isNull(parentRemark)){
            return new ResponseDTO<>(ErrorStatus.ERROR);
        }
        XkrAboutRemark remark = remarkAgent.saveNewRemark(parentRemarkId,loginEnum,loginId,null,null,content);
        if(Objects.isNull(remark)){
            return new ResponseDTO<>(ErrorStatus.ERROR);
        }

        //给用户发送消息
        XkrAdminAccount adminAccount = (XkrAdminAccount) SecurityUtils.getSubject().getPrincipal();
        messageService.saveMessageToUser(LoginEnum.ADMIN,adminAccount.getId(),parentRemark.getUserId(),
                String.format(MessageService.REMARK_TEMPLATE,remark.getContent()));

        return new ResponseDTO<>(remark.getId());
    }

    /**
     * 获取留言列表
     * @param pageNum
     * @param size
     * @return
     */
    public ListRemarkDTO getAllRemarkDTO(int pageNum, int size) {
        ListRemarkDTO result = new ListRemarkDTO();
        pageNum = pageNum <= 0 ? 1 : pageNum;
        size = size <= 0 ? 10 : size;
        Page page = PageHelper.startPage(pageNum, size, "update_time desc");
        List<XkrAboutRemark> remarkList = remarkAgent.getAllRemarkList();

        result.setTotalCount((int) page.getTotal());

        List<Long> userId = remarkList.stream().map(XkrAboutRemark::getUserId).collect(Collectors.toList());

        List<XkrUser> users = userAgent.getUserByIds(userId);

        buildListRemarkDTO(result, remarkList, users);

        return result;
    }


    /**
     * 获取留言详情
     * @param id
     * @return
     */
    public ResponseDTO<RemarkDetailDTO> getRemarkDetailById(Long id){
        if(Objects.isNull(id)){
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        XkrAboutRemark aboutRemark = remarkAgent.getRemarkById(id);
        if(Objects.isNull(aboutRemark)){
            return new ResponseDTO<>(ErrorStatus.ERROR);
        }
        RemarkDetailDTO remarkDetailDTO = new RemarkDetailDTO();

        buildRemarkDetailDTO(remarkDetailDTO,aboutRemark);

        return new ResponseDTO<>(remarkDetailDTO);
    }

    /**
     * 批量删除留言
     * @param ids
     * @return
     */
    @OptLog(moduleEnum = OptLogModuleEnum.REMARK,optEnum = OptEnum.DELETE)
    public ResponseDTO<Boolean> batchDeleteRemarkByIds(List<Long> ids){
        if(CollectionUtils.isEmpty(ids)){
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        return new ResponseDTO<>(remarkAgent.batchUpdateRemarkStatus(ids, RemarkStatusEnum.STATUS_DELETED.getCode()));
    }


    private void buildRemarkDetailDTO(RemarkDetailDTO remarkDetailDTO,XkrAboutRemark remark){
        remarkDetailDTO.setContent(remark.getContent());
        remarkDetailDTO.setRemarkId(remark.getId());
        remarkDetailDTO.setSubmitDate(remark.getCreateTime());
        if(Objects.nonNull(remark.getParentRemarkId()) &&
                remark.getParentRemarkId() != XkrRemarkAgent.DEFAULT_PARENT_REMARK_ID){
            XkrAboutRemark parentRemark = remarkAgent.getRemarkById(remark.getParentRemarkId());
            if(Objects.nonNull(parentRemark)){
                RemarkDetailDTO parentDetailDTO = new RemarkDetailDTO();
                buildRemarkDetailDTO(parentDetailDTO,parentRemark);
                remarkDetailDTO.setpRemark(parentDetailDTO);
            }
        }
    }

    private void buildListRemarkDTO(ListRemarkDTO remarkDTO, List<XkrAboutRemark> remarks, List<XkrUser> users) {
        remarks.forEach(xkrAboutRemark -> {
            XkrUser user = users.stream().filter(user1 -> user1.getId().equals(xkrAboutRemark.getUserId())).findFirst().orElse(null);
            RemarkDTO remark = new RemarkDTO();
            JSONObject ext = JSON.parseObject(xkrAboutRemark.getExt());
            remark.setContent(xkrAboutRemark.getContent());
            remark.setRemarkId(xkrAboutRemark.getId());
            remark.setPhone(ext.getString("phone"));
            remark.setQq(ext.getString("qq"));
            if (Objects.isNull(user)) {
                user = new XkrUser();
                user.setUserName("未知账户");
            }
            remark.setUserName(user.getUserName());
            remark.setSubmitDate(xkrAboutRemark.getCreateTime());
            remarkDTO.getList().add(remark);
        });
    }

    /**
     * ------------------- 用户服务 ----------------------
     */

    /**
     * 提交留言
     * @param user
     * @param qq
     * @param phone
     * @param content
     * @return
     */
    public ResponseDTO<Long> submitRemark(XkrUser user,
                                          String qq, String phone,
                                          String content) {
        if (Objects.isNull(user) ||
                StringUtils.isEmpty(qq) || StringUtils.isEmpty(phone) ||
                StringUtils.isEmpty(content)) {
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }

        if (UserStatusEnum.USER_STATUS_NORMAL.getCode() != user.getStatus()) {
            return new ResponseDTO<>(ErrorStatus.REMARK_USER_NOT_PRILIVEGED);
        }

        XkrAboutRemark remark = remarkAgent.saveNewRemark(null, LoginEnum.CUSTOMER,
                user.getId(), qq, phone, content);
        if (Objects.isNull(remark)) {
            return new ResponseDTO<>(ErrorStatus.ERROR);
        }
        return new ResponseDTO<>(remark.getId());
    }



}
