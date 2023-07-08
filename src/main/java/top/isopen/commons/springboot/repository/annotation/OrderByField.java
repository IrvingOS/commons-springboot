package top.isopen.commons.springboot.repository.annotation;

import top.isopen.commons.springboot.repository.enums.OrderByTypeEnum;

import java.lang.annotation.*;

/**
 * Model 层排序注解
 *
 * @author TimeChaser
 * @since 9/7/2023 下午1:02
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OrderByField {

    /**
     * 排序类型
     * <p>
     * {@link OrderByTypeEnum}
     */
    OrderByTypeEnum type() default OrderByTypeEnum.ASC;

    /**
     * 排序字段的优先级顺序
     * <p>
     * 默认为 -1
     */
    int order() default -1;

}
