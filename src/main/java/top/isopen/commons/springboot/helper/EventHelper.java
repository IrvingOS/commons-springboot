package top.isopen.commons.springboot.helper;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

public class EventHelper {

    private final ApplicationContext applicationContext;

    public EventHelper(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void publish(ApplicationEvent applicationEvent) {
        applicationContext.publishEvent(applicationEvent);
    }

}
