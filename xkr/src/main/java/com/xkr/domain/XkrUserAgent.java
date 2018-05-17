package com.xkr.domain;

import com.xkr.common.UserStatusEnum;
import com.xkr.core.IdGenerator;
import com.xkr.dao.mapper.XkrUserMapper;
import com.xkr.domain.dto.search.ResourceIndexDTO;
import com.xkr.domain.dto.search.UserIndexDTO;
import com.xkr.domain.entity.XkrUser;
import com.xkr.service.api.SearchApiService;
import com.xkr.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    public boolean createUserAccount(String userName,String userToken,
                                     String salt,String email){
        if(StringUtils.isEmpty(userName) ||
                StringUtils.isEmpty(userToken) ||
                StringUtils.isEmpty(salt) ||
                StringUtils.isEmpty(email)){
            return false;
        }
        XkrUser user = new XkrUser();
        user.setId(idGenerator.generateId());
        user.setUserName(userName);
        user.setUserToken(PasswordUtil.createUserPwd(userToken,salt));
        user.setSalt(salt);
        user.setEmail(email);
        user.setTotalRecharge(0L);
        user.setWealth(0L);
        user.setStatus(UserStatusEnum.UNAUTHORIZED.getCode());
        return xkrUserMapper.insertSelective(user) == 1;
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

}
