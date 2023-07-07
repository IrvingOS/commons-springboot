package top.isopen.commons.springboot.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.SneakyThrows;
import top.isopen.commons.springboot.model.AbstractModel;
import top.isopen.commons.springboot.repository.annotation.QueryField;
import top.isopen.commons.springboot.repository.enums.QueryTypeEnum;
import top.isopen.commons.springboot.repository.types.OrderBy;
import top.isopen.commons.springboot.repository.types.OrderByList;
import top.isopen.commons.springboot.repository.types.Query;
import top.isopen.commons.springboot.repository.types.QueryList;
import top.isopen.commons.springboot.types.AbstractType;
import top.isopen.commons.springboot.util.FieldUtil;
import top.isopen.commons.springboot.util.TypeUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 抽象 Repository 层
 * <p>
 * 提供基础条件查询（{@link QueryField}）、复杂条件查询（{@link Query}、{@link QueryList}）以及排序查询（{@link OrderBy}、{@link OrderByList}）动态支持
 * <p>
 * 简化了复杂查询的组合
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 15:22
 */
public abstract class AbstractRepository<T extends AbstractType<T, ?>, R extends AbstractModel<R, ?>> {

    /**
     * 普通条件查询与排序查询的组合查询
     *
     * @param query       {@link AbstractType} 普通条件查询实体，对于 query 中被 {@link QueryField} 注解且不为空的属性进行 {@link QueryField#type()} 类型的查询
     * @param orderByList {@link OrderBy} 排序查询实体列表
     * @return {@link LambdaQueryWrapper}
     * @author TimeChaser
     * @since 2023/7/7 15:30
     */
    @SafeVarargs
    protected final LambdaQueryWrapper<R> queryWrapper(T query, OrderBy<T>... orderByList) {
        return queryWrapper(query, orderByList != null ? OrderByList.<T>builder().orderBy(orderByList).build() : null);
    }

    /**
     * 普通条件查询与排序查询的组合查询
     *
     * @param query       {@link AbstractType} 普通条件查询实体，对于 query 中被 {@link QueryField} 注解且不为空的属性进行 {@link QueryField#type()} 类型的查询
     * @param orderByList {@link OrderByList} 排序查询实体
     * @return {@link LambdaQueryWrapper<R>}
     * @author TimeChaser
     * @since 2023/7/7 15:34
     */
    protected LambdaQueryWrapper<R> queryWrapper(T query, OrderByList<T> orderByList) {
        return this.queryWrapper(query, orderByList != null ? orderByList.getValue() : null);
    }

    /**
     * 复杂条件查询与排序查询的组合查询
     *
     * @param queryList   {@link QueryList} 复杂条件查询实体
     * @param orderByList {@link OrderBy} 排序查询实体列表
     * @return {@link LambdaQueryWrapper<R>}
     * @author TimeChaser
     * @since 2023/7/7 15:36
     */
    @SafeVarargs
    protected final LambdaQueryWrapper<R> queryWrapper(QueryList<T> queryList, OrderBy<T>... orderByList) {
        return queryWrapper(queryList, orderByList != null ? OrderByList.<T>builder().orderBy(orderByList).build() : null);
    }

    /**
     * 复杂条件查询与排序查询的组合查询
     *
     * @param queryList   {@link QueryList} 复杂条件查询实体
     * @param orderByList {@link OrderBy} 排序查询实体
     * @return {@link LambdaQueryWrapper<R>}
     * @author TimeChaser
     * @since 2023/7/7 15:37
     */
    protected final LambdaQueryWrapper<R> queryWrapper(QueryList<T> queryList, OrderByList<T> orderByList) {
        return this.queryWrapper(queryList != null ? queryList.getValue() : null, orderByList != null ? orderByList.getValue() : null);
    }

    private LambdaQueryWrapper<R> queryWrapper(T query, List<OrderBy<T>> orderByList) {
        QueryWrapper<R> queryWrapper = new QueryWrapper<>();

        if (query != null) {
            List<Query<R>> queryList = resolveQuery(query);
            fillQuery(queryWrapper, queryList);
        }
        if (orderByList != null) {
            List<OrderBy<R>> transformedOrderByList = TypeUtil.transform(orderByList,
                    orderBy -> OrderBy.<R>builder()
                            .asc(orderBy.isAsc(), orderBy.getColumn())
                            .build());
            fillOrderBy(queryWrapper, transformedOrderByList);
        }

        return queryWrapper.lambda();
    }

    private LambdaQueryWrapper<R> queryWrapper(List<Query<T>> queryList, List<OrderBy<T>> orderByList) {
        QueryWrapper<R> queryWrapper = new QueryWrapper<>();

        if (queryList != null) {
            List<Query<R>> transformedQueryList = TypeUtil.transform(queryList,
                    query -> Query.<R>builder()
                            .type(query.getType())
                            .column(query.getColumn())
                            .value(query.getValue())
                            .build());
            fillQuery(queryWrapper, transformedQueryList);
        }
        if (orderByList != null) {
            List<OrderBy<R>> transformedOrderByList = TypeUtil.transform(orderByList,
                    orderBy -> OrderBy.<R>builder()
                            .asc(orderBy.isAsc(), orderBy.getColumn())
                            .build());
            fillOrderBy(queryWrapper, transformedOrderByList);
        }

        return queryWrapper.lambda();
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private List<Query<R>> resolveQuery(T query) {
        List<Query<R>> result = new ArrayList<>();

        R model = (R) query.toModel();
        Class<R> clazz = (Class<R>) model.getClass();

        List<Field> fieldList = FieldUtil.resolveDeclaredField(clazz);
        for (Field field : fieldList) {
            if (field.isAnnotationPresent(QueryField.class)) {
                field.setAccessible(true);
                Object value = field.get(model);
                if (value != null) {
                    QueryField queryField = field.getAnnotation(QueryField.class);
                    QueryTypeEnum type = queryField.type();
                    result.add(Query.<R>builder()
                            .type(type)
                            .column(field.getName())
                            .value(value)
                            .build());
                }
            }
        }
        return result;
    }

    private void fillQuery(QueryWrapper<R> queryWrapper, List<Query<R>> queryList) {
        for (Query<R> queryEntity : queryList) {
            QueryTypeEnum queryType = queryEntity.getType();
            String column = queryEntity.getColumn();
            Object value = queryEntity.getValue();

            if (queryType == QueryTypeEnum.EQ) {
                queryWrapper.eq(column, value);
            } else if (queryType == QueryTypeEnum.LIKE) {
                queryWrapper.like(column, value);
            } else if (queryType == QueryTypeEnum.OR) {
                queryWrapper.or();
            } else if (queryType == QueryTypeEnum.NE) {
                queryWrapper.ne(column, value);
            } else if (queryType == QueryTypeEnum.LE) {
                queryWrapper.le(column, value);
            } else if (queryType == QueryTypeEnum.GE) {
                queryWrapper.ge(column, value);
            } else if (queryType == QueryTypeEnum.LT) {
                queryWrapper.lt(column, value);
            } else if (queryType == QueryTypeEnum.GT) {
                queryWrapper.gt(column, value);
            }
        }
    }

    private void fillOrderBy(QueryWrapper<R> queryWrapper, List<OrderBy<R>> orderByList) {
        for (OrderBy<R> orderBy : orderByList) {
            queryWrapper.orderBy(true, orderBy.isAsc(), orderBy.getColumn());
        }
    }

}
