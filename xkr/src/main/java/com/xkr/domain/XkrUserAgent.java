package com.xkr.domain;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.xkr.common.UserStatusEnum;
import com.xkr.core.IdGenerator;
import com.xkr.dao.mapper.XkrUserMapper;
import com.xkr.domain.dto.search.ResourceIndexDTO;
import com.xkr.domain.dto.search.UserIndexDTO;
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

    public static final int USER_STATUS_NORMAL = 1;

    public static final int USER_STATUS_UNVERIFY = 2;

    public static final int USER_STATUS_FREEZED = 3;


    public List<XkrUser> getUserByIds(List<Long> userIds,Integer status){
        List<XkrUser> list = Lists.newArrayList();
        if(CollectionUtils.isEmpty(userIds)){
            return list;
        }
        Map<String,Object> params = ImmutableMap.of(
                "ids",userIds,
                "status",status
        );
        return xkrUserMapper.getUserByIds(params);
    }

    public List<XkrUser> getUserByIds(List<Long> userIds){
        List<XkrUser> list = Lists.newArrayList();
        if(CollectionUtils.isEmpty(userIds)){
            return list;
        }
        Map<String,Object> params = ImmutableMap.of(
                "ids",userIds
        );
        return xkrUserMapper.getUserByIds(params);
    }

    public XkrUser getUserById(Long userId){
        if(Objects.isNull(userId)){
            return null;
        }
        return xkrUserMapper.selectByPrimaryKey(userId);
    }

    public XkrUser getUserByNameOrEmail(String userLogin){
        if(StringUtils.isEmpty(userLogin)){
            return null;
        }
        XkrUser user = xkrUserMapper.selectByUserName(userLogin);
        if(Objects.isNull(user)){
            user = xkrUserMapper.selectByEmail(userLogin);
        }
        return user;
    }

    public boolean verifyUserAccountByUserId(Long userId){
        if(Objects.isNull(userId)){
            return false;
        }
        XkrUser user = new XkrUser();
        user.setId(userId);
        user.setStatus((byte)USER_STATUS_NORMAL);
        if(xkrUserMapper.updateByPrimaryKey(user) == 1){
            UserIndexDTO userIndexDTO = new UserIndexDTO();
            searchApiService.getAndBuildIndexDTOByIndexId(userIndexDTO,String.valueOf(user.getId()));
            userIndexDTO.setStatus(USER_STATUS_NORMAL);
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
        user.setStatus(UserStatusEnum.UNAUTHORIZED.getCode());
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
        return xkrUserMapper.updateByPrimaryKey(user) == 1;
    }

    private void buildUserIndexDTO(UserIndexDTO userIndexDTO,XkrUser xkrUser){
        userIndexDTO.setUserId(xkrUser.getId());
        userIndexDTO.setEmail(xkrUser.getEmail());
        userIndexDTO.setStatus(xkrUser.getStatus());
        userIndexDTO.setUserName(xkrUser.getUserName());
        userIndexDTO.setCreateTime(xkrUser.getCreateTime());
    }

}
