package top.isopen.commons.springboot.config;

import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import top.isopen.commons.springboot.helper.ApplicationContextHelper;
import top.isopen.commons.springboot.helper.RedisHelper;
import top.isopen.commons.springboot.lock.RedLockAspect;
import top.isopen.commons.springboot.lock.RedLocksAspect;

@Configuration(proxyBeanMethods = false)
public class RedisConfig {

    @Bean
    @ConditionalOnBean({StringRedisTemplate.class})
    @ConditionalOnMissingBean({RedisHelper.class})
    public RedisHelper redisHelper(ApplicationContextHelper applicationContextHelper) {
        StringRedisTemplate stringRedisTemplate = applicationContextHelper.getBean(StringRedisTemplate.class);
        return new RedisHelper(stringRedisTemplate);
    }

    @Bean
    @ConditionalOnBean({RedisHelper.class})
    @ConditionalOnMissingBean({RedisHelper.KeyOps.class})
    public RedisHelper.KeyOps redisKeyOps(RedisHelper redisHelper) {
        return redisHelper.new KeyOps();
    }

    @Bean
    @ConditionalOnBean({RedisHelper.class})
    @ConditionalOnMissingBean({RedisHelper.StringOps.class})
    public RedisHelper.StringOps redisStringOps(RedisHelper redisHelper) {
        return redisHelper.new StringOps();
    }

    @Bean
    @ConditionalOnBean({RedisHelper.class})
    @ConditionalOnMissingBean({RedisHelper.HashOps.class})
    public RedisHelper.HashOps redisHashOps(RedisHelper redisHelper) {
        return redisHelper.new HashOps();
    }

    @Bean
    @ConditionalOnBean({RedisHelper.class})
    @ConditionalOnMissingBean({RedisHelper.ListOps.class})
    public RedisHelper.ListOps redisListOps(RedisHelper redisHelper) {
        return redisHelper.new ListOps();
    }

    @Bean
    @ConditionalOnBean({RedisHelper.class})
    @ConditionalOnMissingBean({RedisHelper.SetOps.class})
    public RedisHelper.SetOps redisSetOps(RedisHelper redisHelper) {
        return redisHelper.new SetOps();
    }

    @Bean
    @ConditionalOnBean({RedisHelper.class})
    @ConditionalOnMissingBean({RedisHelper.ZSetOps.class})
    public RedisHelper.ZSetOps redisZSetOps(RedisHelper redisHelper) {
        return redisHelper.new ZSetOps();
    }

    @Bean
    @ConditionalOnBean({RedisHelper.class})
    @ConditionalOnMissingBean({RedisHelper.LockOps.class})
    public RedisHelper.LockOps redisLockOps(RedisHelper redisHelper) {
        return redisHelper.new LockOps();
    }

    @Bean
    @ConditionalOnBean({RedissonClient.class})
    @ConditionalOnMissingBean({RedLockAspect.class})
    public RedLockAspect redLockAspect(RedissonClient redissonClient) {
        return new RedLockAspect(redissonClient);
    }

    @Bean
    @ConditionalOnBean({RedissonClient.class})
    @ConditionalOnMissingBean({RedLocksAspect.class})
    public RedLocksAspect redLocksAspect(RedissonClient redissonClient) {
        return new RedLocksAspect(redissonClient);
    }

}
