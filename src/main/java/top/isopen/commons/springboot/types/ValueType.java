package top.isopen.commons.springboot.types;

/**
 * 包装的值类型
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/18 10:18
 */
public class ValueType<T> {

    private final T value;

    protected ValueType(T value) {
        this.value = value;
    }

    protected T getValue() {
        return this.value;
    }

    protected static <T, R extends ValueType<T>> T resolve(R entity) {
        return entity != null ? entity.getValue() : null;
    }

}
