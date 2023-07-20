package top.isopen.commons.springboot.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.SneakyThrows;
import top.isopen.commons.logging.Log;
import top.isopen.commons.logging.LogFactory;
import top.isopen.commons.springboot.model.AbstractModel;
import top.isopen.commons.springboot.repository.annotation.OrderByField;
import top.isopen.commons.springboot.repository.annotation.QueryField;
import top.isopen.commons.springboot.repository.enums.OrderByTypeEnum;
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
import java.util.Comparator;
import java.util.List;

/**
 * 抽象 Repository 层
 * <p>
 * 提供基础条件注解式查询（{@link QueryField}）、复杂条件查询（{@link Query}、{@link QueryList}）以及排序查询（{@link OrderBy}、{@link OrderByList}）动态支持
 * <p>
 * 简化了复杂查询的组合
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 15:22
 */
public abstract class AbstractRepository<T extends AbstractType<T, ?>, R extends AbstractModel<R, ?>> {

    private static final Log log = LogFactory.getLog(AbstractRepository.class);

    /**
     * 注解式条件查询与注解式排序查询的组合查询
     *
     * @param query   查询实体，可为 null
     * @param inOrder 是否启用 {@link OrderByField} 排序
     * @return {@link LambdaQueryWrapper<R>}
     * @author TimeChaser
     * @since 2023/7/10 14:47
     */
    @SuppressWarnings("unchecked")
    protected final LambdaQueryWrapper<R> queryWrapper(T query, boolean inOrder) {
        R model = query != null ? (R) query.toModel() : null;
        Class<R> clazz = model != null ? (Class<R>) model.getClass() : null;
        List<Field> fieldList = clazz != null ? FieldUtil.resolveDeclaredField(clazz) : null;

        List<Query<R>> queryList = fieldList != null ? resolveQuery(model, fieldList) : null;
        List<OrderBy<R>> orderByList = inOrder && fieldList != null ? resolveOrderBy(fieldList) : null;

        return queryWrapper(queryList, orderByList);
    }

    /**
     * 注解式条件查询与排序查询的组合查询
     *
     * @param query       {@link AbstractType} 注解式条件查询实体，对于 query 中被 {@link QueryField} 注解且不为空的属性进行 {@link QueryField#type()} 类型的查询，可为 null
     * @param orderByList {@link OrderBy} 排序查询实体列表，可为 null
     * @return {@link LambdaQueryWrapper}
     * @author TimeChaser
     * @since 2023/7/7 15:30
     */
    @SafeVarargs
    protected final LambdaQueryWrapper<R> queryWrapper(T query, OrderBy<T>... orderByList) {
        return queryWrapper(query, orderByList != null ? OrderByList.<T>builder().orderBy(orderByList).build() : null);
    }

    /**
     * 注解式条件查询与排序查询的组合查询
     *
     * @param query       {@link AbstractType} 注解式条件查询实体，对于 query 中被 {@link QueryField} 注解且不为空的属性进行 {@link QueryField#type()} 类型的查询，可为 null
     * @param orderByList {@link OrderByList} 排序查询实体，可为 null
     * @return {@link LambdaQueryWrapper<R>}
     * @author TimeChaser
     * @since 2023/7/7 15:34
     */
    @SuppressWarnings("unchecked")
    protected LambdaQueryWrapper<R> queryWrapper(T query, OrderByList<T> orderByList) {
        R model = query != null ? (R) query.toModel() : null;
        Class<R> clazz = model != null ? (Class<R>) model.getClass() : null;
        List<Field> fieldList = clazz != null ? FieldUtil.resolveDeclaredField(clazz) : null;

        List<Query<R>> queryList = fieldList != null ? resolveQuery(model, fieldList) : null;
        List<OrderBy<R>> transformedOrderByList = orderByList != null ? TypeUtil.transform(orderByList.getValue(),
                orderBy -> OrderBy.<R>builder().asc(orderBy.isAsc(), orderBy.getColumn()).build()) :
                null;

        return queryWrapper(queryList, transformedOrderByList);
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
        return queryWrapper(queryList,
                orderByList != null ? OrderByList.<T>builder().orderBy(orderByList).build() : null);
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
        List<Query<R>> transformedQueryList = queryList != null ? TypeUtil.transform(queryList.getValue(),
                query -> Query.<R>builder()
                        .type(query.getType())
                        .column(query.getColumn())
                        .value(query.getValue())
                        .subQuery(transformQueryList(query.getSubQuery()))
                        .build()) : null;
        List<OrderBy<R>> transformedOrderByList = orderByList != null ? TypeUtil.transform(orderByList.getValue(),
                orderBy -> OrderBy.<R>builder().asc(orderBy.isAsc(), orderBy.getColumn()).build()) :
                null;

        return this.queryWrapper(transformedQueryList, transformedOrderByList);
    }

