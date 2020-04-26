package com.stephen.learning.redisson.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author jack
 * @description  使用多种缓存机制：优先从堆内存读取缓存，堆内存缓存不存在时再从redis读取缓存，
 *   redis缓存不存在时最后从mysql读取数据，并将读取到的数据依次写到redis和堆内存中
 * @date 2020/4/23
 */
@Data
@AllArgsConstructor
public class CustomCacheResolver implements CacheResolver, InitializingBean {

    @Nullable
    private List<CacheManager> cacheManagerList;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.cacheManagerList, "CacheManager is required");
    }

    @Override
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> cacheOperationInvocationContext) {
        Collection<String> cacheNames = getCacheNames(cacheOperationInvocationContext);
        if (cacheNames == null) {
            return Collections.emptyList();
        }
        Collection<Cache> result = new ArrayList<>();
        for(CacheManager cacheManager : getCacheManagerList()){
            for (String cacheName : cacheNames) {
                Cache cache = cacheManager.getCache(cacheName);
                if (cache == null) {
                    throw new IllegalArgumentException("Cannot find cache named '" +
                            cacheName + "' for " + cacheOperationInvocationContext.getOperation());
                }
                result.add(cache);
            }
        }
        return result;
    }

    private Collection<String> getCacheNames(CacheOperationInvocationContext<?> context){
        return context.getOperation().getCacheNames();
    }
}
