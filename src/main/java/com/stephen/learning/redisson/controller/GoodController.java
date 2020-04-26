package com.stephen.learning.redisson.controller;

import com.stephen.learning.redisson.lock.DistributedLocker;
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

    @RequestMapping("/sell")
    public String sell() {
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
