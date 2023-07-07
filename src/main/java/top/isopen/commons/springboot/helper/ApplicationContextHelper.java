package top.isopen.commons.springboot.helper;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import top.isopen.commons.springboot.enums.BaseErrorEnum;

/**
 * 应用上下文
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/5/19 16:35
 */
public class ApplicationContextHelper implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(@Nullable ApplicationContext applicationContext) throws BeansException {
        if (applicationContext == null) {
            BaseErrorEnum.INVALID_APPLICATION_CONTEXT_ERROR.throwException();
        }
        ApplicationContextHelper.applicationContext = applicationContext;
    }

    public <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }

}
