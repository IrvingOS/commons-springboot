package top.isopen.commons.springboot.types;

import lombok.Builder;
import lombok.Data;
import top.isopen.commons.springboot.support.SFunction;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class OrderByList {

    private final List<OrderBy> orderByList;

    public OrderByList() {
        this.orderByList = new ArrayList<>();
    }

    public OrderByList(OrderBy orderBy) {
        this();
        this.orderByList.add(orderBy);
    }

    public OrderByList(List<OrderBy> orderByList) {
        this.orderByList = orderByList;
    }

    public OrderByList asc(String column) {
        orderByList.add(OrderBy.asc(column));
        return this;
    }

    public <T> OrderByList asc(SFunction<T, ?> func) {
        orderByList.add(OrderBy.asc(func));
        return this;
    }

    public OrderByList desc(String column) {
        orderByList.add(OrderBy.desc(column));
        return this;
    }

    public <T> OrderByList desc(SFunction<T, ?> func) {
        orderByList.add(OrderBy.desc(func));
        return this;
    }

}
