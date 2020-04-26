package com.stephen.learning.redisson.config;

import com.stephen.learning.redisson.lock.DistributedLocker;
import com.stephen.learning.redisson.lock.RedissonDistributedLocker;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author jack
 * @description  Redisson配置
 * 参考： https://blog.csdn.net/baidu_38558076/java/article/details/90707985
 * CachingConfigurerSupport
 * @date 2020/4/23
 */
@Configuration
public class RedissonConfig {

    /**
     * 初始化 redisson
     * @return
     * @throws IOException
     */
    @Bean
    public RedissonClient redisson() throws IOException {
        //使用的是yaml格式的配置文件，读取使用Config.fromYAML
        Config config = Config.fromYAML(RedissonConfig.class.getClassLoader().getResource("redisson-config.yml"));
        return Redisson.create(config);
    }

    @Bean
    public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redisson) {
        return new RedissonConnectionFactory(redisson);
    }


    @Bean
    public DistributedLocker distributedLocker(@Autowired RedissonClient redissonClient){
        RedissonDistributedLocker distributedLocker = new RedissonDistributedLocker();
        distributedLocker.setRedissonClient(redissonClient);
        return distributedLocker;
    }

}
