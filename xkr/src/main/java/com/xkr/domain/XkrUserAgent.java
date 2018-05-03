package com.xkr.domain;

import com.xkr.common.UserStatusEnum;
import com.xkr.core.IdGenerator;
import com.xkr.dao.mapper.XkrUserMapper;
import com.xkr.domain.entity.XkrAdminRole;
import com.xkr.domain.entity.XkrUser;
import com.xkr.util.PasswordUtil;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/2
 */
@Service
public class XkrUserAgent {

    @Autowired
    private XkrUserMapper xkrUserMapper;

    @Autowired
    private IdGenerator idGenerator;

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

}
