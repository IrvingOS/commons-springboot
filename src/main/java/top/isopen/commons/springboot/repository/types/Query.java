package top.isopen.commons.springboot.repository.types;

import top.isopen.commons.springboot.repository.bean.QueryRequest;
import top.isopen.commons.springboot.repository.enums.QueryTypeEnum;
import top.isopen.commons.springboot.repository.support.SFunction;
import top.isopen.commons.springboot.util.FieldUtil;
import top.isopen.commons.springboot.util.NameUtil;

/**
 * 条件查询类型
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 17:45
 */
public class Query<T> {

    private QueryTypeEnum type;
    private SFunction<T, ?> columnFunc;
    private Column column;
    private Object value;

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static <T> Query<T> resolve(QueryRequest queryRequest) {
        return Query.<T>builder()
                .type(QueryTypeEnum.resolve(queryRequest.getType()))
                .column(queryRequest.getColumn())
                .value(queryRequest.getValue())
                .build();
    }

    public QueryTypeEnum getType() {
        return type;
    }

    public String getColumn() {
        return column.getValue();
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Query{" +
                "type=" + type +
                ", columnFunc=" + columnFunc +
                ", column='" + column.getValue() + '\'' +
                ", value=" + value +
                '}';
    }

    public static class Builder<T> {

        private final Query<T> query;

        Builder() {
            query = new Query<>();
        }

        public Query<T> build() {
            return query;
        }

        public Builder<T> type(QueryTypeEnum type) {
            query.type = type;
            return this;
        }

        public Builder<T> columnFunc(SFunction<T, ?> columnFunc) {
            query.columnFunc = columnFunc;
            query.column = new Column(FieldUtil.resolveName(columnFunc));
            return this;
        }

        public Builder<T> column(String column) {
            query.column = new Column(NameUtil.humpToUnderline(column));
            return this;
        }

        public Builder<T> value(Object value) {
            query.value = value;
            return this;
        }

    }

}
