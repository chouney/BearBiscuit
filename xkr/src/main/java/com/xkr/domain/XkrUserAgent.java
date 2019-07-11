package com.xkr.domain;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xkr.core.IdGenerator;
import com.xkr.dao.mapper.XkrUserMapper;
import com.xkr.domain.dto.search.UserIndexDTO;
import com.xkr.domain.dto.user.UserStatusEnum;
import com.xkr.domain.entity.XkrUser;
import com.xkr.service.api.SearchApiService;
import com.xkr.util.IdUtil;
import com.xkr.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/2
 */
@Service
public class XkrUserAgent {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XkrUserMapper xkrUserMapper;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private SearchApiService searchApiService;

    public List<XkrUser> searchByFilter(String userLogin, Date createDate,
                                        int status){
        Map<String,Object> params = Maps.newHashMap();
        params.put("userName",userLogin);
        params.put("createDate",createDate);
        params.put("status",status);
        return xkrUserMapper.searchByFilter(params);
    }

    public boolean batchUpdateUserByIds(List<Long> userIds,UserStatusEnum status){
        if(CollectionUtils.isEmpty(userIds) || Objects.isNull(status)){
            return false;
        }
        boolean isSuccess = false;
        if(UserStatusEnum.TOUPDATE_STATUSED.contains(status)){
            isSuccess = xkrUserMapper.batchUpdateUserByIds(ImmutableMap.of(
                    "list",userIds,"status",status.getCode()
            )) > 0;
        }
        if(isSuccess){
            if (!searchApiService.bulkUpdateIndexStatus("user", userIds, status.getCode())) {
                logger.error("XkrUserAgent batchUpdateUserByIds failed ,userIds:{},status:{}", JSON.toJSONString(userIds),status);
            }
        }
        return isSuccess;
    }

    public List<XkrUser> getUserByIds(List<Long> userIds,List<UserStatusEnum> statuses){
        List<XkrUser> list = Lists.newArrayList();
        if(CollectionUtils.isEmpty(userIds)){
            return list;
        }
        Map<String,Object> params = ImmutableMap.of(
                "ids",userIds,
                "statuses",statuses.stream().map(UserStatusEnum::getCode).collect(Collectors.toList())
        );
        return xkrUserMapper.getUserByIds(params);
    }

    public List<XkrUser> getUserByIds(List<Long> userIds){
        List<XkrUser> list = Lists.newArrayList();
        if(CollectionUtils.isEmpty(userIds)){
            return list;
        }
        Map<String,Object> params = ImmutableMap.of(
                "ids",userIds,
                "statuses",UserStatusEnum.NON_DELETE_STATUSED.stream().map(UserStatusEnum::getCode).collect(Collectors.toList())
        );
        return xkrUserMapper.getUserByIds(params);
    }

    public XkrUser getUserById(Long userId,List<UserStatusEnum> statuses){
        if(Objects.isNull(userId)){
            return null;
        }
        return xkrUserMapper.getUserById(ImmutableMap.of(
                "id",userId,"statuses",statuses.stream().map(UserStatusEnum::getCode).collect(Collectors.toList())
        ));
    }

    public Integer getUserTotalCount(){
        return xkrUserMapper.getTotalUser(ImmutableMap.of(
                "statuses",UserStatusEnum.NON_DELETE_STATUSED.stream().map(UserStatusEnum::getCode).collect(Collectors.toList())
        ));
    }

    public Integer getUserTotalCountByStatues(List<UserStatusEnum> userStatusEnums){
        return xkrUserMapper.getTotalUser(ImmutableMap.of(
                "statuses",userStatusEnums.stream().map(UserStatusEnum::getCode).collect(Collectors.toList())
        ));
    }

    public XkrUser getUserById(Long userId){
        if(Objects.isNull(userId)){
            return null;
        }
        return xkrUserMapper.getUserById(ImmutableMap.of(
                "id",userId,"statuses",UserStatusEnum.NON_DELETE_STATUSED.stream().map(UserStatusEnum::getCode).collect(Collectors.toList())
        ));
    }

