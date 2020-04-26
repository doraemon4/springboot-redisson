package com.stephen.learning.redisson.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

/**
 * @author jack
 * @description  用户自定义异常处理
 * @date 2020/4/23
 */
public class CustomerCacheErrorHandler implements CacheErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomerCacheErrorHandler.class);

    @Override
    public void handleCacheGetError(RuntimeException e, Cache cache, Object o) {
        log.error("获取缓存异常",e.getMessage(),e);
    }

    @Override
    public void handleCachePutError(RuntimeException e, Cache cache, Object o, Object o1) {
        log.error("存入缓存异常",e.getMessage(),e);
    }

    @Override
    public void handleCacheEvictError(RuntimeException e, Cache cache, Object o) {
        log.error(e.getMessage(),e);
    }

    @Override
    public void handleCacheClearError(RuntimeException e, Cache cache) {
        log.error("清除缓存异常",e.getMessage(),e);
    }
}
