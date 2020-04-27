package com.stephen.learning.redisson.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author jack
 * @description 使用redis的setnx实现
 * @date 2020/4/27
 */

@Slf4j
@Component
public class RedisLock {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 加锁
     * @param key
     * @param value
     * @return
     */
    public boolean lock(String key,String value){
        //setnx如果不存在就新建，存在就不做任何操作
        if(stringRedisTemplate.opsForValue().setIfAbsent(key,value)){
            return true;
        }
        String currentValue = stringRedisTemplate.opsForValue().get(key);
        //如果锁过期
        if(!StringUtils.isEmpty(currentValue)&&Long.parseLong(currentValue)<System.currentTimeMillis()){
            //使用getset方法，获取上一个锁的时间
            String oldValue = stringRedisTemplate.opsForValue().getAndSet(key, value);
            if(!StringUtils.isEmpty(oldValue) && oldValue.equals(currentValue)){
                return true;
            }

        }
        return false;
    }

    /**
     * 释放锁
     * @param key
     * @param value
     */
    public void unlock(String key,String value){
        try {
            String currentValue = stringRedisTemplate.opsForValue().get(key);
            if (!StringUtils.isEmpty(currentValue) && currentValue.equals(value)) {
                stringRedisTemplate.opsForValue().getOperations().delete(key);
            }
        }catch(Exception e){
            log.error("【redis分布式锁】解锁异常, {}", e);
        }

    }
}