    private List<Query<R>> transformQueryList(List<Query<T>> queryList) {
        if (queryList == null) {
            return null;
        }
        return TypeUtil.transform(queryList, query -> Query.<R>builder()
                .type(query.getType())
                .column(query.getColumn())
                .value(query.getValue())
                .subQuery(query.getSubQuery() != null ? transformQueryList(query.getSubQuery()) : null)
                .build());
    }

    private LambdaQueryWrapper<R> queryWrapper(List<Query<R>> queryList, List<OrderBy<R>> orderByList) {
        QueryWrapper<R> queryWrapper = new QueryWrapper<>();

        if (queryList != null) {
            if (log.isDebugEnabled()) {
                log.debug("query: {}", queryList);
            }
            fillQuery(queryWrapper, queryList);
        }
        if (orderByList != null) {
            if (log.isDebugEnabled()) {
                log.debug("order by: {}", orderByList);
            }
            fillOrderBy(queryWrapper, orderByList);
        }

        return queryWrapper.lambda();
    }

    @SneakyThrows
    private List<Query<R>> resolveQuery(R model, List<Field> fieldList) {
        List<Query<R>> result = new ArrayList<>();

        for (Field field : fieldList) {
            if (field.isAnnotationPresent(QueryField.class)) {
                field.setAccessible(true);
                Object value = field.get(model);
                if (value != null) {
                    QueryField queryField = field.getAnnotation(QueryField.class);
                    QueryTypeEnum type = queryField.type();
                    result.add(Query.<R>builder().type(type).column(field.getName()).value(value).build());
                }
            }
        }
        return result;
    }

    private List<OrderBy<R>> resolveOrderBy(List<Field> fieldList) {
        List<OrderBy<R>> result = new ArrayList<>();

        for (Field field : fieldList) {
            if (field.isAnnotationPresent(OrderByField.class)) {
                OrderByField orderByField = field.getAnnotation(OrderByField.class);
                OrderByTypeEnum type = orderByField.type();
                int order = orderByField.order();
                result.add(OrderBy.<R>builder().asc(type.isAsc(), field.getName(), order).build());
            }
        }
        return result;
    }

    private void fillQuery(QueryWrapper<R> queryWrapper, List<Query<R>> queryList) {
        for (Query<R> queryEntity : queryList) {
            QueryTypeEnum queryType = queryEntity.getType();
            String column = escapeColumn(queryEntity.getColumn());
            Object value = queryEntity.getValue();
            List<Query<R>> subQuery = queryEntity.getSubQuery();

            if (queryType == QueryTypeEnum.EQ) {
                queryWrapper.eq(column, value);
            } else if (queryType == QueryTypeEnum.LIKE) {
                queryWrapper.like(column, value);
            } else if (queryType == QueryTypeEnum.OR) {
                queryWrapper.or();
            } else if (queryType == QueryTypeEnum.AND) {
                queryWrapper.and(x -> fillQuery(x, subQuery));
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
            } else if (queryType == QueryTypeEnum.IN) {
                queryWrapper.in(column, (List<Object>) value);
            } else if (queryType == QueryTypeEnum.NOT_IN) {
                queryWrapper.notIn(column, (List<Object>) value);
            }
        }
    }

    private void fillOrderBy(QueryWrapper<R> queryWrapper, List<OrderBy<R>> orderByList) {
        orderByList.sort(Comparator.comparingInt(OrderBy::getOrder));
        for (OrderBy<R> orderBy : orderByList) {
            queryWrapper.orderBy(true, orderBy.isAsc(), escapeColumn(orderBy.getColumn()));
        }
    }

    private String escapeColumn(String column) {
        return "`" + column + "`";
    }

}
