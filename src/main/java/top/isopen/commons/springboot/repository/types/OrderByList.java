package top.isopen.commons.springboot.repository.types;

import top.isopen.commons.springboot.repository.bean.OrderByRequest;
import top.isopen.commons.springboot.repository.support.SFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 多排序类型
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 16:35
 */
public class OrderByList<T> {

    private final List<OrderBy<T>> value;

    private OrderByList() {
        this.value = new ArrayList<>();
    }

    public static <T> OrderByList<T> resolve(List<OrderByRequest> orderByRequestList) {
        return OrderByList.<T>builder()
                .orderBy(orderByRequestList.stream().map(OrderBy::<T>resolve).collect(Collectors.toList()))
                .build();
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public List<OrderBy<T>> getValue() {
        return value;
    }

    public static class Builder<T> {
        private final OrderByList<T> orderByList;

        Builder() {
            orderByList = new OrderByList<>();
        }

        public OrderByList<T> build() {
            return orderByList;
        }

        public Builder<T> asc(SFunction<T, ?> columnFunc) {
            OrderBy<T> orderBy = OrderBy.<T>builder().asc(columnFunc).build();
            orderByList.getValue().add(orderBy);
            return this;
        }

        public Builder<T> asc(String column) {
            OrderBy<T> orderBy = OrderBy.<T>builder().asc(column).build();
            orderByList.getValue().add(orderBy);
            return this;
        }

        public Builder<T> asc(boolean asc, SFunction<T, ?> columnFunc) {
            OrderBy<T> orderBy = OrderBy.<T>builder().asc(asc, columnFunc).build();
            orderByList.getValue().add(orderBy);
            return this;
        }

        public Builder<T> asc(boolean asc, String column) {
            OrderBy<T> orderBy = OrderBy.<T>builder().asc(asc, column).build();
            orderByList.getValue().add(orderBy);
            return this;
        }

        public Builder<T> desc(SFunction<T, ?> columnFunc) {
            OrderBy<T> orderBy = OrderBy.<T>builder().desc(columnFunc).build();
            orderByList.getValue().add(orderBy);
            return this;
        }

        public Builder<T> desc(String column) {
            OrderBy<T> orderBy = OrderBy.<T>builder().desc(column).build();
            orderByList.getValue().add(orderBy);
            return this;
        }

        public Builder<T> orderBy(OrderBy<T> orderBy) {
            orderByList.getValue().add(orderBy);
            return this;
        }

        @SafeVarargs
        public final Builder<T> orderBy(OrderBy<T>... orderByList) {
            for (OrderBy<T> orderBy : orderByList) {
                this.orderByList.getValue().add(orderBy);
            }
            return this;
        }

        public Builder<T> orderBy(List<OrderBy<T>> orderByList) {
            this.orderByList.getValue().addAll(orderByList);
            return this;
        }

    }

}
