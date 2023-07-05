package top.isopen.commons.springboot.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import top.isopen.commons.springboot.enums.QueryTypeEnum;
import top.isopen.commons.springboot.model.AbstractModel;
import top.isopen.commons.springboot.types.AbstractType;
import top.isopen.commons.springboot.types.OrderBy;
import top.isopen.commons.springboot.types.OrderByList;
import top.isopen.commons.springboot.types.Query;
import top.isopen.commons.springboot.util.FieldUtil;

import java.util.List;

public abstract class AbstractRepository<T extends AbstractType<T, ?>, R extends AbstractModel<R, ?>> {

    protected QueryWrapper<R> queryWrapper(@Nullable T query, @Nullable OrderByList<R> orderByList) {
        return this.queryWrapper(query, orderByList != null ? orderByList.getOrderByList() : null);
    }

    protected QueryWrapper<R> queryWrapper(@Nullable T query, @Nullable List<OrderBy<R>> orderByList) {
        QueryWrapper<R> queryWrapper = new QueryWrapper<>();

        if (query != null) {
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
        }

        if (orderByList != null) {
            for (OrderBy<R> orderBy : orderByList) {
                queryWrapper.orderBy(true, orderBy.isAsc(), orderBy.getColumn().getValue());
            }
        }

        return queryWrapper;
    }

    @NonNull
    protected abstract List<Query<R>> buildQueryEntity(@NonNull T query);

}
