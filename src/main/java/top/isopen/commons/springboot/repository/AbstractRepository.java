package top.isopen.commons.springboot.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import top.isopen.commons.springboot.enums.QueryTypeEnum;
import top.isopen.commons.springboot.types.OrderBy;
import top.isopen.commons.springboot.types.OrderByList;
import top.isopen.commons.springboot.model.BaseModel;
import top.isopen.commons.springboot.types.BaseType;
import top.isopen.commons.springboot.types.Query;
import top.isopen.commons.springboot.util.FieldUtil;

import java.util.List;

public abstract class AbstractRepository<T extends BaseType, R extends BaseModel> {

    protected QueryWrapper<R> queryWrapper(T query, @Nullable OrderByList orderByList) {
        return this.queryWrapper(query, orderByList != null ? orderByList.getOrderByList() : null);
    }

    protected QueryWrapper<R> queryWrapper(T query, @Nullable List<OrderBy> orderByList) {
        QueryWrapper<R> queryWrapper = new QueryWrapper<>();

        List<Query<R>> queryList = buildQueryEntity(query);
        for (Query<R> queryEntity : queryList) {
            QueryTypeEnum queryType = queryEntity.getType();
            String fieldName = FieldUtil.resolveName(queryEntity.getFieldFunc());
            Object value = queryEntity.getValue();

            if (queryType == QueryTypeEnum.EQ) {
                queryWrapper.eq(fieldName, value);
            } else if (queryType == QueryTypeEnum.LIKE) {
                queryWrapper.like(fieldName, value);
            } else if (queryType == QueryTypeEnum.OR) {
                queryWrapper.or();
            } else if (queryType == QueryTypeEnum.LE) {
                queryWrapper.le(fieldName, value);
            } else if (queryType == QueryTypeEnum.GE) {
                queryWrapper.ge(fieldName, value);
            } else if (queryType == QueryTypeEnum.LT) {
                queryWrapper.lt(fieldName, value);
            } else if (queryType == QueryTypeEnum.GT) {
                queryWrapper.gt(fieldName, value);
            }
        }

        if (orderByList != null) {
            for (OrderBy orderBy : orderByList) {
                queryWrapper.orderBy(true, orderBy.isAsc(), orderBy.getColumn().getValue());
            }
        }

        return queryWrapper;
    }

    @NonNull
    protected abstract List<Query<R>> buildQueryEntity(T query);

}
