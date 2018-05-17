package com.xkr.dao.cache;

import com.google.common.cache.CacheLoader;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/18
 */
public abstract class RemedyCacheLoader<K, V> extends CacheLoader<K, V> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ListenableFuture<V> reload(K key, V oldValue) throws Exception {
        checkNotNull(key);
        checkNotNull(oldValue);
        V result = null;
        try {
            result = load(key);
        }catch (Exception e){
            logger.error("RemedyCacheLoader reload error on loading cache ,will use oldValue .key:{},oldValue:{}",key,oldValue,e);
        }
        if(Objects.isNull(result)){
            result = oldValue;
        }
        return Futures.immediateFuture(result);
    }
}
