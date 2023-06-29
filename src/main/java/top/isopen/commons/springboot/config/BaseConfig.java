package top.isopen.commons.springboot.config;

import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import top.isopen.commons.springboot.exception.BaseExceptionHandler;
import top.isopen.commons.springboot.helper.ApplicationContextHelper;
import top.isopen.commons.springboot.helper.RedisHelper;
import top.isopen.commons.springboot.lock.RedLockAspect;
import top.isopen.commons.springboot.lock.RedLocksAspect;

@Configuration(proxyBeanMethods = false)
@Import({BaseExceptionHandler.class})
public class BaseConfig {

    @Bean
    @ConditionalOnMissingBean({ApplicationContextHelper.class})
    public ApplicationContextHelper applicationContextHelper() {
        return new ApplicationContextHelper();
    }

    @Bean
    @ConditionalOnBean({StringRedisTemplate.class})
    @ConditionalOnMissingBean({RedisHelper.class})
    public RedisHelper redisHelper(ApplicationContextHelper applicationContextHelper) {
        StringRedisTemplate stringRedisTemplate = applicationContextHelper.getBean(StringRedisTemplate.class);
        return new RedisHelper(stringRedisTemplate);
    }

    @Bean
    @ConditionalOnBean({RedissonClient.class})
    public RedLockAspect redLockAspect(RedissonClient redissonClient) {
        return new RedLockAspect(redissonClient);
    }

    @Bean
    @ConditionalOnBean({RedissonClient.class})
    public RedLocksAspect redLocksAspect(RedissonClient redissonClient) {
        return new RedLocksAspect(redissonClient);
    }

}
