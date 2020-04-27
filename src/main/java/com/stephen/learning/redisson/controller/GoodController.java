package com.stephen.learning.redisson.controller;

import com.stephen.learning.redisson.lock.DistributedLocker;
import com.stephen.learning.redisson.lock.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jack
 * @description 模拟分布式锁秒杀抢购
 * @date 2020/4/25
 */
@Slf4j
@RestController
@RequestMapping("/goods")
public class GoodController {
    private final static String ITEM_COUNT = "item:count";
    private final static String LOCK_KEY = "item:count:lock";
    private static final int TIMEOUT = 10 * 1000; //超时时间 10s

    @Autowired
    RedisLock redisLock;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    DistributedLocker distributedLocker;

    @RequestMapping("/init")
    public String setCount(int value) {
        redisTemplate.opsForValue().set(ITEM_COUNT, value + "");
        return "success";
    }

    @RequestMapping("/getCount")
    public String getCount() {
        return (String) redisTemplate.opsForValue().get(ITEM_COUNT);
    }

    /**
     * 不使用锁
     * @return
     */
    @RequestMapping("/sell")
    public String sell() {
        String result;
        int stock = Integer.parseInt(redisTemplate.opsForValue().get(ITEM_COUNT).toString());
        if (stock > 0) {
            result = "success";
            redisTemplate.opsForValue().set(ITEM_COUNT, (stock - 1) + "");
        } else {
            result = "fail";
        }
        log.info(Thread.currentThread().getName() + ", result: " + result);
        return result;
    }

    /**
     * 使用setnx实现锁
     * @return
     */
    @RequestMapping("/sell2")
    public String sell2() {
        String result = "fail";
        long time = System.currentTimeMillis() + TIMEOUT;
        if(!redisLock.lock(LOCK_KEY,String.valueOf(time))){
            log.error("--------获取锁失败---------");
           return result;
        }
        int stock = Integer.parseInt(redisTemplate.opsForValue().get(ITEM_COUNT).toString());
        if (stock > 0) {
            result = "success";
            redisTemplate.opsForValue().set(ITEM_COUNT, (stock - 1) + "");
        } else {
            result = "fail";
        }
        redisLock.unlock(LOCK_KEY,String.valueOf(time));
        log.info(Thread.currentThread().getName() + ", result: " + result);
        return result;
    }

    /**
     * 使用分布式锁
     * @return
     */
    @RequestMapping("/sell3")
    public String sell3() {
        String result;
        distributedLocker.lock(LOCK_KEY);
        int stock = Integer.parseInt(redisTemplate.opsForValue().get(ITEM_COUNT).toString());
        if (stock > 0) {
            result = "success";
            redisTemplate.opsForValue().set(ITEM_COUNT, (stock - 1) + "");
        } else {
            result = "fail";
        }
        distributedLocker.unlock(LOCK_KEY);
        log.info(Thread.currentThread().getName() + ", result: " + result);
        return result;
    }
}
