package com.xkr.service;

import com.google.common.collect.ImmutableMap;
import com.xkr.common.*;
import com.xkr.common.annotation.OptLog;
import com.xkr.core.shiro.LoginAuthenticationToken;
import com.xkr.dao.cache.AdminIndexRedisService;
import com.xkr.domain.XkrLoginTokenAgent;
import com.xkr.domain.XkrUserAgent;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.search.ResourceIndexDTO;
import com.xkr.domain.dto.search.SearchResultListDTO;
import com.xkr.domain.dto.search.UserIndexDTO;
import com.xkr.domain.dto.user.ListUserDetailDTO;
import com.xkr.domain.dto.user.UserDTO;
import com.xkr.domain.dto.user.UserDetailDTO;
import com.xkr.domain.dto.user.UserStatusEnum;
import com.xkr.domain.entity.XkrLoginToken;
import com.xkr.domain.entity.XkrUser;
import com.xkr.exception.RegUserException;
import com.xkr.service.api.MailApiService;
import com.xkr.service.api.SearchApiService;
import com.xkr.util.EncodeUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
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
public class UserService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XkrUserAgent xkrUserAgent;

    @Autowired
    private XkrLoginTokenAgent xkrLoginTokenAgent;

    @Autowired
    private MailApiService mailApiService;

    @Autowired
    private SearchApiService searchApiService;

    @Autowired
    private AdminIndexRedisService adminIndexRedisService;

    /**
     * ------------------- 管理员服务 ----------------------
     */

    /**
     * 后台用户搜索
     *
     * @param userLogin
     * @param createDate
     * @param status
     * @param pageNum
     * @param size
     * @return
     */
    public ListUserDetailDTO searchUserByAdmin(String userLogin, Date createDate,
                                               UserStatusEnum status, int pageNum, int size) {
        ListUserDetailDTO result = new ListUserDetailDTO();
        if (StringUtils.isEmpty(userLogin) && Objects.isNull(createDate) &&
                Objects.isNull(status)) {
            result.setStatus(ErrorStatus.PARAM_ERROR);
            return result;
        }
        int offset = pageNum - 1 < 0 ? 0 : pageNum - 1;
        size = size <= 0 ? 10 : size;
        SearchResultListDTO<UserIndexDTO> searchResultListDTO = null;
        if(StringUtils.isEmpty(userLogin)){
            searchResultListDTO = searchApiService.searchByFilterField(UserIndexDTO.class,ImmutableMap.of("status", status.getCode()),Pair.of(createDate, null),
                    "createTime",null,offset,size);
        } else {
            searchResultListDTO = searchApiService.searchByKeyWordInField(
                    UserIndexDTO.class, userLogin, ImmutableMap.of("userName", 1F, "email", 0.5F),
                    ImmutableMap.of("status", status.getCode()), Pair.of(createDate, null), "createTime",
                    null, null, offset, size
            );
        }

        result.setTotalCount((int) searchResultListDTO.getTotalCount());

        List<Long> userIds = searchResultListDTO.getSearchResultDTO().stream().
                map(UserIndexDTO::getUserId).collect(Collectors.toList());

        List<XkrUser> users = xkrUserAgent.getUserByIds(userIds);

        List<XkrLoginToken> loginTokens = xkrLoginTokenAgent.getUserLoginRecordByIds(userIds);

        buildListUserDetailDTO(result, users, loginTokens, searchResultListDTO);

        return result;
    }

    /**
     * 批量操作用户状态
     *
     * @param userIds
     * @param status
     * @return
     */
    @OptLog(moduleEnum = OptLogModuleEnum.USER, optEnum = OptEnum.UPDATE)
    public ResponseDTO<Boolean> batchUpdateUserStatus(List<Long> userIds, UserStatusEnum status) {
        if (CollectionUtils.isEmpty(userIds) || Objects.isNull(status)) {
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        Boolean success = xkrUserAgent.batchUpdateUserByIds(userIds, status);
        return new ResponseDTO<>(success);
    }

    private void buildListUserDetailDTO(ListUserDetailDTO result, List<XkrUser> users, List<XkrLoginToken> loginTokens, SearchResultListDTO<UserIndexDTO> searchResultListDTO) {
        searchResultListDTO.getSearchResultDTO().forEach(userIndexDTO -> {
            XkrUser user = users.stream().filter(user1 -> userIndexDTO.getUserId().equals(user1.getId())).findAny().orElseThrow(RuntimeException::new);
            XkrLoginToken loginToken = loginTokens.stream().filter(loginToken1 -> userIndexDTO.getUserId().equals(loginToken1.getUserId())).findAny().orElse(null);
            UserDetailDTO userDetailDTO = new UserDetailDTO();
            buildUserDetailDTO(userDetailDTO, user, loginToken);
            result.getUserList().add(userDetailDTO);
        });
    }

    private void buildUserDetailDTO(UserDetailDTO userDetailDTO, XkrUser user, XkrLoginToken loginToken) {
        userDetailDTO.setClientIp(loginToken.getClientIp());
        userDetailDTO.setEmail(user.getEmail());
        userDetailDTO.setLastLoginDate(loginToken.getUpdateTime());
        userDetailDTO.setStatus(Integer.valueOf(user.getStatus()));
        userDetailDTO.setTotalRecharge(user.getTotalRecharge());
        userDetailDTO.setUserId(user.getId());
        userDetailDTO.setUserName(user.getUserName());
        userDetailDTO.setWealth(user.getWealth());
        userDetailDTO.setUserToken(user.getUserToken());
    }

    /**
     * ------------------- 用户服务 ----------------------
     */

    public ResponseDTO<Long> userLogin(LoginAuthenticationToken token) {
        ResponseDTO<Long> result = new ResponseDTO<>();
        token.setLoginType(LoginEnum.CUSTOMER.toString());
        //获取当前的Subject
        Subject currentUser = SecurityUtils.getSubject();
        String userAccount = token.getUsername();
        try {
            logger.info("对用户[" + userAccount + "]进行登录验证..验证开始");
            currentUser.login(token);
        } catch (IncorrectCredentialsException | UnknownAccountException ice) {
            result.setStatus(ErrorStatus.USER_INCORRECT_LOGIN);
            token.clear();
            logger.info("对用户[" + userAccount + "]进行登录验证..验证未通过,错误的凭证");
            return result;
        } catch (LockedAccountException lae) {
            logger.info("对用户[" + userAccount + "]进行登录验证..验证未通过,账户已锁定");
            result.setStatus(ErrorStatus.USER_ALREADY_FREEZED);
            token.clear();
            return result;
        } catch (ExcessiveAttemptsException eae) {
            logger.info("对用户[" + userAccount + "]进行登录验证..验证未通过,错误次数过多");
            result.setStatus(ErrorStatus.USER_ATTEMPT_EXCESSIVE_LOGIN);
            token.clear();
            return result;
        } catch (Exception ae) {
            logger.error("对用户[" + userAccount + "]进行登录验证..验证未通过,堆栈轨迹如下", ae);
            result.setStatus(ErrorStatus.USER_LOGIN_ERROR);
            token.clear();
            return result;
        }
        //验证是否登录成功
        if (currentUser.isAuthenticated()) {
            adminIndexRedisService.incrLoginCount();
            XkrUser user = (XkrUser) currentUser.getPrincipal();
            xkrLoginTokenAgent.recordLogin(user.getId(), LoginEnum.CUSTOMER);
            return new ResponseDTO<>(user.getId());
        }
        token.clear();
        return new ResponseDTO<>(ErrorStatus.USER_INCORRECT_LOGIN);
    }

    /**
     * 登出
     *
     * @return
     */
    public ResponseDTO<Boolean> userLogout() {
        SecurityUtils.getSubject().logout();
        return new ResponseDTO<>(true);
    }

    /**
     * 创建用户
     *
     * @param userName
     * @param email
     * @param userToken
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<Long> createUserAccount(String userName, String email, String userToken) throws RegUserException {
        if (StringUtils.isEmpty(userName) ||
                StringUtils.isEmpty(userToken) ||
                StringUtils.isEmpty(email)) {
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        XkrUser oldUser = xkrUserAgent.getUserByNameOrEmail(userName);
        if (Objects.nonNull(oldUser)) {
            return new ResponseDTO<>(ErrorStatus.USER_NAME_ALREADY_EXIST);
        }
        oldUser = xkrUserAgent.getUserByNameOrEmail(email);
        if (Objects.nonNull(oldUser)) {
            return new ResponseDTO<>(ErrorStatus.USER_EMAIL_ALREADY_EXIST);
        }
        XkrUser newUser = xkrUserAgent.createUserAccount(userName, userToken, email);
        if (Objects.isNull(newUser)) {
            logger.error("UserService createUserAccount failed , userEmail:{},userName:{},userToken:{}", email, userName, userToken);
            return new ResponseDTO<>(ErrorStatus.ERROR);
        }
        //发送验证邀请
        try {

            adminIndexRedisService.incrRegCount();

            mailApiService.sendRegValidCaptcha(newUser.getEmail(), newUser.getUserName(), EncodeUtil.createEmailValidateString(LocalDateTime.now().toString(),
                    String.valueOf(newUser.getId()), String.valueOf(Const.USER_ACCOUNT_VERIFY_TYPE_REG)));
            return new ResponseDTO<>(newUser.getId());
        } catch (MessagingException e) {
            logger.error("UserService sendCaptcha failed ,userEmail:{},userName:{},userToken:{}", email, userName, userToken, e);
        }
        throw new RegUserException();
    }

    /**
     * token验证
     *
     * @return
     */
    public ResponseDTO<Long> validateAccountByToken(Long userId, int type) {
        if (Objects.isNull(userId)) {
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        try {
            XkrUser user = xkrUserAgent.getUserById(userId);
            if (Objects.isNull(user)) {
                return new ResponseDTO<>(ErrorStatus.USER_NOT_EXIST);
            }
            //如果是注册验证
            if (type == Const.USER_ACCOUNT_VERIFY_TYPE_REG) {
                if (UserStatusEnum.USER_STATUS_NORMAL.getCode() == user.getStatus()) {
                    return new ResponseDTO<>(ErrorStatus.USER_ALREADY_ACTIVE);
                }
                if (UserStatusEnum.USER_STATUS_FREEZED.getCode() == user.getStatus()) {
                    return new ResponseDTO<>(ErrorStatus.USER_ALREADY_FREEZED);
                }
                if (xkrUserAgent.verifyUserAccountByUserId(userId)) {
                    return new ResponseDTO<>(userId);
                }
            } else if (type == Const.USER_ACCOUNT_VERIFY_TYPE_UPDATE_PASSWORD) {
                //修改密码验证
                //渲染密码页面
                return new ResponseDTO<>(userId);
            }
        } catch (Exception e) {
            logger.error("UserService validateAccountByToken error ,userId:{},type:{}", userId, type, e);
        }
        return new ResponseDTO<>(ErrorStatus.ERROR);

    }

    /**
     * 修改密码前置
     *
     * @param email
     * @return
     */
    public ResponseDTO<Boolean> preUpdateUserToken(String email) {
        if (StringUtils.isEmpty(email)) {
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        //发送验证邀请
        try {
            XkrUser user = xkrUserAgent.getUserByNameOrEmail(email);
            if (Objects.isNull(user)) {
                return new ResponseDTO<>(ErrorStatus.USER_NOT_EXIST);
            }
            // 冻结是否能够更改密码
            if (UserStatusEnum.USER_STATUS_FREEZED.getCode() == user.getStatus()) {
                return new ResponseDTO<>(ErrorStatus.USER_ALREADY_FREEZED);
            }
//            if (UserStatusEnum.USER_STATUS_NORMAL.getCode() == user.getStatus()) {
//                return new ResponseDTO<>(ErrorStatus.USER_ALREADY_ACTIVE);
//            }
            //发送邮箱验证
            mailApiService.sendPasswordUpdateValidCaptcha(user.getEmail(), user.getUserName(), EncodeUtil.createEmailValidateString(LocalDateTime.now().toString(),
                    String.valueOf(user.getId()), String.valueOf(Const.USER_ACCOUNT_VERIFY_TYPE_UPDATE_PASSWORD)));
            return new ResponseDTO<>(true);
        } catch (MessagingException e) {
            logger.error("UserService sendCaptcha failed ,userEmail:{}", email, e);
        }
        return new ResponseDTO<>(ErrorStatus.ERROR);
    }

    /**
     * 后置修改密码处理
     *
     * @param userId
     * @param userToken
     * @return
     */
    public ResponseDTO<Boolean> postUpdateUserToken(Long userId, String userToken) {
        if (Objects.isNull(userId) || StringUtils.isEmpty(userToken)) {
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        XkrUser user = xkrUserAgent.getUserById(userId);
        if (Objects.isNull(user)) {
            return new ResponseDTO<>(ErrorStatus.USER_NOT_EXIST);
        }
        if(UserStatusEnum.USER_STATUS_FREEZED.getCode() == user.getStatus()){
            return new ResponseDTO<>(ErrorStatus.USER_ALREADY_FREEZED);
        }
        return new ResponseDTO<>(xkrUserAgent.updateUserTokenByUser(user, userToken));
    }

    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    public ResponseDTO<UserDTO> getUserDTOById(Long userId) {
        if (Objects.isNull(userId)) {
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        XkrUser xkrUser = xkrUserAgent.getUserById(userId);
        if (Objects.isNull(xkrUser)) {
            return new ResponseDTO<>(ErrorStatus.USER_NOT_EXIST);
        }
        UserDTO userDTO = new UserDTO();

        buildUserDTO(userDTO, xkrUser);

        return new ResponseDTO<>(userDTO);
    }

    private void buildUserDTO(UserDTO userDTO, XkrUser user) {
        userDTO.setCreateTime(user.getCreateTime());
        userDTO.setEmail(user.getEmail());
        userDTO.setStatus(Integer.valueOf(user.getStatus()));
        userDTO.setUserId(user.getId());
        userDTO.setUserName(user.getUserName());
        userDTO.setWealth(user.getWealth());
    }

}
