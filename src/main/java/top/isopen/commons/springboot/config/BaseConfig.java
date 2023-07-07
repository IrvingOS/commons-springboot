package top.isopen.commons.springboot.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.isopen.commons.springboot.exception.BaseExceptionHandler;
import top.isopen.commons.springboot.helper.ApplicationContextHelper;
import top.isopen.commons.springboot.helper.EventHelper;

/**
 * 基础配置类
 * <p>
 * 用于注册 应用上下文 {@link ApplicationContextHelper}、事件发布器 {@link EventHelper}、基础异常处理器 {@link BaseExceptionHandler}
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 16:59
 */
@Configuration(proxyBeanMethods = false)
public class BaseConfig {

    @Bean
    @ConditionalOnMissingBean({ApplicationContextHelper.class})
    public ApplicationContextHelper applicationContextHelper() {
        return new ApplicationContextHelper();
    }

    @Bean
    @ConditionalOnBean({ApplicationContextHelper.class})
    @ConditionalOnMissingBean({EventHelper.class})
    public EventHelper eventHelper() {
        return new EventHelper(ApplicationContextHelper.getApplicationContext());
    }

    @Bean
    @ConditionalOnMissingBean({BaseExceptionHandler.class})
    public BaseExceptionHandler baseExceptionHandler() {
        return new BaseExceptionHandler();
    }

}
