/*
 * Copyright (c) 2017 <l_iupeiyu@qq.com> All rights reserved.
 */

package com.xkr.dao.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.util.Objects;

/**
 * author xkr
 * date 2017/3/22 0022 下午 16:16
 */
@Service
public class BaseRedisService {

    private static Logger logger = LoggerFactory.getLogger(BaseRedisService.class);

    @Autowired
    private JedisPool jedisPool;

    private static final String LOCK_KEY_POSTFIX = "_LOCK_KEY";


    public void set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            jedis.set(key, value);
            logger.info("Redis set success - " + key + ", value:" + value);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Redis set error: " + e.getMessage() + " - " + key + ", value:" + value);
        } finally {
            returnResource(jedis);
        }
    }

    public void set(String key, String value,int expireSecond) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            jedis.set(key, value);
            jedis.expire(key,expireSecond);
            logger.info("Redis set success - " + key + ", value:" + value);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Redis set error: " + e.getMessage() + " - " + key + ", value:" + value);
        } finally {
            returnResource(jedis);
        }
    }

    public void incrByCount(String key,long count, int ifNotExistExpireSecond) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            Long newValue = jedis.incrBy(key,count);
            if(newValue == 1L){
                jedis.expire(key,ifNotExistExpireSecond);
            }
            logger.info("Redis set success - " + key + ", ifNotExistExpireSecond:" + ifNotExistExpireSecond);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Redis set error: " + e.getMessage() + " - " + key + ", ifNotExistExpireSecond:" + ifNotExistExpireSecond);
        } finally {
            returnResource(jedis);
        }
    }


    public String get(String key) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.get(key);
            logger.info("Redis get success - " + key + ", value:" + result);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Redis set error: " + e.getMessage() + " - " + key + ", value:" + result);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    // TODO: 2018/5/7 待验证 cas aquire 
    public boolean compareAndSet(String key, String oldValue, String newValue) {
        try (Jedis jedis = getResource()){
            String current = jedis.get(key);
            current = Objects.isNull(current) ? "" : current;
            oldValue = Objects.isNull(oldValue) ? "" : oldValue;
            if (oldValue.equals(current)
                    && aquireLock(jedis, key, 10000L)) {
                try {
                    logger.debug("获取锁成功,key:{},oldValue:{},newValue:{}",key,oldValue,newValue);
                    jedis.set(key, newValue);
                    return true;
                } catch (Exception e) {
                    logger.error("Redis set error: " + e.getMessage() + " - " + key + ", value:" + newValue);
                } finally {
                    releaseLock(jedis, key);
                }
            }
        } catch (Exception e) {
            logger.error("Redis set error: " + e.getMessage() + " - " + key + ", value:" + newValue);
        } 
        return false;
    }

    public boolean aquireLock(String key, long expireTime) {
        try (Jedis jedis = getResource()) {
            return aquireLock(jedis, key, expireTime);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Redis set error: " + e.getMessage() + " - " + key);
        }
        return false;
    }

    public void releaseLock(Jedis jedis, String key) {
        if (jedis == null) {
            return;
        }
        String lockKey = key + LOCK_KEY_POSTFIX;
//        String currentValueStr = jedis.get(lockKey); //redis里的时间
//        if (currentValueStr != null && Long.parseLong(currentValueStr) < System.currentTimeMillis()) {
//            获取上一个锁到期时间，并设置现在的锁到期时间，
//            只有一个线程才能获取上一个线上的设置时间，因为jedis.getSet是同步的
        String oldValue = jedis.getSet(lockKey, "0");
        logger.debug("释放锁成功,key:{},oldValue:{}",key,oldValue);
    }

    /**
     * @param jedis
     * @param key
     * @param expireTime 毫秒单位
     * @return
     */
    public boolean aquireLock(Jedis jedis, String key, long expireTime) {
        try {
            long expires = System.currentTimeMillis() + expireTime + 1;
            String expiresStr = String.valueOf(expires); //锁到期时间

            String lockKey = key + LOCK_KEY_POSTFIX;
            if (jedis.setnx(lockKey, expiresStr) == 1) {
                logger.debug("初次设置锁成功,lockKey:{},exprieTime:{}",lockKey,expiresStr);
                //设置键失效时间
                jedis.pexpire(lockKey,expireTime+1);
                // lock acquired
                return true;
            }

            String currentValueStr = jedis.get(lockKey); //redis里的时间
            logger.debug("获取原有锁,lockKey:{},currentValueStr:{}",lockKey,currentValueStr);
            //判断是否为空，不为空的情况下，如果被其他线程设置了值，则第二个条件判断是过不去的
            if (currentValueStr != null && Long.parseLong(currentValueStr) < System.currentTimeMillis()) {
                // lock is expired
                //获取上一个锁到期时间，并设置现在的锁到期时间，
                String oldValueStr = jedis.getSet(lockKey, expiresStr);
                logger.debug("原有锁不为空,set新时间,lockKey:{},oldValueStr:{},expiresStr:{}",lockKey,oldValueStr,expiresStr);
                //只有一个线程才能获取上一个线上的设置时间，因为jedis.getSet是同步的
                if (oldValueStr != null && oldValueStr.equals(currentValueStr)) {
                    logger.debug("抢占锁成功,lockKey:{},oldValueStr:{},currentValueStr:{}",lockKey,oldValueStr,currentValueStr);
                    //如过这个时候，多个线程恰好都到了这里，但是只有一个线程的设置值和当前值相同，他才有权利获取锁
                    // lock acquired
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Redis set error: " + e.getMessage() + " - " + key);
        }
        return false;
    }


    private Jedis getResource() {
        return jedisPool.getResource();
    }

    private void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

}