    public XkrUser getUserByNameOrEmail(String userLogin){
        if(StringUtils.isEmpty(userLogin)){
            return null;
        }
        List<Integer> statusesCode = UserStatusEnum.NON_DELETE_STATUSED.stream().map(UserStatusEnum::getCode).collect(Collectors.toList());
        XkrUser user = xkrUserMapper.selectByUserName(ImmutableMap.of(
                "userLogin",userLogin,"statuses",statusesCode
        ));
//        if(Objects.isNull(user)){
//            user = xkrUserMapper.selectByEmail(ImmutableMap.of(
//                    "userLogin",userLogin,"statuses",statusesCode
//            ));
//        }
        return user;
    }

    public boolean verifyUserAccountByUserId(Long userId){
        if(Objects.isNull(userId)){
            return false;
        }
        XkrUser user = new XkrUser();
        user.setId(userId);
        user.setStatus((byte)UserStatusEnum.USER_STATUS_NORMAL.getCode());
        if(xkrUserMapper.updateByPrimaryKeySelective(user) == 1){
            UserIndexDTO userIndexDTO = new UserIndexDTO();
            searchApiService.getAndBuildIndexDTOByIndexId(userIndexDTO,String.valueOf(user.getId()));
            userIndexDTO.setStatus(UserStatusEnum.USER_STATUS_NORMAL.getCode());
            if(!searchApiService.upsertIndex(userIndexDTO)){
                logger.error("XkrUserAgent verifyUserAccountByUserId failed ,userId:{}",userId);
            }
            return true;
        }
        return false;
    }

    public boolean updateUserTokenByUser(XkrUser user,String userToken){
        if(StringUtils.isEmpty(userToken) || Objects.isNull(user)){
            return false;
        }
        user.setUserToken(PasswordUtil.createUserPwd(userToken,user.getSalt()));
        user.setUpdateTime(new Date());
        return xkrUserMapper.updateByPrimaryKeySelective(user) == 1;
    }

    public XkrUser createUserAccount(String userName,String userToken, String email){
        if(StringUtils.isEmpty(userName) ||
                StringUtils.isEmpty(userToken) ||
                StringUtils.isEmpty(email)){
            return null;
        }
        String salt = IdUtil.uuid();
        XkrUser user = new XkrUser();
        user.setId(idGenerator.generateId());
        user.setUserName(userName);
        user.setUserToken(PasswordUtil.createUserPwd(userToken,salt));
        user.setSalt(salt);
        user.setEmail(email);
        user.setTotalRecharge(0L);
        user.setWealth(0L);
        user.setCreateTime(new Date());
        user.setStatus((byte)UserStatusEnum.USER_STATUS_UNVERIFIED.getCode());
        if(xkrUserMapper.insertSelective(user) == 1){
            //创建用户索引
            UserIndexDTO userIndexDTO = new UserIndexDTO();

            buildUserIndexDTO(userIndexDTO,user);

            if(!searchApiService.upsertIndex(userIndexDTO)){
                logger.error("XkrUserAgent createUserAccount createIndex failed user:{}", JSON.toJSONString(user));
            }
            return user;
        }
        return null;
    }

    public boolean dealUserPurchase(XkrUser user,Integer toTakeOff){
        if(Objects.isNull(user)){
            return false;
        }
        if(user.getWealth() - toTakeOff < 0){
            return false;
        }
        user.setWealth(user.getWealth() - toTakeOff);
        return xkrUserMapper.updateByPrimaryKeySelective(user) == 1;
    }

    private void buildUserIndexDTO(UserIndexDTO userIndexDTO,XkrUser xkrUser){
        userIndexDTO.setUserId(xkrUser.getId());
        userIndexDTO.setEmail(xkrUser.getEmail());
        userIndexDTO.setStatus(xkrUser.getStatus());
        userIndexDTO.setUserName(xkrUser.getUserName());
        userIndexDTO.setCreateTime(xkrUser.getCreateTime());
    }

}
