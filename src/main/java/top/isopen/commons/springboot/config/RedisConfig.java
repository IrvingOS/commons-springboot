package top.isopen.commons.springboot.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
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
    @ConditionalOnBean({RedisConnectionFactory.class})
    @ConditionalOnMissingBean({RedisTemplate.class})
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        /*使用 Jackson2JsonRedisSerialize 替换默认序列化*/
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        /*JSON 转对象类，不设置，默认的会将 JSON 转成 HashMap*/
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.WRAPPER_ARRAY);
        jackson2JsonRedisSerializer.setObjectMapper(mapper);

        /*设置 key 和 value 的序列化器*/
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    @ConditionalOnBean({RedisTemplate.class})
    @ConditionalOnMissingBean({RedisHelper.class})
    public RedisHelper redisHelper(ApplicationContextHelper applicationContextHelper) {
        RedisTemplate<String, Object> redisTemplate = applicationContextHelper.getBean(RedisTemplate.class);
        return new RedisHelper(redisTemplate);
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
