package com.xkr.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.xkr.common.CaptchaEnum;
import com.xkr.common.Const;
import com.xkr.common.ErrorStatus;
import com.xkr.common.annotation.CSRFGen;
import com.xkr.common.annotation.valid.Captcha;
import com.xkr.common.annotation.valid.IsNumberic;
import com.xkr.core.shiro.LoginAuthenticationToken;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.user.UserDTO;
import com.xkr.domain.entity.XkrUser;
import com.xkr.service.UserService;
import com.xkr.util.EncodeUtil;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.user.UserVO;
import org.apache.shiro.SecurityUtils;
import org.chris.redbud.validator.annotation.MethodValidate;
import org.chris.redbud.validator.result.ValidResult;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/29
 */
@RequestMapping("/api/user")
@Controller
public class UserController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userName
     * @param userToken
     * @param email
     * @param result
     * @return
     */
    @RequestMapping(value = "/reg", method = {RequestMethod.POST})
    @ResponseBody
    @MethodValidate
    public BasicResult<JSONObject> regUser(
            @NotBlank
            @Length(min = 6,max = 15,message = "用户名长度需在{min}和{max}之间")
            @RequestParam(name = "userName") String userName,
            @NotBlank
            @Length(min = 6,max = 15,message = "密码长度需在{min}和{max}之间")
            @RequestParam(name = "userToken") String userToken,
            @Email
            @RequestParam(name = "email") String email,
            @Captcha(CaptchaEnum.REG_TYPE)
            @RequestParam(name = "captcha") String captcha,
            ValidResult result) {
        if (result.hasErrors()) {
            return new BasicResult<>(result);
        }
        try {
            ResponseDTO responseDTO = userService.createUserAccount(userName, email, userToken);
            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult<>(responseDTO.getStatus());
            }
            JSONObject output = new JSONObject();
            output.put("userId", String.valueOf(responseDTO.getData()));
            return new BasicResult<>(output);
        } catch (Exception e) {
            logger.error("注册用户时异常 ,userName:{},userToken:{},email:{}", userName, userToken, email, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 邮箱验证
     *
     * @param token
     * @param result
     * @return
     */
    @CSRFGen
    @RequestMapping(value = "/validate", method = {RequestMethod.POST})
    @ResponseBody
    @MethodValidate
    public BasicResult<JSONObject> validateEmail(
            @NotBlank
            @RequestParam(name = "token") String token,
            ValidResult result) {
        if (result.hasErrors()) {
            return new BasicResult<>(result);
        }
        try {
            //解析token
            String[] args = EncodeUtil.getEmailValidateString(token);
            //取出userId以及验证Type
            if (args.length < 3) {
                return new BasicResult<>(ErrorStatus.PARAM_ERROR);
            }
            LocalDateTime dateTime = LocalDateTime.parse(args[0]);
            LocalDateTime now = LocalDateTime.now();

            //如果会话过期
            if(dateTime.plus(Const.VALIDATE_SESSION_EXPIRE, ChronoUnit.MINUTES).isBefore(now)){
                return new BasicResult<>(ErrorStatus.USER_EMAIL_VALIDATE_SESSION_EXPIRED);
            }

            //用户id
            Long userId = Long.valueOf(args[1]);
            //验证类型1为注册验证,2为修改密码验证
            Integer type = Integer.valueOf(args[2]);

            ResponseDTO<Long> responseDTO = userService.validateAccountByToken(userId, type);

            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult<>(responseDTO.getStatus());
            }

            JSONObject output = new JSONObject();
            output.put("userId", String.valueOf(responseDTO.getData()));
            output.put("type", type);

            return new BasicResult<>(output);
        } catch (Exception e) {
            logger.error("邮箱验证时异常,token:{}", token, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 用户登录
     *
     * @param userAccount
     * @param userToken
     * @param captcha
     * @param isRemember
     * @param request
     * @param result
     * @return
     */
    @MethodValidate
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public BasicResult<JSONObject> login(
            @NotBlank
            @RequestParam(name = "userName")
                    String userAccount,
            @NotBlank
            @RequestParam(name = "userToken")
                    String userToken,
            @Captcha(CaptchaEnum.LOGIN_TYPE)
            @RequestParam(name = "captcha")
                    String captcha,
            @RequestParam(name = "isRemember", required = false, defaultValue = "false")
                    boolean isRemember,
            HttpServletRequest request,
            ValidResult result) {
        if (result.hasErrors()) {
            return new BasicResult<>(result);
        }
        try {
            LoginAuthenticationToken token = new LoginAuthenticationToken(userAccount, userToken, isRemember);
            ResponseDTO<Long> responseDTO = userService.userLogin(token);

            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult<>(responseDTO.getStatus());
            }

            JSONObject output = new JSONObject();
            output.put("userId", String.valueOf(responseDTO.getData()));

            return new BasicResult<>(output);
        } catch (Exception e) {
            logger.error("用户登录时异常,userName:{},userToken:{},captcha:{},isRemember:{}", userAccount, userToken, captcha, isRemember, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 用户登出
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public BasicResult<JSONObject> logout() {
        try {
            ResponseDTO<Boolean> responseDTO = userService.userLogout();

            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult<>(responseDTO.getStatus());
            }
            return new BasicResult<>(ErrorStatus.OK);
        } catch (Exception e) {
            logger.error("用户登出时异常", e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 修改密码前置
     * @param email
     * @param result
     * @return
     */
    @MethodValidate
    @RequestMapping(value = "/pre_update", method = RequestMethod.POST)
    @ResponseBody
    public BasicResult<JSONObject> preUpdate(
            @Email
            @RequestParam(name = "email") String email,
            @Captcha(CaptchaEnum.UPDATE_PASS_TYPE)
            @RequestParam(name = "captcha") String captcha,
            ValidResult result) {
        if (result.hasErrors()) {
            return new BasicResult<>(result);
        }
        try {
            ResponseDTO<Boolean> responseDTO = userService.preUpdateUserToken(email);

            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult<>(responseDTO.getStatus());
            }
            return new BasicResult<>(ErrorStatus.OK);
        } catch (Exception e) {
            logger.error("用户修改密码前置时异常,email:{}", email, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 修改密码操作
     * @param userId
     * @param userToken
     * @param result
     * @return
     */
    @MethodValidate
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public BasicResult<JSONObject> update(
            @IsNumberic
            @RequestParam(name = "userId") String userId,
            @NotBlank
            @RequestParam(name = "userToken") String userToken,
            ValidResult result) {
        if (result.hasErrors()) {
            return new BasicResult<>(result);
        }
        try {
            ResponseDTO<Boolean> responseDTO = userService.postUpdateUserToken(Long.valueOf(userId),userToken);

            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult<>(responseDTO.getStatus());
            }
            return new BasicResult<>(ErrorStatus.OK);
        } catch (Exception e) {
            logger.error("用户修改密码时异常,userId:{},userToken:{}", userId,userToken, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public BasicResult<UserVO> userDetail() {
        try {
            XkrUser user = (XkrUser) SecurityUtils.getSubject().getPrincipal();
            ResponseDTO<UserDTO> responseDTO = userService.getUserDTOById(user.getId());

            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult<>(responseDTO.getStatus());
            }

            UserDTO userDTO = responseDTO.getData();

            UserVO userVO = new UserVO();

            buildUserVO(userVO,userDTO);

            return new BasicResult<>(userVO);
        } catch (Exception e) {
            logger.error("用户修改密码时异常", e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    private void buildUserVO(UserVO userVO,UserDTO userDTO){
        userVO.setCreateTime(userDTO.getCreateTime());
        userVO.setEmail(userDTO.getEmail());
        userVO.setStatus(userDTO.getStatus());
        userVO.setUserId(userDTO.getUserId());
        userVO.setUserName(userDTO.getUserName());
        userVO.setWealth(userDTO.getWealth());
    }

}
