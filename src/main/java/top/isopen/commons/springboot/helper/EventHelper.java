package top.isopen.commons.springboot.helper;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

/**
 * 事件发布器
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 17:36
 */
public class EventHelper {

    private final ApplicationContext applicationContext;

    public EventHelper(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void publish(ApplicationEvent applicationEvent) {
        applicationContext.publishEvent(applicationEvent);
    }

}
