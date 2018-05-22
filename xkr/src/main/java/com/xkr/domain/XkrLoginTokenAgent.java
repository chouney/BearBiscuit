package com.xkr.domain;

import com.google.common.collect.Lists;
import com.xkr.common.Const;
import com.xkr.common.LoginEnum;
import com.xkr.core.IdGenerator;
import com.xkr.dao.mapper.XkrLoginTokenMapper;
import com.xkr.domain.entity.XkrLoginToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/2
 */
@Service
public class XkrLoginTokenAgent {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XkrLoginTokenMapper xkrLoginTokenMapper;

    @Autowired
    private IdGenerator idGenerator;

    public void recordLogin(Long userId,LoginEnum loginEnum){
        Session session = SecurityUtils.getSubject().getSession();
        if(Objects.isNull(userId)){
            return;
        }
        //存储登录类型key
        session.setAttribute(Const.SESSION_LOGIN_TYPE_KEY, loginEnum.toString());
        XkrLoginToken xkrLoginToken = new XkrLoginToken();
        xkrLoginToken.setUserId(userId);
        xkrLoginToken.setStatus((byte) 1);
        xkrLoginToken = xkrLoginTokenMapper.selectOne(xkrLoginToken);
        if(Objects.isNull(xkrLoginToken)){
            xkrLoginToken = new XkrLoginToken();
            xkrLoginToken.setId(idGenerator.generateId());
            xkrLoginToken.setLoginCount(1);
            xkrLoginToken.setClientIp(session.getHost());
            xkrLoginToken.setUserId(userId);
            xkrLoginToken.setStatus((byte) 1);
            xkrLoginToken.setLoginToken((String) session.getAttribute(Const.SESSION_COOKIE_NAME));
            //存储token
            session.setAttribute(Const.SESSION_LOGIN_TOKEN_KEY,xkrLoginToken);
            xkrLoginTokenMapper.insert(xkrLoginToken);
            return ;
        }
        xkrLoginToken.setClientIp(session.getHost());
        Integer count = xkrLoginToken.getLoginCount() == null ? 0 : xkrLoginToken.getLoginCount();
        xkrLoginToken.setLoginCount(count+1);
        xkrLoginToken.setUpdateTime(new Date());
        xkrLoginToken.setLoginToken((String) session.getAttribute(Const.SESSION_COOKIE_NAME));
        //存储token
        session.setAttribute(Const.SESSION_LOGIN_TOKEN_KEY,xkrLoginToken);
        xkrLoginTokenMapper.updateByPrimaryKey(xkrLoginToken);
    }

    public XkrLoginToken getUserLoginRecordById(Long userId){
        if(Objects.isNull(userId)){
            return null;
        }
        XkrLoginToken xkrLoginToken = new XkrLoginToken();
        xkrLoginToken.setUserId(userId);
        xkrLoginToken.setStatus((byte) 1);
        return xkrLoginTokenMapper.selectOne(xkrLoginToken);
    }

    public List<XkrLoginToken> getUserLoginRecordByIds(List<Long> userId){
        List<XkrLoginToken> result = Lists.newArrayList();
        if(CollectionUtils.isEmpty(userId)){
            return result;
        }
        result = xkrLoginTokenMapper.getLoginTokensByIds(userId);
        return result;
    }
}
