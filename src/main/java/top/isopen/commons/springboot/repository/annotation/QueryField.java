package top.isopen.commons.springboot.repository.annotation;

import top.isopen.commons.springboot.repository.enums.QueryTypeEnum;

import java.lang.annotation.*;

/**
 * Model 层查询属性注解
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 15:14
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryField {

    /**
     * 查询类型
     * {@link QueryTypeEnum}
     *
     * @author TimeChaser
     * @since 2023/7/7 15:14
     */
    QueryTypeEnum type() default QueryTypeEnum.EQ;

}
