package top.isopen.commons.springboot.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.isopen.commons.springboot.exception.BaseExceptionHandler;
import top.isopen.commons.springboot.helper.ApplicationContextHelper;
import top.isopen.commons.springboot.helper.EventHelper;

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
    public BaseExceptionHandler baseExceptionHandler() {
        return new BaseExceptionHandler();
    }

}
