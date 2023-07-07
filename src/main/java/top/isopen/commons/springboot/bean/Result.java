package top.isopen.commons.springboot.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通用结果响应体
 * <p>
 * 序列化依赖 getter 函数，不依赖构造函数
 * <p>
 * 所以此处的注解只需使用 @Data
 * <p>
 * 不需使用 @NoArgsConstructor 和 @AllArgsConstructor
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/5/10 16:16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Result<T> extends BaseResponse {

    private static final long serialVersionUID = 2774312271443025267L;

    private static final int SUCCESS_CODE = 0;

    private int code;
    private String message;
    private String description;
    private T data;

    private static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static Result<?> ok() {
        return Result.builder()
                .code(SUCCESS_CODE)
                .build();
    }

    public static <T> Result<T> ok(T data) {
        return Result.<T>builder()
                .code(SUCCESS_CODE)
                .data(data)
                .build();
    }

    public static <T> Result<T> ok(String message, String description, T data) {
        return Result.<T>builder()
                .code(SUCCESS_CODE)
                .message(message)
                .description(description)
                .data(data)
                .build();
    }

    public static <T> Result<T> error(int code, String message, String description) {
        return Result.<T>builder()
                .code(code)
                .message(message)
                .description(description)
                .build();
    }

    private static class Builder<T> {

        private final Result<T> result;

        Builder() {
            this.result = new Result<>();
        }

        private Result<T> build() {
            return result;
        }

        private Builder<T> code(int code) {
            result.code = code;
            return this;
        }

        private Builder<T> message(String message) {
            result.message = message;
            return this;
        }

        private Builder<T> description(String description) {
            result.description = description;
            return this;
        }

        private Builder<T> data(T data) {
            result.data = data;
            return this;
        }

    }

}
