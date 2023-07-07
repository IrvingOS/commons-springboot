package top.isopen.commons.springboot.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 类型工具类
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/5/25 15:03
 */
public class TypeUtil {

    public static <T, R> Page<R> transform(Page<T> data, Function<T, R> mapper) {
        Page<R> result = Page.of(data.getCurrent(), data.getSize(), data.getTotal());
        result.setRecords(data.getRecords().stream().map(mapper).collect(Collectors.toList()));
        return result;
    }

    public static <T, R> List<R> transform(List<T> data, Function<T, R> mapper) {
        return data.stream().map(mapper).collect(Collectors.toList());
    }

}
