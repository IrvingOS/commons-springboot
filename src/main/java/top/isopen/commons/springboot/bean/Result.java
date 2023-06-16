package top.isopen.commons.springboot.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.isopen.commons.springboot.enums.HttpStatusEnum;

/**
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

    private int code = 0;
    private String message = "success";
    private T data;

    public static Result<?> ok() {
        return new Result<>();
    }

    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setData(data);
        return result;
    }

    public static <T> Result<T> ok(int code, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> ok(int code, String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error() {
        Result<T> result = new Result<>();
        result.setCode(HttpStatusEnum.INTERNAL_SERVER_ERROR.value());
        result.setMessage(HttpStatusEnum.INTERNAL_SERVER_ERROR.getReasonPhrase());
        return result;
    }

    public static <T> Result<T> error(int code) {
        Result<T> result = new Result<>();
        result.setCode(code);
        return result;
    }

    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> error(int code, String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

}
