package com.xkr.web.controller.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xkr.common.CaptchaEnum;
import com.xkr.common.ErrorStatus;
import com.xkr.common.annotation.valid.Captcha;
import com.xkr.common.annotation.valid.IsNumberic;
import com.xkr.core.shiro.LoginAuthenticationToken;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.admin.account.AdminAccountDetailDTO;
import com.xkr.domain.dto.admin.account.ListAdminAccountDTO;
import com.xkr.domain.entity.XkrAdminAccount;
import com.xkr.service.AdminService;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.admin.account.AdminAccountDetailVO;
import com.xkr.web.model.vo.admin.account.AdminAccountVO;
import com.xkr.web.model.vo.admin.account.ListAdminAccountVO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.chris.redbud.validator.annotation.MethodValidate;
import org.chris.redbud.validator.result.ValidResult;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/29
 */
@RequestMapping("/api/admin/account")
@Controller
public class AccountController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private AdminService adminService;


    /**
     * 管理员登录
     *
     * @return
     */
    @MethodValidate
    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    @ResponseBody
    public BasicResult adminLogin(
            @NotBlank
            @RequestParam(name = "accountName") String accountName,
            @NotBlank
            @RequestParam(name = "accountToken") String accountToken,
            @Captcha(CaptchaEnum.LOGIN_TYPE)
            @RequestParam("captcha") String captcha,
            ValidResult result) {
        if (result.hasErrors()) {
            return new BasicResult<>(result);
        }
        try {
            LoginAuthenticationToken token = new LoginAuthenticationToken(accountName, accountToken, false);

            ResponseDTO<Boolean> responseDTO = adminService.adminLogin(token);

            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult<>(responseDTO.getStatus());
            }

            return new BasicResult<>(responseDTO.getData());
        } catch (Exception e) {
            logger.error("后台管理员登录异常 , accountName:{},accountToken:{}", accountName, accountToken, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 管理员登出
     *
     * @return
     */
    @RequestMapping(value = "/logout", method = {RequestMethod.POST})
    @ResponseBody
    public BasicResult adminLogout() {
        try {
            ResponseDTO<Boolean> responseDTO = adminService.adminLogout();

            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult<>(responseDTO.getStatus());
            }
            return new BasicResult<>(responseDTO.getData());
        } catch (Exception e) {
            logger.error("后台管理员登录异常", e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }


    /**
     * 获取管理员列表
     *
     * @param pageNum
     * @param size
     * @return
     */
    @RequestMapping(value = "/list", method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult getAccountList(
            @RequestParam(name = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        try {
            ListAdminAccountDTO listAdminAccountDTO = adminService.getListAdminAccountDTO(pageNum, size);

            if (!ErrorStatus.OK.equals(listAdminAccountDTO.getStatus())) {
                return new BasicResult(listAdminAccountDTO.getStatus());
            }

            ListAdminAccountVO listAdminAccountVO = new ListAdminAccountVO();

            buildListAdminAccountVO(listAdminAccountVO, listAdminAccountDTO);

            return new BasicResult<>(listAdminAccountVO);
        } catch (Exception e) {
            logger.error("后台获取管理员列表异常 ,pageNum:{},size:{}", pageNum, size, e);
        }
        return new BasicResult(ErrorStatus.ERROR);
    }

    /**
     * 管理员详情
     *
     * @return
     */
    @RequestMapping(value = "/detail", method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult getAccountDetail(
            @RequestParam(name = "adminAccountId",required = false) String adminAccountId) {
        try {
            if(StringUtils.isEmpty(adminAccountId)){
                //如果用户未登录则报参数异常
                Object obj = SecurityUtils.getSubject().getPrincipal();
                if(Objects.isNull(obj)){
                    return new BasicResult(ErrorStatus.PARAM_ERROR);
                }
                XkrAdminAccount xkrAdminAccount = (XkrAdminAccount)obj;

                //如果用户登录则默认去用户权限
                adminAccountId = String.valueOf(xkrAdminAccount.getId());
            }
            AdminAccountDetailDTO adminAccountDetailDTO = adminService.getAdminAccountDetailById(Long.valueOf(adminAccountId));

            if (!ErrorStatus.OK.equals(adminAccountDetailDTO.getStatus())) {
                return new BasicResult<>(adminAccountDetailDTO.getStatus());
            }

            AdminAccountDetailVO adminAccountDetailVO = new AdminAccountDetailVO();

            buildAdminAccountDetailVO(adminAccountDetailVO, adminAccountDetailDTO);

            return new BasicResult<>(adminAccountDetailVO);
        } catch (Exception e) {
            logger.error("后台获取管理员详情异常,adminAccountId:{}", adminAccountId, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 添加管理员操作
     *
     * @return
     */
    @MethodValidate
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    @ResponseBody
    public BasicResult addAdmin(
            @NotBlank
            @RequestParam(name = "accountName") String accountName,
            @NotBlank
            @RequestParam(name = "accountToken") String accountToken,
            @NotEmpty
            @RequestParam(name = "permissionIds[]") String[] permissionIds,
            @Email
            @RequestParam(name = "email") String email,
            ValidResult result) {
        if (result.hasErrors()) {
            return new BasicResult<>(result);
        }
        try {

            ResponseDTO<Long> responseDTO = adminService.saveNewAdminAccount(accountName, accountToken, email, permissionIds);

            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult<>(responseDTO.getStatus());
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("adminAccountId", String.valueOf(responseDTO.getData()));

            return new BasicResult<>(jsonObject);
        } catch (Exception e) {
            logger.error("后台添加管理员异常,accountName:{},accountToken:{},permissionIds:{},email:{}", accountName, accountToken, Arrays.toString(permissionIds), email, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 管理员更新
     *
     * @return
     */
    @MethodValidate
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    @ResponseBody
    public BasicResult updateAdmin(
            @IsNumberic
            @RequestParam(name = "adminAccountId") String adminAccountId,
            @NotBlank
            @RequestParam(name = "accountName") String accountName,
            @NotBlank
            @RequestParam(name = "accountToken") String accountToken,
            @NotEmpty
            @RequestParam(name = "permissionIds[]") String[] permissionIds,
            @Email
            @RequestParam(name = "email") String email,
            ValidResult result) {
        if (result.hasErrors()) {
            return new BasicResult<>(result);
        }
        try {

            ResponseDTO<Boolean> responseDTO = adminService.updateAdminAccountById(Long.valueOf(adminAccountId), accountName, accountToken, email, permissionIds);

            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult<>(responseDTO.getStatus());
            }

            return new BasicResult<>(responseDTO.getData());
        } catch (Exception e) {
            logger.error("后台管理员更新异常 , adminAccountId:{},accountName:{},accountToken:{},permissionIds:{},email:{}", adminAccountId, accountName, accountToken, Arrays.toString(permissionIds), email, e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    /**
     * 管理员删除
     *
     * @param adminAccountIds
     * @return
     */
    @RequestMapping(value = "/del", method = {RequestMethod.POST})
    @ResponseBody
    public BasicResult batchDelAccount(
            @RequestParam(name = "adminAccountIds[]") String[] adminAccountIds) {
        try {
            List<Long> ids = Arrays.stream(adminAccountIds).map(Long::valueOf).collect(Collectors.toList());

            ResponseDTO<Boolean> responseDTO = adminService.batchDeleteAdminAccount(ids);

            if (!ErrorStatus.OK.equals(responseDTO.getStatus())) {
                return new BasicResult<>(responseDTO.getStatus());
            }

            return new BasicResult<>(responseDTO.getData());
        } catch (Exception e) {
            logger.error("后台删除管理员,adminAccountIds:{}", JSON.toJSONString(adminAccountIds), e);
        }
        return new BasicResult<>(ErrorStatus.ERROR);
    }

    private void buildAdminAccountDetailVO(AdminAccountDetailVO adminAccountDetailVO, AdminAccountDetailDTO adminAccountDetailDTO) {
        adminAccountDetailVO.setAccountName(adminAccountDetailDTO.getAccountName());
        adminAccountDetailVO.setAdminAccountId(adminAccountDetailDTO.getAdminAccountId());
        adminAccountDetailVO.setEmail(adminAccountDetailDTO.getEmail());
        adminAccountDetailVO.setPermissionIds(adminAccountDetailDTO.getPermissionIds());
    }

    private void buildListAdminAccountVO(ListAdminAccountVO listAdminAccountVO, ListAdminAccountDTO listAdminAccountDTO) {
        listAdminAccountDTO.getList().forEach(adminAccountDTO -> {
            AdminAccountVO adminAccountVO = new AdminAccountVO();
            adminAccountVO.setAccountName(adminAccountDTO.getAccountName());
            adminAccountVO.setAdminAccountId(adminAccountDTO.getAdminAccountId());
            adminAccountVO.setClientIp(adminAccountDTO.getClientIp());
            adminAccountVO.setCreateDate(adminAccountDTO.getCreateDate());
            adminAccountVO.setEmail(adminAccountDTO.getEmail());
            adminAccountVO.setLastLoginDate(adminAccountDTO.getLastLoginDate());
            listAdminAccountVO.getList().add(adminAccountVO);
        });
        listAdminAccountVO.setTotalCount(listAdminAccountDTO.getTotalCount());
    }

}
