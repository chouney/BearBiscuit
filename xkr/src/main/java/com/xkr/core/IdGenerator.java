package com.xkr.core;

import com.relops.snowflake.Snowflake;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;

import java.io.Serializable;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/4
 */
public class IdGenerator implements SessionIdGenerator {

    private Snowflake snowflake;

    public IdGenerator() {
        snowflake = new Snowflake(1);
    }

    @Override
    public Serializable generateId(Session session) {
        return snowflake.next();
    }

    public Long generateId() {
        return snowflake.next();
    }
}
