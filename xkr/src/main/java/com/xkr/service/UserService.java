package com.xkr.service;

import com.xkr.common.Const;
import com.xkr.common.ErrorStatus;
import com.xkr.domain.XkrUserAgent;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.UserDTO;
import com.xkr.domain.entity.XkrUser;
import com.xkr.exception.EmailExistException;
import com.xkr.exception.UserExistException;
import com.xkr.service.api.MailApiService;
import com.xkr.util.EncodeUtil;
import com.xkr.util.IdUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@Service
public class UserService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XkrUserAgent xkrUserAgent;

    @Autowired
    private MailApiService mailApiService;

    /**
     * 创建用户
     *
     * @param userName
     * @param email
     * @param userToken
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<Long> createUserAccount(String userName, String email, String userToken)  {
        if (StringUtils.isEmpty(userName) ||
                StringUtils.isEmpty(userToken) ||
                StringUtils.isEmpty(email)) {
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        XkrUser oldUser = xkrUserAgent.getUserByNameOrEmail(userName);
        if(Objects.nonNull(oldUser)){
            return new ResponseDTO<>(ErrorStatus.USER_NAME_ALREADY_EXIST);
        }
        oldUser = xkrUserAgent.getUserByNameOrEmail(email);
        if(Objects.nonNull(oldUser)){
            return new ResponseDTO<>(ErrorStatus.USER_EMAIL_ALREADY_EXIST);
        }
        XkrUser newUser = xkrUserAgent.createUserAccount(userName, userToken, email);
        if (Objects.isNull(newUser)) {
            logger.error("UserService createUserAccount failed , userEmail:{},userName:{},userToken:{}", email, userName, userToken);
            return new ResponseDTO<>(ErrorStatus.ERROR);
        }
        //发送验证邀请
        try {
            mailApiService.sendCaptcha(newUser.getEmail(),EncodeUtil.createEmailValidateString(LocalDateTime.now().toString(),
                    String.valueOf(newUser.getId()), String.valueOf(Const.USER_ACCOUNT_VERIFY_TYPE_REG)));
            return new ResponseDTO<>(newUser.getId());
        } catch (MessagingException e) {
            logger.error("UserService sendCaptcha failed ,userEmail:{},userName:{},userToken:{}", email, userName, userToken, e);
        }
        return new ResponseDTO<>(ErrorStatus.ERROR);
    }

    /**
     * token验证
     * @return
     */
    public ResponseDTO<Long> validateAccountByToken(Long userId,int type) {
        if (Objects.isNull(userId)) {
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        try {
            //暂无过期限制
            if(type == Const.USER_ACCOUNT_VERIFY_TYPE_REG && xkrUserAgent.verifyUserAccountByUserId(userId)){
                return new ResponseDTO<>(userId);
            }else if(type == Const.USER_ACCOUNT_VERIFY_TYPE_UPDATE_PASSWORD){
                //渲染密码页面
                return new ResponseDTO<>(userId);
            }
        }catch (Exception e){
            logger.error("UserService validateAccountByToken error ,userId:{},type:{}",userId,type,e);
        }
        return new ResponseDTO<>(ErrorStatus.ERROR);

    }

    /**
     * 修改密码前置
     * @param email
     * @return
     */
    public ResponseDTO<Boolean> preUpdateUserToken(String email){
        if(StringUtils.isEmpty(email)){
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        //发送验证邀请
        try {
            XkrUser user = xkrUserAgent.getUserByNameOrEmail(email);
            if(Objects.isNull(user)){
                return new ResponseDTO<>(ErrorStatus.USER_NOT_EXIST);
            }
            if(XkrUserAgent.USER_STATUS_FREEZED == user.getStatus()){
                return new ResponseDTO<>(ErrorStatus.USER_ALREADY_FREEZED);
            }
            if(XkrUserAgent.USER_STATUS_NORMAL == user.getStatus()){
                return new ResponseDTO<>(ErrorStatus.USER_ALREADY_ACTIVE);
            }
            //发送邮箱验证
            mailApiService.sendCaptcha(user.getEmail(),EncodeUtil.createEmailValidateString(LocalDateTime.now().toString(),
                    String.valueOf(user.getId()), String.valueOf(Const.USER_ACCOUNT_VERIFY_TYPE_UPDATE_PASSWORD)));
            return new ResponseDTO<>(true);
        } catch (MessagingException e) {
            logger.error("UserService sendCaptcha failed ,userEmail:{}", email, e);
        }
        return new ResponseDTO<>(ErrorStatus.ERROR);
    }

    /**
     * 后置修改密码处理
     * @param userId
     * @param userToken
     * @return
     */
    public ResponseDTO<Boolean> postUpdateUserToken(Long userId,String userToken){
        if(Objects.isNull(userId) || StringUtils.isEmpty(userToken)){
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        XkrUser user = xkrUserAgent.getUserById(userId);
        if(Objects.isNull(user)){
            return new ResponseDTO<>(ErrorStatus.USER_NOT_EXIST);
        }
        if(XkrUserAgent.USER_STATUS_FREEZED == user.getStatus()){
            return new ResponseDTO<>(ErrorStatus.USER_ALREADY_FREEZED);
        }
        return new ResponseDTO<>(xkrUserAgent.updateUserTokenByUser(user,userToken));
    }

}
