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

/**
 * Redis 配置类
 * <p>
 * 用于注册 Redis 工具类 {@link RedisHelper}、Redis 分布式锁 {@link RedLockAspect} {@link RedLocksAspect}
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 17:03
 */
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
