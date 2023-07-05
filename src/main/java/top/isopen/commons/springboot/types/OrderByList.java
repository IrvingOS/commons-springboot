package top.isopen.commons.springboot.types;

import lombok.Builder;
import lombok.Data;
import top.isopen.commons.springboot.bean.OrderByRequest;
import top.isopen.commons.springboot.model.AbstractModel;
import top.isopen.commons.springboot.support.SFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class OrderByList<T extends AbstractModel<T, ?>> {

    private final List<OrderBy<T>> orderByList;

    public OrderByList() {
        this.orderByList = new ArrayList<>();
    }

    public OrderByList(OrderBy<T> orderBy) {
        this();
        this.orderByList.add(orderBy);
    }

    public OrderByList(List<OrderBy<T>> orderByList) {
        this.orderByList = orderByList;
    }

    public static <T extends AbstractModel<T, ?>> OrderByList<T> resolve(List<OrderByRequest> orderByRequestList) {
        return OrderByList
                .<T>builder()
                .orderByList(
                        orderByRequestList
                                .stream()
                                .map(OrderBy::<T>resolve)
                                .collect(Collectors.toList())
                )
                .build();
    }

    public OrderByList<T> asc(String column) {
        orderByList.add(new OrderBy<T>().asc(column));
        return this;
    }

    public OrderByList<T> asc(SFunction<T, ?> func) {
        orderByList.add(new OrderBy<T>().asc(func));
        return this;
    }

    public OrderByList<T> desc(String column) {
        orderByList.add(new OrderBy<T>().desc(column));
        return this;
    }

    public OrderByList<T> desc(SFunction<T, ?> func) {
        orderByList.add(new OrderBy<T>().desc(func));
        return this;
    }

}
