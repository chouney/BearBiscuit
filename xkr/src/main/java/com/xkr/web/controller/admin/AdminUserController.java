package com.xkr.web.controller.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xkr.common.ErrorStatus;
import com.xkr.common.PermissionEnum;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.user.ListUserDetailDTO;
import com.xkr.domain.dto.user.UserDTO;
import com.xkr.domain.dto.user.UserStatusEnum;
import com.xkr.service.UserService;
import com.xkr.util.DateUtil;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.user.ListUserDetailVO;
import com.xkr.web.model.vo.user.UserDetailVO;
import com.xkr.web.model.vo.user.UserVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.chris.redbud.validator.annotation.MethodValidate;
import org.chris.redbud.validator.result.ValidResult;
import org.chris.redbud.validator.validate.annotation.ContainsInt;
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
 * @date 2018/5/29
 */
@RequestMapping("/api/admin/user")
@Controller
public class AdminUserController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private UserService userService;

    /**
     * 用户搜索
     *
     * @return
     */
    @RequiresPermissions(PermissionEnum.Constant.USER_PERM)
    @RequestMapping(value = "/search", method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult<ListUserDetailVO> getUserListBySearchWord(
            @RequestParam(name = "createDate", required = false, defaultValue = "") String createDate,
            @RequestParam(name = "userName", required = false, defaultValue = "") String userName,
            @RequestParam(name = "status", required = false, defaultValue = "1") Integer status,
            @RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {

        try {
            Date sDate = Date.from(LocalDateTime.now().minusWeeks(1).toInstant(ZoneOffset.UTC));
            if (StringUtils.isNotEmpty(createDate)) {
                sDate = DateUtil.stringToDate(createDate, "yyyy-MM-dd");
            }

            ListUserDetailDTO userDetailDTOs = userService.searchUserByAdmin(userName, sDate, UserStatusEnum.getByCode(status), pageNum, size);

            if (!ErrorStatus.OK.equals(userDetailDTOs.getStatus())) {
                return new BasicResult<>(userDetailDTOs.getStatus());
            }

            ListUserDetailVO listUserDetailVOs = new ListUserDetailVO();

            buildListResourceVO(listUserDetailVOs, userDetailDTOs);

            return new BasicResult<>(listUserDetailVOs);
        } catch (Exception e) {
            logger.error("后台获取搜索用户异常,createDate:{},userName:{},status:{},pageNum:{},size:{}", createDate, userName, status, pageNum, size, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 用户批量操作
     *
     * @return
     */
    @RequiresPermissions(PermissionEnum.Constant.USER_PERM)
    @MethodValidate
    @RequestMapping(value = "/opt", method = {RequestMethod.POST})
    @ResponseBody
    public BasicResult batchOptUser(
            @RequestParam(name = "userIds[]") String[] userIds,
            @ContainsInt({1, 3, -1})
            @RequestParam(name = "optType") Integer optType,
            ValidResult result) {
        if (result.hasErrors()) {
            return new BasicResult<>(result);
        }
        try {
            List<Long> ids = Arrays.stream(userIds).map(Long::valueOf).collect(Collectors.toList());

            ResponseDTO<Boolean> responseDTO = userService.batchUpdateUserStatus(ids, UserStatusEnum.getByCode(optType));

            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult<>(responseDTO.getStatus());
            }

            return new BasicResult<>(responseDTO.getData());
        } catch (Exception e) {
            logger.error("后台批量操作用户异常,userIds:{},optType:{}", JSON.toJSONString(userIds), optType, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }


    private void buildListResourceVO(ListUserDetailVO userDetailVOs, ListUserDetailDTO listUserDetailDTO) {
        listUserDetailDTO.getUserList().forEach(userDetailDTO -> {
            UserDetailVO userDetailVO = new UserDetailVO();
            userDetailVO.setClientIp(userDetailDTO.getClientIp());
            userDetailVO.setEmail(userDetailDTO.getEmail());
            userDetailVO.setLastLoginDate(userDetailDTO.getLastLoginDate());
            userDetailVO.setStatus(userDetailDTO.getStatus());
            userDetailVO.setTotalRecharge(userDetailDTO.getTotalRecharge());
            userDetailVO.setUserId(userDetailDTO.getUserId());
            userDetailVO.setUserName(userDetailDTO.getUserName());
            userDetailVO.setUserToken(userDetailDTO.getUserToken());
            userDetailVO.setWealth(userDetailDTO.getWealth());
            userDetailVOs.getUserList().add(userDetailVO);
        });
        userDetailVOs.setTotalCount(listUserDetailDTO.getTotalCount());
    }

}
