package com.xkr.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.ImmutableList;
import com.xkr.common.*;
import com.xkr.common.annotation.OptLog;
import com.xkr.core.shiro.LoginAuthenticationToken;
import com.xkr.dao.cache.AdminIndexRedisService;
import com.xkr.domain.*;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.admin.account.AdminAccountDTO;
import com.xkr.domain.dto.admin.account.AdminAccountDetailDTO;
import com.xkr.domain.dto.admin.account.ListAdminAccountDTO;
import com.xkr.domain.dto.admin.index.AdminIndexDTO;
import com.xkr.domain.dto.admin.index.ResourceAccountDTO;
import com.xkr.domain.dto.admin.index.UserAccountDTO;
import com.xkr.domain.dto.admin.permission.ListPermissionDTO;
import com.xkr.domain.dto.admin.permission.PermissionDTO;
import com.xkr.domain.dto.resource.ResourceStatusEnum;
import com.xkr.domain.dto.user.UserStatusEnum;
import com.xkr.domain.entity.XkrAdminAccount;
import com.xkr.domain.entity.XkrLoginToken;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@Service
public class AdminService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XkrResourceAgent xkrResourceAgent;

    @Autowired
    private XkrAdminAccountAgent xkrAdminAccountAgent;

    @Autowired
    private XkrLoginTokenAgent xkrLoginTokenAgent;

    @Autowired
    private AdminIndexRedisService adminIndexRedisService;

    @Autowired
    private XkrUserAgent xkrUserAgent;


    /**
     * ——————————————————————管理员————————————————————
     */

    /**
     * 登录
     * @param token
     * @return
     */
    public ResponseDTO<Boolean> adminLogin(LoginAuthenticationToken token) {
        ResponseDTO<Boolean> result = new ResponseDTO<>();
        token.setLoginType(LoginEnum.ADMIN.toString());
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
            XkrAdminAccount user = (XkrAdminAccount) currentUser.getPrincipal();
            xkrLoginTokenAgent.recordLogin(user.getId(), LoginEnum.ADMIN);
            return new ResponseDTO<>(true);
        }
        token.clear();
        return new ResponseDTO<>(ErrorStatus.USER_INCORRECT_LOGIN);
    }

    /**
     * 登出
     * @return
     */
    public ResponseDTO<Boolean> adminLogout(){
        SecurityUtils.getSubject().logout();
        return new ResponseDTO<>(true);
    }

    /**
     * 获取管理员信息
     *
     * @param adminAccountId
     * @return
     */
    public AdminAccountDetailDTO getAdminAccountDetailById(Long adminAccountId) {
        AdminAccountDetailDTO result = new AdminAccountDetailDTO();
        if (Objects.isNull(adminAccountId)) {
            result.setStatus(ErrorStatus.PARAM_ERROR);
            return result;
        }
        XkrAdminAccount adminAccount = xkrAdminAccountAgent.getById(adminAccountId);
        if (Objects.isNull(adminAccount)) {
            result.setStatus(ErrorStatus.ERROR);
            return result;
        }

        buildAdminAccountDetailDTO(result, adminAccount);

        return result;
    }

    /**
     * 添加管理员
     *
     * @param accountName
     * @param accountToken
     * @param email
     * @param permissionIds
     * @return
     */
    @OptLog(moduleEnum = OptLogModuleEnum.ADMIN,optEnum = OptEnum.INSERT)
    public ResponseDTO<Long> saveNewAdminAccount(String accountName, String accountToken, String email, String[] permissionIds) {
        if (StringUtils.isEmpty(accountName) || StringUtils.isEmpty(accountToken)
                || StringUtils.isEmpty(email) ) {
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        if(ArrayUtils.isEmpty(permissionIds)){
            permissionIds = new String[]{};
        }
        String strPermissionIds = String.join(";",permissionIds);
//        if(StringUtils.isEmpty(strPermissionIds)){
//            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
//        }
        XkrAdminAccount xkrAdminAccount = xkrAdminAccountAgent.getAdminAccountByName(accountName);
        if(Objects.nonNull(xkrAdminAccount)){
            return new ResponseDTO<>(ErrorStatus.USER_NAME_ALREADY_EXIST);
        }
        XkrAdminAccount newAccount = xkrAdminAccountAgent.saveNewAdminAccount(accountName, accountToken, email, strPermissionIds);
        if (Objects.isNull(newAccount)) {
            return new ResponseDTO<>(ErrorStatus.ERROR);
        }
        return new ResponseDTO<>(newAccount.getId());
    }

    /**
     * 更新管理员信息
     *
     * @param adminAccountId
     * @param accountName
     * @param accountToken
     * @param email
     * @param permissionIds
     * @return
     */
    @OptLog(moduleEnum = OptLogModuleEnum.ADMIN,optEnum = OptEnum.UPDATE)
    public ResponseDTO<Boolean> updateAdminAccountById(Long adminAccountId, String accountName, String accountToken, String email, String[] permissionIds) {
        if (Objects.isNull(adminAccountId) || StringUtils.isEmpty(accountName) || StringUtils.isEmpty(accountToken)
                || StringUtils.isEmpty(email)) {
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        if(ArrayUtils.isEmpty(permissionIds)){
            permissionIds = new String[]{};
        }
        String strPermissionIds = String.join(";",permissionIds);
//        if(StringUtils.isEmpty(strPermissionIds)){
//            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
//        }
        XkrAdminAccount xkrAdminAccount = xkrAdminAccountAgent.getAdminAccountByName(accountName);
        if(Objects.nonNull(xkrAdminAccount) && !xkrAdminAccount.getId().equals(adminAccountId)){
            return new ResponseDTO<>(ErrorStatus.USER_NAME_ALREADY_EXIST);
        }
        return new ResponseDTO<>(xkrAdminAccountAgent.updateAdminAccountById(adminAccountId, accountName, accountToken, email, strPermissionIds));
    }

    /**
     * 获取管理员列表
     *
     * @param pageNum
     * @param size
     * @return
     */
    public ListAdminAccountDTO getListAdminAccountDTO(int pageNum, int size) {
        ListAdminAccountDTO result = new ListAdminAccountDTO();
        pageNum = pageNum < 1 ? 1 : pageNum;
        size = size < 1 ? 10 : size;
        Page page = PageHelper.startPage(pageNum, size, "update_time desc");
        List<XkrAdminAccount> adminAccounts = xkrAdminAccountAgent.selectList();
        result.setTotalCount((int) page.getTotal());

        List<Long> adminIds = adminAccounts.stream().map(XkrAdminAccount::getId).collect(Collectors.toList());

        List<XkrLoginToken> tokens = xkrLoginTokenAgent.getUserLoginRecordByIds(adminIds);

        buildListAdminAccountDTO(result, adminAccounts, tokens);

        return result;

    }

    /**
     * 批量删除管理员
     *
     * @param accountIds
     * @return
     */
    @OptLog(moduleEnum = OptLogModuleEnum.ADMIN,optEnum = OptEnum.DELETE)
    public ResponseDTO<Boolean> batchDeleteAdminAccount(List<Long> accountIds) {
        if (CollectionUtils.isEmpty(accountIds)) {
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        return new ResponseDTO<>(xkrAdminAccountAgent.batchUpdateAdminAccountByIds(accountIds, XkrAdminAccountAgent.STATUS_DELETED));

    }
    /**
     * ——————————————————————首页————————————————————————
     */

    /**
     * 后台首页
     *
     * @return
     */
    public AdminIndexDTO getAdminIndexDTO() {
        AdminIndexDTO indexDTO = new AdminIndexDTO();

        buildAdminIndexDTO(indexDTO);

        return indexDTO;
    }

    /**
     * ——————————————————————角色——————————————————————
     */

//    public ListAdminRoleDTO getListAdminRoleDTO(int pageNum, int size) {
//        ListAdminRoleDTO result = new ListAdminRoleDTO();
//        pageNum = pageNum < 1 ? 1 : pageNum;
//        size = size < 1 ? 10 : size;
//        Page page = PageHelper.startPage(pageNum, size, "update_time desc");
//        List<XkrAdminRole> adminRoles = xkrAdminRoleAgent.selectList();
//
//        result.setTotalCount((int) page.getTotal());
//
//        buildListAdminRoleDTO(result, adminRoles);
//
//        return result;
//    }

//    /**
//     * 获取角色详情
//     *
//     * @param roleId
//     * @return
//     */
//    public RoleDetailDTO getRoleDetailByRoleId(Integer roleId) {
//        RoleDetailDTO result = new RoleDetailDTO();
//        if (Objects.isNull(roleId)) {
//            result.setStatus(ErrorStatus.PARAM_ERROR);
//            return result;
//        }
//        XkrAdminRole adminRole = xkrAdminRoleAgent.getAdminRoleById(roleId);
//        if (Objects.isNull(adminRole)) {
//            result.setStatus(ErrorStatus.ERROR);
//            return result;
//        }
//        JSONObject ext = JSON.parseObject(adminRole.getExt());
//        List<Integer> pIds = Arrays.stream(adminRole.getPermissionIds().split(";")).map(Integer::valueOf).collect(Collectors.toList());
//
//        result.setRoleName(adminRole.getRoleName());
//        result.setRoleDetail(ext.getString(XkrAdminRoleAgent.ROLE_DETAIL_EXT_KEY));
//        result.setPermissionIds(pIds);
//
//        return result;
//    }

//    /**
//     * 添加角色
//     *
//     * @param roleName
//     * @param roleDetail
//     * @param permissionIds
//     * @return
//     */
//    @OptLog(moduleEnum = OptLogModuleEnum.ADMIN,optEnum = OptEnum.INSERT)
//    public ResponseDTO<Integer> saveNewRole(String roleName, String roleDetail, List<Integer> permissionIds) {
//        if (StringUtils.isEmpty(roleDetail) || StringUtils.isEmpty(roleName) || CollectionUtils.isEmpty(permissionIds)) {
//            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
//        }
//        XkrAdminRole adminRole = xkrAdminRoleAgent.saveNewAdminRole(roleName, roleDetail, permissionIds);
//        if (Objects.isNull(adminRole)) {
//            return new ResponseDTO<>(ErrorStatus.ERROR);
//        }
//        return new ResponseDTO<>(adminRole.getId());
//    }

//    /**
//     * 更新角色信息
//     *
//     * @param roleId
//     * @param roleName
//     * @param roleDetail
//     * @param permissionIds
//     * @return
//     */
//    @OptLog(moduleEnum = OptLogModuleEnum.ADMIN,optEnum = OptEnum.UPDATE)
//    public ResponseDTO<Boolean> updateRoleById(Integer roleId, String roleName, String roleDetail, List<Integer> permissionIds) {
//        if (Objects.isNull(roleDetail) || StringUtils.isEmpty(roleDetail) || StringUtils.isEmpty(roleName) || CollectionUtils.isEmpty(permissionIds)) {
//            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
//        }
//        return new ResponseDTO<>(xkrAdminRoleAgent.updateRoleById(roleId, roleName, roleDetail, permissionIds));
//    }

//    /**
//     * 批量删除角色
//     *
//     * @param roleIds
//     * @return
//     */
//    @OptLog(moduleEnum = OptLogModuleEnum.ADMIN,optEnum = OptEnum.DELETE)
//    public ResponseDTO<Boolean> batchDeleteRoleById(List<Integer> roleIds) {
//        if (CollectionUtils.isEmpty(roleIds)) {
//            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
//        }
//
//        List<XkrAdminAccount> blockedAccount = Lists.newArrayList();
//        //确认没有管理员拥有该角色
//        roleIds.forEach(id ->
//                blockedAccount.addAll(xkrAdminAccountAgent.getAdminAccountByRoleId(id)));
//        if (!CollectionUtils.isEmpty(blockedAccount)) {
//            return new ResponseDTO<>(ErrorStatus.ADMIN_EXIST_HOLDING_ROLE_ACCOUNT);
//        }
//
//        return new ResponseDTO<>(xkrAdminRoleAgent.batchUpdateRoleByIds(roleIds, XkrAdminRoleAgent.STATUS_DELETED));
//    }

    /**
     * ——————————————————权限——————————————————————
     */

    public ListPermissionDTO getListPermissionDTO() {
        ListPermissionDTO result = new ListPermissionDTO();
        List<PermissionDTO> permissions = Arrays.stream(PermissionEnum.values()).map(permissionEnum -> {
            PermissionDTO permissionDto = new PermissionDTO();
            permissionDto.setPermissionName(permissionEnum.getPermissionName());
            permissionDto.setPermissionId(permissionEnum.getPermissionId());
            return permissionDto;
        }).collect(Collectors.toList());

        result.setList(permissions);

        result.setTotalCount(permissions.size());

        return result;
    }


    private void buildListAdminAccountDTO(ListAdminAccountDTO adminAccountDTOs, List<XkrAdminAccount> accounts, List<XkrLoginToken> loginTokens) {
        accounts.forEach(adminAccount -> {
            AdminAccountDTO adminAccountDTO = new AdminAccountDTO();
            XkrLoginToken currentToken = loginTokens.stream().filter(loginToken -> loginToken.getUserId().equals(adminAccount.getId())).findFirst().orElse(null);
            adminAccountDTO.setAccountName(adminAccount.getAccountName());
            adminAccountDTO.setAdminAccountId(adminAccount.getId());
            adminAccountDTO.setCreateDate(adminAccount.getCreateTime());
            adminAccountDTO.setEmail(adminAccount.getEmail());
            if (Objects.nonNull(currentToken)) {
                adminAccountDTO.setClientIp(currentToken.getClientIp());
                adminAccountDTO.setLastLoginDate(currentToken.getUpdateTime());
            }
            adminAccountDTOs.getList().add(adminAccountDTO);
        });
    }

    private void buildAdminIndexDTO(AdminIndexDTO indexDTO) {
        Subject subject = SecurityUtils.getSubject();
        if (Objects.isNull(subject.getPrincipal())) {
            indexDTO.setStatus(ErrorStatus.NOT_FOUND);
            return;
        }
        Session session = subject.getSession();

        XkrAdminAccount adminAccount = (XkrAdminAccount) subject.getPrincipal();

        UserAccountDTO userAccountDTO = new UserAccountDTO();

        ResourceAccountDTO resourceAccountDTO = new ResourceAccountDTO();
        ResourceAccountDTO designAccountDTO = new ResourceAccountDTO();

        buildUserAccountDTO(userAccountDTO);

        buildResourceAccountDTO(XkrClassAgent.ROOT_RESOURCE_CLASS_ID,resourceAccountDTO);

        buildResourceAccountDTO(XkrClassAgent.ROOT_DESIGN_CLASS_ID,designAccountDTO);

        indexDTO.setAccountName(adminAccount.getAccountName());
        indexDTO.setLastLoginDate(session.getLastAccessTime());

        indexDTO.setUserAccountDTO(userAccountDTO);

        indexDTO.setResoureAccountDTO(resourceAccountDTO);

        indexDTO.setDesignAccountDTO(designAccountDTO);

    }

    private void buildUserAccountDTO(UserAccountDTO userAccountDTO){
        Integer userTotal = xkrUserAgent.getUserTotalCount();
        Integer userActiveTotal = xkrUserAgent.getUserTotalCountByStatues(ImmutableList.of(UserStatusEnum.USER_STATUS_NORMAL));

        userAccountDTO.setLoginCount(adminIndexRedisService.getLoginCount());
        userAccountDTO.setRegCount(adminIndexRedisService.getRegCount());
        userAccountDTO.setUserActiveTotalCount(userActiveTotal);
        userAccountDTO.setUserTotalCount(userTotal);
    }

    private void buildResourceAccountDTO(int classId, ResourceAccountDTO resourceAccountDTO){
        Integer totalCount = xkrResourceAgent.getResourceTotal(ResourceStatusEnum.NON_DELETE_STATUSED,classId);
        Integer unverifiedCount = xkrResourceAgent.getResourceTotal(ImmutableList.of(ResourceStatusEnum.STATUS_UNVERIFIED),classId);

        resourceAccountDTO.setDownloadCount(adminIndexRedisService.getDownLoadCount(classId));
        resourceAccountDTO.setUploadCount(adminIndexRedisService.getUploadCount(classId));
        resourceAccountDTO.setTotalCount(totalCount);
        resourceAccountDTO.setUnverifiedCount(unverifiedCount);
    }

    private void buildAdminAccountDetailDTO(AdminAccountDetailDTO result, XkrAdminAccount adminAccount) {
        result.setAccountName(adminAccount.getAccountName());
        result.setAdminAccountId(adminAccount.getId());
        result.setEmail(adminAccount.getEmail());
        if(!StringUtils.isEmpty(adminAccount.getPermissionIds())){
            result.setPermissionIds(adminAccount.getPermissionIds().split(";"));
        }
    }


}
