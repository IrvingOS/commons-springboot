package top.isopen.commons.springboot.repository.types;

import top.isopen.commons.springboot.repository.bean.QueryRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 多条件查询类型
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 17:45
 */
public class QueryList<T> {

    private final List<Query<T>> value;

    private QueryList() {
        this.value = new ArrayList<>();
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static <T> QueryList<T> resolve(List<QueryRequest> queryRequestList) {
        return QueryList.<T>builder()
                .query(queryRequestList.stream().map(Query::<T>resolve).collect(Collectors.toList()))
                .build();
    }

    public List<Query<T>> getValue() {
        return value;
    }

    public static class Builder<T> {

        private final QueryList<T> queryList;

        Builder() {
            queryList = new QueryList<>();
        }

        public QueryList<T> build() {
            return queryList;
        }

        @SafeVarargs
        public final Builder<T> query(Query<T>... queryList) {
            for (Query<T> query : queryList) {
                this.queryList.getValue().add(query);
            }
            return this;
        }

        public Builder<T> query(List<Query<T>> queryList) {
            this.queryList.getValue().addAll(queryList);
            return this;
        }

    }

}
