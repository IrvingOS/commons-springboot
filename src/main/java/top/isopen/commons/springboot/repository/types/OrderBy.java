package top.isopen.commons.springboot.repository.types;

import top.isopen.commons.springboot.repository.bean.OrderByRequest;
import top.isopen.commons.springboot.repository.support.SFunction;
import top.isopen.commons.springboot.util.FieldUtil;
import top.isopen.commons.springboot.util.NameUtil;

/**
 * 排序类型
 * <p>
 * 支持通过 {@link SFunction} 和 column 名称构造
 * <p>
 * 使用 {@link SFunction}  构造时自动解析 column 名称
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 16:35
 */
public class OrderBy<T> {

    private SFunction<T, ?> columnFunc;
    private Column column;
    private boolean asc;
    private int order;

    public static <T> OrderBy<T> resolve(OrderByRequest orderByRequest) {
        return OrderBy.<T>builder()
                .asc(orderByRequest.isAsc(), orderByRequest.getColumn(), orderByRequest.getOrder())
                .build();
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public String getColumn() {
        return column.getValue();
    }

    public boolean isAsc() {
        return asc;
    }

    public int getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return "OrderBy{" +
                "columnFunc=" + columnFunc +
                ", column=" + column.getValue() +
                ", asc=" + asc +
                '}';
    }

    public static class Builder<T> {
        private final OrderBy<T> orderBy;

        Builder() {
            orderBy = new OrderBy<>();
        }

        public OrderBy<T> build() {
            return orderBy;
        }

        public Builder<T> asc(SFunction<T, ?> columnFunc) {
            orderBy.asc = true;
            orderBy.columnFunc = columnFunc;
            orderBy.column = new Column(FieldUtil.resolveName(columnFunc));
            return this;
        }

        public Builder<T> asc(String column) {
            orderBy.asc = true;
            orderBy.column = new Column(NameUtil.humpToUnderline(column));
            return this;
        }

        public Builder<T> asc(boolean asc, SFunction<T, ?> columnFunc) {
            orderBy.asc = asc;
            orderBy.columnFunc = columnFunc;
            orderBy.column = new Column(FieldUtil.resolveName(columnFunc));
            return this;
        }

        public Builder<T> asc(boolean asc, SFunction<T, ?> columnFunc, int order) {
            orderBy.asc = asc;
            orderBy.columnFunc = columnFunc;
            orderBy.column = new Column(FieldUtil.resolveName(columnFunc));
            orderBy.order = order;
            return this;
        }

        public Builder<T> asc(boolean asc, String column) {
            orderBy.asc = asc;
            orderBy.column = new Column(NameUtil.humpToUnderline(column));
            return this;
        }

        public Builder<T> asc(boolean asc, String column, int order) {
            orderBy.asc = asc;
            orderBy.column = new Column(NameUtil.humpToUnderline(column));
            orderBy.order = order;
            return this;
        }

        public Builder<T> desc(SFunction<T, ?> columnFunc) {
            orderBy.asc = false;
            orderBy.columnFunc = columnFunc;
            orderBy.column = new Column(FieldUtil.resolveName(columnFunc));
            return this;
        }

        public Builder<T> desc(String column) {
            orderBy.asc = false;
            orderBy.column = new Column(NameUtil.humpToUnderline(column));
            return this;
        }

    }

}
