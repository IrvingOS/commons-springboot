package top.isopen.commons.springboot.repository.support;

import java.io.Serializable;
import java.util.function.Function;

/**
 * FunctionalInterface 用来作为 repository 中的函数式操作
 * <p>
 * OnlineModel::getStatus {@link top.isopen.commons.springboot.model.OnlineModel#getStatus}
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 17:43
 */
@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {
}
