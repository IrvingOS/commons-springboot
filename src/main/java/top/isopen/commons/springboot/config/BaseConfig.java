package top.isopen.commons.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import top.isopen.commons.springboot.helper.ApplicationContextHelper;
import top.isopen.commons.springboot.helper.RedisHelper;

@Configuration(proxyBeanMethods = false)
public class BaseConfig {

    @Bean
    public ApplicationContextHelper applicationContextHelper() {
        return new ApplicationContextHelper();
    }

    @Bean
    public RedisHelper redisHelper(ApplicationContextHelper applicationContextHelper) {
        StringRedisTemplate stringRedisTemplate = applicationContextHelper.getBean(StringRedisTemplate.class);
        return new RedisHelper(stringRedisTemplate);
    }

}
