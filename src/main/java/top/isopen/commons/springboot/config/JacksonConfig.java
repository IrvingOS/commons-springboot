package top.isopen.commons.springboot.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson 序列化配置
 *
 * @author TimeChaser
 * @since 8/7/2023 上午9:31
 */
@Configuration(proxyBeanMethods = false)
public class JacksonConfig {

    /**
     * Long.TYPE == long.class
     * <p>
     * Long.class != Long.TYPE
     * <p>
     * Long.class != long.class
     *
     * @author TimeChaser
     * @since 8/7/2023 上午9:32
     */
    @Bean
    @ConditionalOnMissingBean({Jackson2ObjectMapperBuilderCustomizer.class})
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder.serializerByType(Long.TYPE, ToStringSerializer.instance)
                .serializerByType(Long.class, ToStringSerializer.instance);
    }

}
