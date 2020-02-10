package com.xkr.domain;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.xkr.common.Const;
import com.xkr.common.LoginEnum;
import com.xkr.core.IdGenerator;
import com.xkr.dao.mapper.XkrLoginTokenMapper;
import com.xkr.domain.entity.XkrLoginToken;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
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

        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;


        //存储登录类型key
        session.setAttribute(Const.SESSION_LOGIN_TYPE_KEY, loginEnum.getType());


        XkrLoginToken xkrLoginToken = new XkrLoginToken();
        xkrLoginToken.setUserId(userId);
        xkrLoginToken.setStatus((byte) 1);
        xkrLoginToken = xkrLoginTokenMapper.selectOne(xkrLoginToken);
        logger.info("GetClientIp------------------------->" + getClientIp(sra.getRequest()));
        if(Objects.isNull(xkrLoginToken)){
            xkrLoginToken = new XkrLoginToken();
            xkrLoginToken.setId(idGenerator.generateId());
            xkrLoginToken.setLoginCount(1);
            logger.info("GetClientIp------------------------->" + getClientIp(sra.getRequest()));
            xkrLoginToken.setClientIp(getClientIp(sra.getRequest()));
            xkrLoginToken.setUserId(userId);
            xkrLoginToken.setStatus((byte) 1);
            xkrLoginToken.setLoginToken(String.valueOf(session.getId()));
            //存储token
            session.setAttribute(Const.SESSION_LOGIN_TOKEN_KEY, JSON.toJSONString(xkrLoginToken));
            xkrLoginTokenMapper.insertSelective(xkrLoginToken);
            return ;
        }
        xkrLoginToken.setClientIp(session.getHost());
        Integer count = xkrLoginToken.getLoginCount() == null ? 0 : xkrLoginToken.getLoginCount();
        xkrLoginToken.setLoginCount(count+1);
        xkrLoginToken.setUpdateTime(new Date());
        xkrLoginToken.setLoginToken(String.valueOf(session.getId()));
        //存储token
        session.setAttribute(Const.SESSION_LOGIN_TOKEN_KEY,JSON.toJSONString(xkrLoginToken));
        xkrLoginTokenMapper.updateByPrimaryKeySelective(xkrLoginToken);
    }


    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
            int index = ip.indexOf(",");
            if(index != -1){
                return ip.substring(0,index);
            }else{
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
            return ip;
        }
        return request.getRemoteAddr();
    }

    public XkrLoginToken getUserLoginRecordById(Long userId){
        if(Objects.isNull(userId)){
            return null;
        }
        List<XkrLoginToken> userIds = getUserLoginRecordByIds(ImmutableList.of(userId));
        return CollectionUtils.isEmpty(userIds) ? null : userIds.get(0);
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
